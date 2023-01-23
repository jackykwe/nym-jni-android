package com.kaeonx.nymandroidport.services

import android.app.*
import android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
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
    private lateinit var _nymRunStateTearDownWatcher: StateFlow<NymRunState>  // current value is current state
    private lateinit var _sendEarliestPendingSendMessageIfExistsWatcher: StateFlow<Unit>

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
        _nymRunStateTearDownWatcher =
            keyStringValuePairRepository.get(NYM_RUN_STATE_KSVP_KEY).map {
                val currentNymRunState = NymRunState.valueOf(it ?: NymRunState.IDLE.name)

                if (currentNymRunState == NymRunState.TEARING_DOWN) {
                    Process.sendSignal(Process.myPid(), 2)
                }

                currentNymRunState
            }.stateIn(serviceIOScope, SharingStarted.Eagerly, NymRunState.IDLE)
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

    ///////////////////////////////////
    // METHODS CALLED ONLY FROM RUST //
    ///////////////////////////////////

    // Still accessible from Rust via JNI, despite private
    // Named as such because it makes sense to the programmer on the Rust side
    @Suppress("unused")
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

                    _sendEarliestPendingSendMessageIfExistsWatcher =
                        messageRepository.getEarliestPendingSendFromSelectedClient().map {
                            // Only does work if the current NymRunState is SOCKET_OPEN, and is cancelled when
                            // serviceScope is stopped (when supervisorJob is cancelled in onDestroy())
                            // TODO (clarify): Will the previous invocation of this function be cancelled if the flow changes value?
                            if (_nymRunStateTearDownWatcher.value == NymRunState.SOCKET_OPEN && it != null) {
                                // successfully enqueued into web socket outgoing queue
                                val successfullyEnqueued =
                                    nymWebSocketClient.sendMessageThroughWebSocket(
                                        messageLogId = it.message,
                                        message = "${it.message}${
                                            NymMessageToSend.from(it).encodeToString()
                                        }"
                                    )
                                if (successfullyEnqueued) {
                                    // prepare to send next pending-send message
                                    messageRepository.updateEarliestPendingSendById(it.id)
                                }
                            }
                        }.stateIn(serviceIOScope, SharingStarted.Eagerly, Unit)
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
            .setContentText(notificationText).setSmallIcon(R.drawable.ic_baseline_cloud_sync_24)
            .setOngoing(ongoing).setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE).build()
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