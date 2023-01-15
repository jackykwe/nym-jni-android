package com.kaeonx.nymchatprototype.services

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.room.withTransaction
import com.kaeonx.nymchatprototype.database.AppDatabase
import com.kaeonx.nymchatprototype.database.NYM_RUN_STATE_KSVP_KEY
import com.kaeonx.nymchatprototype.repositories.KeyStringValuePairRepository
import com.kaeonx.nymchatprototype.repositories.MessageRepository
import com.kaeonx.nymchatprototype.utils.NymMessageToSend
import com.kaeonx.nymchatprototype.utils.NymRunState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.exitProcess

private const val TAG = "nymWebSocketBoundService"

class NymWebSocketBoundService : Service() {
    override fun onCreate() {
        Log.i(TAG, "NymWebSocketBoundService created with pid ${Process.myPid()}")
        super.onCreate()
    }

    // Courtesy of <https://stackoverflow.com/a/63407811>
    private val supervisorJob by lazy { SupervisorJob() }
    private val serviceScope by lazy { CoroutineScope(Dispatchers.IO + supervisorJob) }

    private val appDatabaseInstance by lazy { AppDatabase.getInstance(applicationContext) }
    private val keyStringValuePairRepository by lazy {
        KeyStringValuePairRepository(
            appDatabaseInstance.keyStringValuePairDao()
        )
    }
    private val nymRunStateFlow by lazy {
        keyStringValuePairRepository.get(NYM_RUN_STATE_KSVP_KEY).map {
            NymRunState.valueOf(it ?: NymRunState.IDLE.name)
        }
    }
    private val messageRepository by lazy {
        MessageRepository(
            appDatabaseInstance.messageDao()
        )
    }
    private val earliestPendingSendMessageFlow by lazy {
        messageRepository.getEarliestPendingSendFromSelectedClient()
    }

    @Suppress("unused")  // unused variable but the flow is continuously collected
    private lateinit var _sendPendingSendMessageIfExists: Flow<Unit>

    // Boilerplate for bound services
    private val messenger by lazy {
        Messenger(object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_TYPE_CONNECT_TO_WEBSOCKET -> {
                        Log.w(TAG, "attempting to connect to websocket...")
                        // Instantiate and connect to web socket.
                        // NB: This code could've well resided in this class's onBind(), but I put
                        // it here because I want to send a message back to NymRunService when the
                        // socket's successfully opened
                        // DONE (clarify): Why doesn't Kotlin capture and preserve references? If I don't pass msg.replyTo as an additional argument, it gets deleted by GC and becomes null...?
                        //                 nymWebSocketClient.connectToWebSocket(msg.replyTo) { replyTo ->  // Messenger not passable, nuance with DIFFERENT PROCESSES
                        nymWebSocketClient.connectToWebSocket(
                            onSuccess = {
                                serviceScope.launch {
                                    Log.i(TAG, "writing to db... SOCKET OPEN")
                                    keyStringValuePairRepository.put(
                                        listOf(
                                            NYM_RUN_STATE_KSVP_KEY to NymRunState.SOCKET_OPEN.name
                                        )
                                    )
                                }
                            },
                            onReceive = { senderAddress, message, recvTs ->
                                serviceScope.launch {
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
                                    }
                                }
                            }
                        )
                    }
                    else -> throw IllegalStateException("Unexpected IPC message type")
                }
            }
        })
    }

    override fun onBind(p0: Intent?): IBinder? {
        // Must lateinit _sendPendingSendMessageIfExists instead of instantiating it on class load,
        // because the getApplicationContext() requires ContextWrapper to exist, but it only exists
        // after onCreate()
        // This only does work if the current NymRunState is SOCKET_OPEN, and is cancelled when
        // serviceScope is stopped (when supervisorJob is cancelled in onDestroy())
        _sendPendingSendMessageIfExists =
            combine(
                nymRunStateFlow,
                earliestPendingSendMessageFlow
            ) { nymRunState, earliestPendingSendMessage ->
                // TODO (clarify): Will this suspend function be cancelled if nymRunStateFlow changes value?
                if (nymRunState == NymRunState.SOCKET_OPEN && earliestPendingSendMessage != null) {
                    // successfully enqueued into web socket outgoing queue
                    val successfullyEnqueued = nymWebSocketClient.sendMessageThroughWebSocket(
                        NymMessageToSend.from(earliestPendingSendMessage).encodeToString()
                    )
                    if (successfullyEnqueued) {
                        // prepare to send next pending-send message
                        messageRepository.updateEarliestPendingSendById(earliestPendingSendMessage.id)
                    }
                }
            }.stateIn(serviceScope, SharingStarted.Eagerly, Unit)

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
