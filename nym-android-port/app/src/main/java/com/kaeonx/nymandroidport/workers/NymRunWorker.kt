package com.kaeonx.nymandroidport.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteCoroutineWorker
import com.kaeonx.nymandroidport.R
import com.kaeonx.nymandroidport.jni.nymRun
import com.kaeonx.nymandroidport.jni.topLevelInit

private const val TAG = "nymRunWorker"
internal const val NYMRUNWORKER_CLIENT_ID_KEY = "clientId"

class NymRunWorker(appContext: Context, workerParams: WorkerParameters) :
    RemoteCoroutineWorker(appContext, workerParams) { // CoroutineWorker is recommended for Kotlin

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
        // NB: When in a separate process, applicationContext is a different instance! (TODO: Clarify)
        Log.w(
            TAG,
            "[[[ SUBPROCESS PID = ${android.os.Process.myPid()} ]]] applicationContext is $applicationContext"
        )
        val clientId = inputData.getString(NYMRUNWORKER_CLIENT_ID_KEY) ?: return Result.failure()

        // Mark the Worker as important, and run even if app is closed
        setForegroundAsync(createForegroundInfo("Nym Run is executing in the background.", true))

        System.loadLibrary("nym_jni")
        topLevelInit()
        nymRun(applicationContext.filesDir.absolutePath, clientId)

        setForegroundAsync(createForegroundInfo("Nym Run has finished execution.", false))
        return Result.success()
    }
}
