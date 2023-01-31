package com.kaeonx.nymandroidport.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import com.kaeonx.nymandroidport.R
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.NYM_RUN_STATE_KSVP_KEY
import com.kaeonx.nymandroidport.jni.nymRun
import com.kaeonx.nymandroidport.jni.topLevelInit
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.utils.NymRunState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val TAG = "nymRunWorker"
internal const val NYMRUNWORKER_CLIENT_ID_KEY = "clientId"

internal class NymRunWorker(appContext: Context, workerParams: WorkerParameters) :
    RemoteCoroutineWorker(appContext, workerParams) { // CoroutineWorker is recommended for Kotlin

    private val keyStringValuePairRepository by lazy {
        KeyStringValuePairRepository(
            AppDatabase.getInstance(applicationContext).keyStringValuePairDao()
        )
    }

    private var tearingDownDueToError = false

    // runs on Dispatchers.Default by default
    override suspend fun doRemoteWork(): Result {
        Log.i(
            TAG,
            "NymRunWorker doing work in process ${Process.myPid()} ${Thread.currentThread().id}"
        )
        // NB: When in a separate process, applicationContext is a different instance! (Clarified: Yes)
        val clientId = inputData.getString(NYMRUNWORKER_CLIENT_ID_KEY) ?: return Result.failure()

        // Mark the Worker as important, and run even if app is closed
        setForegroundAsync(createForegroundInfo("Nym Run is executing in the background.", true))

        System.loadLibrary("nym_jni")
        topLevelInit(applicationContext.filesDir.absolutePath)
        val result: Result
        try {
            nymRun(this, clientId, port = NYM_RUN_PORT)
        } catch (e: Exception) {
            // JNI Exception thrown from Nym
            Log.e(TAG, e.stackTraceToString())
            tearingDownDueToError = true
            keyStringValuePairRepository.put(
                listOf(
                    NYM_RUN_STATE_KSVP_KEY to NymRunState.TEARING_DOWN.name
                )
            )
            unbindFromNymWebSocketBoundService()
        } finally {
            setForegroundAsync(createForegroundInfo("Nym Run has finished execution.", false))

            result = if (tearingDownDueToError) Result.failure() else Result.success()
            tearingDownDueToError = false  // consume flag
        }
        return result
    }

    ////////////////////////////
    // IPC WITH BOUND SERVICE //
    ////////////////////////////

    /**
     * Messenger for communicating with services via IPC. If `null`, we are not currently bound to
     * that service. If not null, we are bound.
     */
    private var nymWebSocketBoundServiceMessenger: Messenger? = null

    /** Class for interacting with the main interface of the bound services. */
    private lateinit var nymWebSocketBoundServiceConnection: ServiceConnection

    // Defined as a separate function to give it a better name in Kotlin
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
                Log.i(TAG, "Haiya doing IPC")
                nymWebSocketBoundServiceMessenger?.send(
                    Message.obtain(null, MSG_TYPE_CONNECT_TO_WEBSOCKET)
                )
            }

            /** This is called when the connection with NymWebSocketBoundService has been
             * unexpectedly disconnected -- that is, its process crashed. (not part of normal
             * application flow)
             *
             * Called when a connection to the Service has been lost. This typically happens when
             * the process hosting the service has crashed or been killed. This does not remove the
             * ServiceConnection itself -- this binding to the service will remain active, and you
             * will receive a call to onServiceConnected(ComponentName, IBinder) when the Service is
             * next running.
             *
             * DONE (clarify): Binding is either active or inactive (bound/unbound), and when it's
             *                 active, there can be either an active connection or disconnected. Yes. Two stage.
             */
            override fun onServiceDisconnected(className: ComponentName) {
                tearingDownDueToError = true
                Process.sendSignal(Process.myPid(), 2)
            }
        }

        // Regardless of the return value, you should later call unbindService(ServiceConnection)
        // to release the connection.
        val successfullyBound = applicationContext.bindService(
            Intent(applicationContext, NymWebSocketBoundService::class.java),
            nymWebSocketBoundServiceConnection,
            Context.BIND_AUTO_CREATE
        )
        // DONE (clarify): what happens on failure to bind? Do we need to unbind? (see a few lines up)  // Minor detail; no pts for prototype.
        if (!successfullyBound) {
            Log.e(TAG, "Did not successfully bind to NymWebSocketBoundService")
            applicationContext.unbindService(nymWebSocketBoundServiceConnection)  // asynchronous; service actually destroyed after this method returns
        }
    }

    // Still accessible from Rust via JNI, despite private
    // Named as such because it makes sense to the programmer on the Rust side
    @Suppress("unused")
    private fun afterSocketOpenedCalledFromRust() {
        Log.i(TAG, "afterSocketOpenedCalledFromRust() successfully called from Rust")
        bindToNymWebSocketBoundService()
    }

    // DONE: No need to check if (nymWebSocketBoundServiceMessenger == null), because of the FSM you created
    // Defined as a separate function to give it a better name in Kotlin
    private fun unbindFromNymWebSocketBoundService() {
        if (nymWebSocketBoundServiceMessenger == null) {
            // This method is called from doRemoteWork() due to crashing of nymRun() (Rust code) before connection is successfully established
            // I.e. Failure during setup, perhaps due to
            //     client_core::client::base_client: Could not authenticate and start up the gateway connection - Gateway returned an error response - There is already an open connection to this client
            //     java.lang.RuntimeException: client-core error: Gateway client error: Gateway returned an error response - There is already an open connection to this client
            return
        }
        applicationContext.unbindService(nymWebSocketBoundServiceConnection)  // asynchronous; service actually destroyed after this method returns
    }

    // Still accessible from Rust via JNI, despite private
    // Need to unbind service, otherwise it keeps running and isn't destroyed by the Android OS.
    @Suppress("unused")
    private fun beforeSocketClosedCalledFromRust() {
        Log.w(TAG, "beforeSocketClosedCalledFromRust() successfully called from Rust")
        unbindFromNymWebSocketBoundService()
    }

    //////////////////////////////////////////////////
    // FOREGROUND SERVICE NOTIFICATIONS BOILERPLATE //
    //////////////////////////////////////////////////

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

        // Create a Notification channel
        // SDK_INT is always >=26 (Android O) as specified in manifest, so need to create a
        // notification channel
        createChannel()

        val notification = Notification.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle)
            .setTicker(notificationTitle)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_baseline_cloud_sync_24)
            .setOngoing(ongoing)
            .build()
        return ForegroundInfo(
            42,
            notification
        )  // TODO (Clarify): NON-DETERMINISTIC BEHAVIOUR, NOTIFICATION DOESN'T ALWAYS SHOW UP: Could be because I'm sending notifications too frequently, sometimes I see "notifications silenced" (something to this effect) in Logcat.
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
}
