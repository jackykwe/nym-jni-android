package com.kaeonx.nymandroidport.services

import android.annotation.SuppressLint
import android.app.*
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.*
import android.telephony.*
import android.util.Log
import androidx.annotation.Keep
import androidx.room.withTransaction
import com.kaeonx.nymandroidport.R
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.NYM_RUN_STATE_KSVP_KEY
import com.kaeonx.nymandroidport.database.RUNNING_CLIENT_ADDRESS_KSVP_KEY
import com.kaeonx.nymandroidport.jni.nymRun
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.repositories.MessageRepository
import com.kaeonx.nymandroidport.utils.NymMessageToSend
import com.kaeonx.nymandroidport.utils.NymRunState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.exitProcess

private const val TAG = "nymRunForegroundService"
internal const val NYMRUN_FOREGROUND_SERVICE_CLIENT_ID_EXTRA_KEY = "clientId"
private const val ONGOING_NOTIFICATION_ID = 42

class NymRunForegroundService : Service() {

    private var nymRunExecutingThread: Thread? = null

    // Courtesy of <https://stackoverflow.com/a/63407811>
    private val supervisorJob by lazy { SupervisorJob() }
    private val serviceIOScope by lazy { CoroutineScope(Dispatchers.IO + supervisorJob) }

    private val appDatabaseInstance by lazy { AppDatabase.getInstance(applicationContext) }
    private val keyStringValuePairRepository by lazy {
        KeyStringValuePairRepository(appDatabaseInstance.keyStringValuePairDao())
    }
    private val messageRepository by lazy {
        MessageRepository(appDatabaseInstance.messageDao())
    }

    // These flows are hot and fire on every change
    private val nymRunState by lazy {
        keyStringValuePairRepository.get(NYM_RUN_STATE_KSVP_KEY).map {
            NymRunState.valueOf(it ?: NymRunState.IDLE.name)
        }.stateIn(serviceIOScope, SharingStarted.Eagerly, NymRunState.IDLE)
    }

    // DONE (clarify): Will this leak memory if the service is destroyed?
    // Nope. When this service is destroyed, the process ends and its resources are all relinquished.
    private val nymWebSocketClient by lazy { NymWebSocketClient.getInstance() }

    /**
     * The system invokes this method to perform one-time setup procedures when the service is
     * initially created (before it calls either onStartCommand() or onBind()). If the service is
     * already running, this method is not called.
     */
    override fun onCreate() {
        Log.w(TAG, "NymRunForegroundService created with pid ${Process.myPid()}")
        serviceIOScope.launch {
            nymRunState.collect {
                if (it == NymRunState.TEARING_DOWN) {
                    Process.sendSignal(Process.myPid(), 2)
                }
            }
        }
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }


    // Called on Service's main thread (NB: this service resides in a separate process)
    // Leave the main thread free to handle incoming onStartCommands (to handle the unlikely case
    // where the system kills NymRunForegroundService and restarts it again)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.w(TAG, "NymRunForegroundService: onStartCommand()")

        // If null, no active execution, so onStartCommand() can start the Nym Run service.
        // If not null, two cases:
        // - If caused by user (not possible due to FSM) ignore.
        // - If caused by killing and restarting of process (due to system resource constraint),
        //   need to gracefully recover as seen in onDestroy().
        //   TODO: Minor (unlikely to happen unless system is really stressed, since non-foreground services are killed first
        if (nymRunExecutingThread != null) {
            Log.e(
                TAG,
                "Tried to onStartCommand() NymRunForegroundService when there is already a previous execution thread."
            )
            return START_REDELIVER_INTENT
        }

        // Since this foreground service is started by startForegroundService(), it must call its
        // startForeground() method within five seconds of starting (typically in onStartCommand()).
        startForeground(
            ONGOING_NOTIFICATION_ID,  // Notification ID cannot be 0.
            createNotification("Nym Run is executing in the background.")
        )

        nymRunExecutingThread = object : Thread() {
            override fun run() {
                // NB: When in a separate process, applicationContext is a different instance! (Clarified: Yes)

                if (intent == null) {
                    throw IllegalStateException("Started NymRunForegroundService without any intent")
                }

                val clientId =
                    intent.getStringExtra(NYMRUN_FOREGROUND_SERVICE_CLIENT_ID_EXTRA_KEY)!!

                System.loadLibrary("nym_jni")
                topLevelInit(applicationContext.filesDir.absolutePath)
                try {
                    nymRun(this@NymRunForegroundService, clientId, port = NYM_RUN_PORT)
                } catch (e: Exception) {
                    Log.e(TAG, "Exception raised from Nym Run method")
                    Log.e(TAG, e.stackTraceToString())  // JNI Exception thrown from Nym
                    serviceIOScope.launch {
                        keyStringValuePairRepository.put(
                            listOf(NYM_RUN_STATE_KSVP_KEY to NymRunState.TEARING_DOWN.name)
                        )
                    }
                    nymWebSocketClient.close()
                } finally {
                    // triggers shutdown of this service (NymRunForegroundService)
                    // If the startId doesn't match the latest startId of the most recent
                    // onStartCommand() call, this service is not stopped.
                    stopSelf(startId)
                    nymRunExecutingThread = null
                    // set executing thread to null on completion
                }
            }
        }
        nymRunExecutingThread!!.start()

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Returning null as this ForegroundService does not support binding
        return null
    }

    /**
     * If the service is started and is long-running, the system lowers its position in the list of
     * background tasks over time, and the service becomes highly susceptible to killing—if your
     * service is started, you must design it to gracefully handle restarts by the system. If the
     * system kills your service, it restarts it as soon as resources become available, but this
     * also depends on the value that you return from onStartCommand().
     */
    override fun onDestroy() {
        supervisorJob.cancel()  // NB: Doesn't wait for completion of its child jobs.
        runBlocking {
            keyStringValuePairRepository.put(
                listOf(NYM_RUN_STATE_KSVP_KEY to NymRunState.IDLE.name)
            )
        }
        Log.w(TAG, "Exiting NymRunForegroundService process")
        exitProcess(0)
    }

    ////////////////////////
    // BATTERY STATISTICS //
    ////////////////////////
    private fun getBatteryStatistics(): String {
        val currentBatteryStatus: Intent? =
            IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
                applicationContext.registerReceiver(null, intentFilter)
            }
        val currentBatteryLevel = currentBatteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
        val isCharging =
            (currentBatteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1).let {
                it == BatteryManager.BATTERY_STATUS_CHARGING || it == BatteryManager.BATTERY_STATUS_FULL
            }
        val chargeMethod =
            (currentBatteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1).let {
                when (it) {
                    BatteryManager.BATTERY_PLUGGED_USB -> "USB"
                    BatteryManager.BATTERY_PLUGGED_AC -> "AC"
                    -1 -> "not_charging"
                    else -> "unknown"
                }
            }

        val powerManager =
            applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager

        val isIgnoringBatteryOptimizations =
            powerManager.isIgnoringBatteryOptimizations(applicationContext.packageName)  // the one we're interested in

        val isInteractive = powerManager.isInteractive
        val isDeviceLightIdleMode =
            if (Build.VERSION.SDK_INT >= 33) powerManager.isDeviceLightIdleMode else null
        val isDeviceIdleMode = powerManager.isDeviceIdleMode

        val isPowerSaveMode = powerManager.isPowerSaveMode
        val isLowPowerStandbyEnabled =
            if (Build.VERSION.SDK_INT >= 33) powerManager.isLowPowerStandbyEnabled else null
        val isSustainedPerformanceModeSupported =
            powerManager.isSustainedPerformanceModeSupported  // constant per device

        return "Battery Statistic" +
                " | charge=$currentBatteryLevel% isCharging=$isCharging chargeMethod=$chargeMethod" +
                " | isIgnoringBatteryOptimizations=$isIgnoringBatteryOptimizations" +
                " | isInteractive=$isInteractive isDeviceLightIdleMode=$isDeviceLightIdleMode isDeviceIdleMode=$isDeviceIdleMode" +
                " | isPowerSaveMode=$isPowerSaveMode isLowPowerStandbyEnabled=$isLowPowerStandbyEnabled isSustainedPerformanceModeSupported=$isSustainedPerformanceModeSupported"
    }

    ////////////////////////
    // NETWORK STATISTICS //
    ////////////////////////
    /**
     * From TelephonyManager docs:
     * TelephonyManager is intended for use on devices that implement FEATURE_TELEPHONY. On devices
     * that do not implement this feature, the behavior is not reliable.
     * Requires the PackageManager#FEATURE_TELEPHONY feature which can be detected using
     * PackageManager.hasSystemFeature(String).
     */
    private val telephonyManager by lazy {
        applicationContext.packageManager.run {
            if (!hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                throw IllegalStateException("Attempting to use TelephoneManager when device doesn't support FEATURE_TELEPHONY")
            }
            if (!hasSystemFeature(PackageManager.FEATURE_TELEPHONY_DATA)) {
                throw IllegalStateException("Attempting to use TelephoneManager when device doesn't support FEATURE_TELEPHONY_DATA")
            }
            if (!hasSystemFeature(PackageManager.FEATURE_TELEPHONY_RADIO_ACCESS)) {
                throw IllegalStateException("Attempting to use TelephoneManager when device doesn't support FEATURE_TELEPHONY_RADIO_ACCESS")
            }
        }
        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
//    private val wifiManager by lazy { getSystemService(Context.WIFI_SERVICE) as WifiManager }
    private val connectivityManager by lazy {
        getSystemService(ConnectivityManager::class.java) as ConnectivityManager
    }

    private val networkRequest by lazy {
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }
    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    Log.w(TAG, "Network available! Now on WiFi.")
                } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                    Log.w(TAG, "Network available! Now on cellular.")
                } else {
                    Log.e(
                        TAG,
                        "Network available! Now on a network that is neither WiFi nor cellular."
                    )
                }
            }

            // Network capabilities have changed for the network
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.w(TAG, "NetworkCapabilities changed! Now on WiFi.")
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.w(TAG, "NetworkCapabilities changed! Now on cellular.")
                } else {
                    Log.e(
                        TAG,
                        "NetworkCapabilities changed! Now on a network that is neither WiFi nor cellular."
                    )
                }
            }

            // lost network connection
            override fun onLost(network: Network) {
                super.onLost(network)
                val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
                if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
                    Log.e(TAG, "Network lost! Previous on WiFi.")
                } else if (networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
                    Log.e(TAG, "Network lost! Previous on cellular.")
                } else {
                    Log.e(
                        TAG,
                        "Network lost! Previous on a network that is neither WiFi nor cellular."
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNetworkStatistics(): String {
        val activeNetworkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return if (activeNetworkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true) {
            val transportInfo = activeNetworkCapabilities.transportInfo as WifiInfo

            val ssid = transportInfo.ssid
//            val wifiStandard = when (val ws = transportInfo.wifiStandard) {
//                ScanResult.WIFI_STANDARD_UNKNOWN -> "unknown"
//                ScanResult.WIFI_STANDARD_LEGACY -> "802.11a/b/g"
//                ScanResult.WIFI_STANDARD_11N -> "802.11n"
//                ScanResult.WIFI_STANDARD_11AC -> "802.11ac"
//                ScanResult.WIFI_STANDARD_11AX -> "802.11ax"
//                ScanResult.WIFI_STANDARD_11AD -> "802.11ad"
//                ScanResult.WIFI_STANDARD_11BE -> "802.11be"
//                else -> throw IllegalStateException("Impossible Wifi Standard found: $ws (int value)")
//            }
//            val linkSpeedMbps = transportInfo.linkSpeed
            val rxLinkSpeedMbps = transportInfo.rxLinkSpeedMbps
            val txLinkSpeedMbps = transportInfo.txLinkSpeedMbps
            val dBmRssi = transportInfo.rssi  // raw RSSI in dBm
//            // the RSSI signal quality rating, in the range [0, getMaxSignalLevel()], where 0 is the lowest (worst signal) RSSI rating and getMaxSignalLevel() is the highest (best signal) RSSI rating. Value is 0 or greater
//            val signalLevel = wifiManager.calculateSignalLevel(dBmRssi)
//            val maxSignalLevel = wifiManager.maxSignalLevel

//            "Network Statistic (WiFi) | ssid='$ssid' standard=$wifiStandard lsMbps=$linkSpeedMbps rxLsMbps=$rxLinkSpeedMbps txLsMbps=$txLinkSpeedMbps dBmRssi=$dBmRssi signalLevel=$signalLevel maxSignalLevel=$maxSignalLevel"
            "Network Statistic (WiFi) | ssid='$ssid' rxLsMbps=$rxLinkSpeedMbps txLsMbps=$txLinkSpeedMbps dBmRssi=$dBmRssi"
        } else if (activeNetworkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true) {
            val dataState = when (val ds = telephonyManager.dataState) {
                TelephonyManager.DATA_DISCONNECTED -> "disconnected"
                TelephonyManager.DATA_CONNECTING -> "connecting"
                TelephonyManager.DATA_CONNECTED -> "connected"
                TelephonyManager.DATA_SUSPENDED -> "suspended"
                TelephonyManager.DATA_DISCONNECTING -> "disconnecting"
                TelephonyManager.DATA_HANDOVER_IN_PROGRESS -> "handover_in_progress"
                else -> throw IllegalStateException("Impossible telephony data state found: $ds (int value)")
            }
            val dataNetworkType = when (val dnt = telephonyManager.dataNetworkType) {
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> "unknown"
                TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS"
                TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE"
                TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS"
                TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA"
                TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA"
                TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA"
                TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA:IS95A/IS95B"
                TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDOrev0"
                TelephonyManager.NETWORK_TYPE_EVDO_A -> "EDVOrevA"
                TelephonyManager.NETWORK_TYPE_EVDO_B -> "EDVOrevB"
                TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT"
                TelephonyManager.NETWORK_TYPE_IDEN -> "iDen"
                TelephonyManager.NETWORK_TYPE_LTE -> "LTE/5G:NSA"
                TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD"
                TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+"
                TelephonyManager.NETWORK_TYPE_NR -> "5G:SA"
                else -> throw IllegalStateException("Impossible telephony data network type found: $dnt (int value)")
            }

            // signalStength(): Due to power saving this information may not always be current.
            val logFragments = ArrayList<String>()
            val signalStrengths = telephonyManager.signalStrength?.cellSignalStrengths
            if (signalStrengths != null) {
                for (signalStrength in signalStrengths) {
                    when (signalStrength) {
                        is CellSignalStrengthCdma -> {
                            // Signal strength related information.
                            logFragments.add("CDMA_dBm=${signalStrength.dbm}")
                        }
                        is CellSignalStrengthGsm -> {
                            // GSM signal strength related information.
                            logFragments.add("Gsm_dBm=${signalStrength.dbm}")
                        }
                        is CellSignalStrengthLte -> {
                            // LTE signal strength related information.
                            logFragments.add("LTE_dBm=${signalStrength.dbm}")
                        }
                        is CellSignalStrengthNr -> {
                            // 5G NR signal strength related information.
                            logFragments.add("5G:NR_dBm=${signalStrength.dbm}")
                        }
                        is CellSignalStrengthTdscdma -> {
                            // Tdscdma signal strength related information.
                            logFragments.add("Tdscdma_dBm=${signalStrength.dbm}")
                        }
                        is CellSignalStrengthWcdma -> {
                            // Wcdma signal strength related information.
                            logFragments.add("Wcdma_dBm=${signalStrength.dbm}")
                        }
                        else -> throw IllegalStateException("Impossible signal strength type found")
                    }
                }
            }
            "Network Statistic (Cellular) | dataState=$dataState dataNetworkType=$dataNetworkType |* ${
                logFragments.joinToString(
                    " "
                )
            }"
        } else {
            "Network Statistic (Cellular) | Device on neither WiFi or cellular network."
        }
    }


    ///////////////////////////////////
    // METHODS CALLED ONLY FROM RUST //
    ///////////////////////////////////

    // Still accessible from Rust via JNI, despite private
    // Named as such because it makes sense to the programmer on the Rust side
    @Suppress("unused")
    @Keep  // prevents minify from modifying function name and breaking JNI
    private fun afterSocketOpenedCalledFromRust() {
        Log.i(TAG, "afterSocketOpenedCalledFromRust() successfully called from Rust")

        nymWebSocketClient.connectToWebSocket(
            onSuccessfulConnection = {
                serviceIOScope.launch {
                    keyStringValuePairRepository.put(
                        listOf(
                            NYM_RUN_STATE_KSVP_KEY to NymRunState.SOCKET_OPEN.name
                        )
                    )

                    messageRepository.getEarliestPendingSendFromSelectedClient().collect {
                        // Only does work if the current NymRunState is SOCKET_OPEN, and is cancelled when
                        // serviceScope is stopped (when supervisorJob is cancelled in onDestroy())
                        // Done: Observe hot flow instead of doing map; map should be pure function.
                        // Done (clarify): Will the previous invocation of this function be cancelled if the flow changes value? Nope. If want that behaviour, use collectLatest().
                        if (nymRunState.value == NymRunState.SOCKET_OPEN && it != null) {
                            // successfully enqueued into web socket outgoing queue
                            val successfullyEnqueued =
                                nymWebSocketClient.sendMessageThroughWebSocket(
                                    messageLogId = it.message,
                                    message = "${it.message}${
                                        NymMessageToSend.from(it).encodeToString()
                                    }",
                                    getBatteryStatistics = { getBatteryStatistics() },
                                    getNetworkStatistics = { getNetworkStatistics() }
                                )
                            if (successfullyEnqueued) {
                                // prepare to send next pending-send message
                                messageRepository.updateEarliestPendingSendById(it.id)
                            }
                        }
                    }
                }

                serviceIOScope.launch(Dispatchers.IO) {
                    var messageLogId = 0UL
                    while (true) {
                        val selectedClientAddress =
                            keyStringValuePairRepository.getLatest(
                                RUNNING_CLIENT_ADDRESS_KSVP_KEY
                            )!!
                        val tM = SystemClock.elapsedRealtimeNanos()  // Monotonic
                        Log.i(TAG, "tK=0 l=KotlinCreation tM=$tM mId=$messageLogId")

                        messageRepository.sendMessageFromSelectedClient(
                            selectedClientAddress,
                            messageLogId.toString()
//                            System.currentTimeMillis().toString()
                        )
                        messageLogId += 1UL
                        delay(1_000L)
                    }
                }
            },
            onReceiveMessage =
            { senderAddress, message, recvTs ->
                serviceIOScope.launch {
                    appDatabaseInstance.run {
                        withTransaction {
                            contactDao().insertOrIgnoreForSelectedClient(
                                newContactAddress = senderAddress
                            )
                            messageDao().insertToSelectedClient(
                                fromAddress = senderAddress,
                                message = "$message.$recvTs"
                            )
                        }
                        val tM = SystemClock.elapsedRealtimeNanos()  // Monotonic
                        Log.i(TAG, "tK=9 l=KotlinDelivered tM=$tM mId=$message")
                    }
                }
            },
            onWebSocketUnexpectedlyClosed =
            {
                serviceIOScope.launch {
                    keyStringValuePairRepository.put(
                        listOf(NYM_RUN_STATE_KSVP_KEY to NymRunState.TEARING_DOWN.name)
                    )
                }
            }
        )
    }

    // Still accessible from Rust via JNI, despite private
    @Suppress("unused")
    @Keep  // prevents minify from modifying function name and breaking JNI
    private fun beforeSocketClosedCalledFromRust() {
        Log.w(TAG, "beforeSocketClosedCalledFromRust() successfully called from Rust")
        nymWebSocketClient.close()
    }

    //////////////////////////////////////////////////
    // FOREGROUND SERVICE NOTIFICATIONS BOILERPLATE //
    //////////////////////////////////////////////////

    private val notificationManager by lazy {
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    // Creates an instance of ForegroundInfo, used to update the ongoing notification
    // Calls setForeground(createForegroundInfo(<msg>)) to replace the text in the ongoing
    // Notification with <msg>.
    // NB: Notifications from conversations are NOT handled here.
    private fun createNotification(
        notificationText: String, ongoing: Boolean = true
    ): Notification {
        val channelId =
            applicationContext.getString(R.string.nym_run_foreground_service_notification_channel_id)
        val notificationTitle =
            applicationContext.getString(R.string.nym_run_foreground_service_notification_title)

        // Create a Notification channel
        // SDK_INT is always >=26 (Android O) as specified in manifest, so need to create a
        // notification channel
        createChannel()

        return Notification.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle).setTicker(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_baseline_cloud_sync_24)
            .setOngoing(ongoing).setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        // TODO: Is non-deterministic behaviour of notifications still present?
        // DONE (Clarify): Non-deterministic behaviour, notification doesn't always show up:
        // Could be because I'm sending notifications too frequently, sometimes I see
        // "notifications silenced" (something to this effect) in Logcat. Yes indeed.
    }

    // It's safe to call this repeatedly because creating an existing notification channel is a no-op.
    private fun createChannel() {
        val channelId =
            applicationContext.getString(R.string.nym_run_foreground_service_notification_channel_id)
        val channelName =
            applicationContext.getString(R.string.nym_run_foreground_service_notification_channel_name)
        val channelImportance = NotificationManager.IMPORTANCE_HIGH
        val channelDescription =
            applicationContext.getString(R.string.nym_run_foreground_service_notification_channel_description)

        val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
            description = channelDescription
        }
        // Register the channel with the system; you can't change the importance or other
        // notification behaviours after this
        notificationManager.createNotificationChannel(channel)
    }
}