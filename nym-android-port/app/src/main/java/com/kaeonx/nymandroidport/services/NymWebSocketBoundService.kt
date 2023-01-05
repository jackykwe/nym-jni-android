package com.kaeonx.nymandroidport.services

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.kaeonx.nymandroidport.database.AppDatabase
import com.kaeonx.nymandroidport.database.NYM_RUN_STATE_KSVP_KEY
import com.kaeonx.nymandroidport.repositories.KeyStringValuePairRepository
import com.kaeonx.nymandroidport.utils.NymRunState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

private const val TAG = "nymWebSocketBoundService"

class NymWebSocketBoundService : Service() {
    // Courtesy of <https://stackoverflow.com/a/63407811>
    private val supervisorJob by lazy { SupervisorJob() }
    private val serviceScope by lazy { CoroutineScope(Dispatchers.IO + supervisorJob) }

    private val keyStringValuePairRepository by lazy {
        KeyStringValuePairRepository(
            AppDatabase.getInstance(applicationContext).keyStringValuePairDao()
        )
    }

    // Boilerplate for bound services
    private val messenger by lazy {
        Messenger(object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_TYPE_CONNECT_TO_WEBSOCKET -> {
                        // Instantiate and connect to web socket.
                        // NB: This code could've well resided in this class's onBind(), but I put
                        // it here because I want to send a message back to NymRunService when the
                        // socket's successfully opened.
                        // TODO (clarify): Why doesn't Kotlin capture and preserve references? If I don't pass msg.replyTo as an additional argument, it gets deleted by GC and becomes null...?
                        //                 nymWebSocketClient.connectToWebSocket(msg.replyTo) { replyTo ->
                        nymWebSocketClient.connectToWebSocket {
                            serviceScope.launch {
                                keyStringValuePairRepository.put(
                                    listOf(
                                        NYM_RUN_STATE_KSVP_KEY to NymRunState.SOCKET_OPEN.name
                                    )
                                )
                            }
                        }
                    }
                    else -> throw IllegalStateException("Unexpected IPC message type")
                }
            }
        })
    }

    override fun onBind(p0: Intent?): IBinder? {
        return messenger.binder
    }

    // TODO (clarify): Will this leak memory if the service is destroyed? I doubt it: a new instance of NymWebSocketBoundService is created on new binds (assuming onDestroy() was called before, which will happen because we ever only have 0-1 bindings to this service)
    private val nymWebSocketClient by lazy { NymWebSocketClient.getInstance() }

    /**
     * The system invokes this method when the service is no longer used and is being destroyed.
     * Your service should implement this to clean up any resources such as threads, registered
     * listeners, or receivers. This is the last call that the service receives.
     *
     * When a bound service is unbound from all clients, the Android system destroys it.
     */
    override fun onDestroy() {
        super.onDestroy()
        supervisorJob.cancel()  // NB: Doesn't wait for completion of its child jobs.
        Log.w(TAG, "Exiting process 3")
        exitProcess(0)
    }
}
