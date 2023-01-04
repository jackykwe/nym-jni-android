package com.kaeonx.nymandroidport.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import com.kaeonx.nymandroidport.R
import com.kaeonx.nymandroidport.jni.nymRun
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.services.NYM_RUN_PORT
import com.kaeonx.nymandroidport.services.NymWebSocketBoundService
import com.kaeonx.nymandroidport.utils.MSG_TYPE_CONNECT_TO_WEBSOCKET
import com.kaeonx.nymandroidport.utils.MSG_TYPE_WEBSOCKET_SUCCESSFULLY_CONNECTED
import com.kaeonx.nymandroidport.utils.PROGRESS_WEBSOCKET_CONNECTION_SUCCESSFUL_KEY
import com.kaeonx.nymandroidport.utils.PROGRESS_WEBSOCKET_TEARING_DOWN_KEY
import kotlinx.coroutines.runBlocking

private const val TAG = "nymRunWorker"
internal const val NYMRUNWORKER_CLIENT_ID_KEY = "clientId"

internal class NymRunWorker(appContext: Context, workerParams: WorkerParameters) :
    RemoteCoroutineWorker(appContext, workerParams) { // CoroutineWorker is recommended for Kotlin

    init {
        Log.e(TAG, "NymRunWorker instance created!")
    }

    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Creates an instance of ForegroundInfo, used to update the ongoing notification
    // Calls setForeground(createForegroundInfo(<msg>)) to replace the text in the ongoing
    // Notification with <msg>.
    // NB: Notifications from conversations are NOT handled here.
    private fun createForegroundInfo(notificationText: String, ongoing: Boolean): ForegroundInfo {
        val channelId =
            applicationContext.getString(R.string.nym_run_worker_notification_channel_id)
        val notificationTitle =
            applicationContext.getString(R.string.nym_run_worker_notification_title)
//        val cancelActionText =
//            applicationContext.getString(R.string.nym_run_worker_cancel_action_text)

//        val contentIntent = Intent(applicationContext, MainActivity::class.java)
//        val contentPendingIntent =
//            TaskStackBuilder.create(applicationContext).run {
//                // Add the intent, which inflates the back stack
//                addNextIntentWithParentStack(contentIntent)
//                // Get the PendingIntent containing the entire back stack
//                getPendingIntent(
//                    0,
//                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                )
//            }
        // This PendingIntent can be used to cancel the worker
//        val cancelWorkerPendingIntent =
//            WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        // Create a Notification channel
        // SDK_INT is always >=26 (Android O) as specified in manifest, so need to create a
        // notification channel
        createChannel()

        val notification = Notification.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle)
            .setTicker(notificationTitle)
            .setContentText(notificationText)
//            .setContentIntent(contentPendingIntent)  // launch activity on press
            .setSmallIcon(R.drawable.ic_baseline_cloud_sync_24)
            .setOngoing(ongoing)
            // Add the cancel action to the notification which allows user to cancel this worker
//            .addAction(
//                Notification.Action.Builder(
//                    Icon.createWithResource(
//                        applicationContext,
//                        android.R.drawable.ic_delete
//                    ),
//                    cancelActionText,
//                    cancelWorkerPendingIntent
//                )
//                    .setAuthenticationRequired(true).build()
//            )
            .build()
        return ForegroundInfo(
            42,
            notification
        )  // TODO: NON-DETERMINISTIC BEHAVIOUR, NOTIFICATION DOESN'T ALWAYS SHOW UP
    }

    // It's safe to call this repeatedly because creating an existing notification channel performs
    // no operation.
    private fun createChannel() {
        val channelId =
            applicationContext.getString(R.string.nym_run_worker_notification_channel_id)
        val channelName =
            applicationContext.getString(R.string.nym_run_worker_notification_channel_name)
        val channelImportance = NotificationManager.IMPORTANCE_HIGH
        val channelDescription =
            applicationContext.getString(R.string.nym_run_worker_notification_channel_description)

        val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
            description = channelDescription
        }
        // Register the channel with the system; you can't change the importance or other
        // notification behaviours after this
        notificationManager.createNotificationChannel(channel)
    }

    // runs on Dispatchers.Default by default
    override suspend fun doRemoteWork(): Result {
        // NB: When in a separate process, applicationContext is a different instance! (Clarified: Yes)
        Log.w(
            TAG,
            "[[[ SUBPROCESS PID = ${Process.myPid()} ]]] applicationContext is $applicationContext"
        )
//        bindToNymRunService()
        val clientId = inputData.getString(NYMRUNWORKER_CLIENT_ID_KEY) ?: return Result.failure()

        // Mark the Worker as important, and run even if app is closed
        setForegroundAsync(createForegroundInfo("Nym Run is executing in the background.", true))

        System.loadLibrary("nym_jni")
        topLevelInit(applicationContext.filesDir.absolutePath)
        nymRun(this, clientId, port = NYM_RUN_PORT)

        setForegroundAsync(createForegroundInfo("Nym Run has finished execution.", false))

        return Result.success()
    }

    ///////////////////
    // IPC TERRITORY //
    ///////////////////

    // Also defined in any component that wants to participate in IPC with services
    private val messenger: Messenger by lazy {
        Messenger(object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_TYPE_WEBSOCKET_SUCCESSFULLY_CONNECTED -> {
                        Log.i(
                            TAG,
                            "[NymRunWorker] SUCCESSFULLY received MSG_WEBSOCKET_SUCCESSFULLY_CONNECTED"
                        )
                        setProgressAsync(
                            Data.Builder()
                                .putBoolean(PROGRESS_WEBSOCKET_CONNECTION_SUCCESSFUL_KEY, true)
                                .build()
                        )
                        // TODO: REVERSE CHAIN
//                        val clientIdAndAddressPair = msg.obj as Bundle
//                        nymWebSocketClient.connectToWebSocket {
//                            runBlocking {  // TODO: I think web socket isn't active until this operation is complete, because this blocks the web socket's onOpen()
//                                keyStringValuePairRepository.put(
//                                    listOf(
//                                        RUNNING_CLIENT_ID_KSVP_KEY to clientIdAndAddressPair.getString(RUNNING_CLIENT_ID_KSVP_KEY)!!,
//                                        RUNNING_CLIENT_ADDRESS_KSVP_KEY to clientIdAndAddressPair.getString(RUNNING_CLIENT_ADDRESS_KSVP_KEY)!!
//                                    )
//                                )
//                            }
//                        }
                    }
                    else -> throw IllegalStateException("Unexpected IPC message type")
                }
            }
        })
    }


    /**
     * Messenger for communicating with services via IPC. If `null`, we are not currently bound to
     * that service. If not null, we are bound.
     */
//    private var nymRunServiceMessenger: Messenger? = null
    private var nymWebSocketBoundServiceMessenger: Messenger? = null

    /** Class for interacting with the main interface of the bound services. */
//    private lateinit var nymRunServiceConnection: ServiceConnection
    private lateinit var nymWebSocketBoundServiceConnection: ServiceConnection

//    private fun bindToNymRunService() {
//        Log.i(TAG, "bindToNymRunService: ATTEMPT STARTED")
//        nymRunServiceConnection = object : ServiceConnection {
//            override fun onServiceConnected(className: ComponentName, service: IBinder) {
//                nymRunServiceMessenger = Messenger(service)
//            }
//
//            override fun onServiceDisconnected(className: ComponentName) {
//                nymRunServiceMessenger = null
//            }
//        }
//
//        val successfullyBound = applicationContext.bindService(
//            Intent(applicationContext, NymRunService::class.java).apply {
//                putExtra(INTENT_FLAG_NOT_WORK_MANAGER_KEY, true)
//            },
//            nymRunServiceConnection,
//            Context.BIND_AUTO_CREATE
//        )
//        if (!successfullyBound) {
//            throw IllegalStateException("FAILED TO BIND")
////            applicationContext.unbindService(nymRunServiceConnection)
//        } else {
//            Log.i(TAG, "Successfully bound: nymRunServiceMessenger is $nymRunServiceMessenger")
//        }
//    }

    private fun bindToNymWebSocketBoundService() {
        nymWebSocketBoundServiceConnection = object : ServiceConnection {
            /** This is called when the connection with the NymWebSocketBoundService has been
             * established, giving us the object we can use to interact with the service. We are
             * communicating with the service using a Messenger, so here we get a client-side
             * representation of that from the raw IBinder object.
             */
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                nymWebSocketBoundServiceMessenger = Messenger(service)
                // communicate with NymWebSocketBoundService
                nymWebSocketBoundServiceMessenger?.send(
                    Message.obtain(null, MSG_TYPE_CONNECT_TO_WEBSOCKET).apply {
                        replyTo = messenger
                    }
                )
            }

            /** This is called when the connection with NymWebSocketBoundService has been
             * unexpectedly disconnected -- that is, its process crashed. (not part of normal
             * application flow)
             */
            override fun onServiceDisconnected(className: ComponentName) {
                nymWebSocketBoundServiceMessenger = null
            }
        }

        val successfullyBound = applicationContext.bindService(
            Intent(applicationContext, NymWebSocketBoundService::class.java),
            nymWebSocketBoundServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        if (!successfullyBound) {
            applicationContext.unbindService(nymWebSocketBoundServiceConnection)
        }
    }

    // Still accessible from Rust via JNI, despite private
    private fun afterSocketOpenedCalledFromRust() {
        bindToNymWebSocketBoundService()
    }

    // Still accessible from Rust via JNI, despite private
    // Need to unbind service, otherwise it keeps running and isn't destroyed by the Android OS.
    private fun beforeSocketClosedCalledFromRust() {
        Log.w(TAG, "SIGINT successfully called from Rust")
        if (nymWebSocketBoundServiceMessenger != null) {
            runBlocking {
                setProgress(
                    Data.Builder()
                        .putBoolean(PROGRESS_WEBSOCKET_TEARING_DOWN_KEY, true)
                        .build()
                )
                Log.w(TAG, "SETHA HA A AH AHA HA HAA")
                applicationContext.unbindService(nymWebSocketBoundServiceConnection)
            }
        }
    }
}
