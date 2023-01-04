package com.kaeonx.nymandroidport.services

import android.util.Log
import androidx.work.multiprocess.RemoteWorkerService

private const val TAG = "nymRunService"
//internal const val MSG_WEBSOCKET_SUCCESSFULLY_CONNECTED = 2
//internal const val INTENT_FLAG_NOT_WORK_MANAGER_KEY = "intentFlagNotWorkManager"

class NymRunService : RemoteWorkerService() {
    init {
        Log.e(TAG, "NymRunService instance created!")
    }
//
//    // Also defined in any component that wants to participate in IPC with services
//    private val messenger: Messenger by lazy {
//        Messenger(object : Handler(Looper.getMainLooper()) {
//            override fun handleMessage(msg: Message) {
//                when (msg.what) {
//                    MSG_WEBSOCKET_SUCCESSFULLY_CONNECTED -> {
//                        Log.i(
//                            TAG,
//                            "[NymWebSocketBoundService] SUCCESSFULLY received MSG_CONNECT_TO_WEBSOCKET ${msg.obj}"
//                        )
//                        // TODO: REVERSE CHAIN
////                        val clientIdAndAddressPair = msg.obj as Bundle
////                        nymWebSocketClient.connectToWebSocket {
////                            runBlocking {  // TODO: I think web socket isn't active until this operation is complete, because this blocks the web socket's onOpen()
////                                keyStringValuePairRepository.put(
////                                    listOf(
////                                        RUNNING_CLIENT_ID_KSVP_KEY to clientIdAndAddressPair.getString(RUNNING_CLIENT_ID_KSVP_KEY)!!,
////                                        RUNNING_CLIENT_ADDRESS_KSVP_KEY to clientIdAndAddressPair.getString(RUNNING_CLIENT_ADDRESS_KSVP_KEY)!!
////                                    )
////                                )
////                            }
////                        }
//                    }
//                    else -> throw IllegalStateException("Unexpected IPC message type")
//                }
//            }
//        })
//    }

    // This does not work!! Manual binding and passing in a boolean extra in the Intent didn't work, though I might have done something wrong.
//    override fun onBind(intent: Intent): IBinder? {
//        return if (intent.getBooleanExtra(INTENT_FLAG_NOT_WORK_MANAGER_KEY, false)) {
//            // A custom binding was manually done for the purposes of IPC
//            messenger.binder
//        } else {
//            // WorkManager was the one who bound to NymRunService, so don't disrupt it's normal workings
//            super.onBind(intent)
//        }
//    }

    override fun onDestroy() {
        Log.e(TAG, "DESTROYED, bye bye")
    }
}