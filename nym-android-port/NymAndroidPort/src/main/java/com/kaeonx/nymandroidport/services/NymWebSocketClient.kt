package com.kaeonx.nymandroidport.services

import android.util.Log
import com.kaeonx.nymandroidport.database.NymBinaryMessageReceived
import com.kaeonx.nymandroidport.database.NymTextMessageReceived
import okhttp3.*
import okio.ByteString
import okio.EOFException
import java.net.ConnectException

private const val TAG = "webSocketClient"
internal const val NYM_RUN_PORT: UShort = 1977u

// Initial inspiration from <https://medium.com/@sthahemant1st/learn-how-to-use-web-socket-in-android-using-okhttp-b205709a2040>
class NymWebSocketClient private constructor() {
    /**
     * OkHttp performs best when you create a single OkHttpClient instance and reuse it for all of
     * your HTTP calls. This is because each client holds its own connection pool and thread pools.
     * Reusing connections and threads reduces latency and saves memory.
     * [(Source)](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/#okhttpclients-should-be-shared)
     */
    private val okHttpClient by lazy { OkHttpClient() }

    private lateinit var webSocketInstance: WebSocket
    internal fun connectToWebSocket(
        onSuccess: () -> Unit,  // write to KSVP
        onReceive: (senderAddress: String, message: String, recvTs: Long) -> Unit,
        backoffMillis: Long = 1L
    ) {
        webSocketInstance = okHttpClient.newWebSocket(
            request = Request.Builder()
                .url(
                    HttpUrl.Builder()
                        .scheme("http")
                        .host("localhost")
                        .port(NYM_RUN_PORT.toInt())
                        .build()
                )
                .build(),
            listener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.i(TAG, "Web socket to Nym Run successfully opened.")
                    onSuccess()
                }

                // DONE (clarify): Why is this sometimes called? (esp. first (few) message(s)); Nym-side bug: Nym changes type of websocket enum when sending ping (0x2: binary ) / text(0x1: text) messages
                // DONE (clarify): There is a first 10 bytes of "garbage", what are these?; Nym-side bug
                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    val now = System.currentTimeMillis()
//                    Log.w(TAG, "received BYTES message via onMessage(): >${bytes.substring(10).utf8()}<")
                    NymBinaryMessageReceived
                        .from(bytes.substring(10).utf8())
                        .let { onReceive(it.senderAddress, it.trueMessage, now) }
                }

                // DONE (clarify): Why is this sometimes called? (esp. first (few) message(s)); Nym-side bug: Nym changes type of websocket enum when sending ping (0x2: binary ) / text(0x1: text) messages
                override fun onMessage(webSocket: WebSocket, text: String) {
                    val now = System.currentTimeMillis()
//                    Log.w(TAG, "received TEXT message via onMessage(): >$text<")
                    NymTextMessageReceived
                        .from(text)
                        .let { onReceive(it.senderAddress, it.trueMessage, now) }
                }

                // DONE: Backoff with sleeping
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    when (t) {
                        is ConnectException -> {
                            // DONE: Non-deterministically fails. Because Nym opens up socket asynchronously!
                            Log.w(
                                TAG,
                                "Web socket to Nym Run closed due to non-deterministic error:"
                            )
//                            Log.w(TAG, t.stackTraceToString())
                            Log.w(TAG, "Retrying... (backing off: $backoffMillis ms)")
                            Thread.sleep(backoffMillis)
                            connectToWebSocket(
                                onSuccess,
                                onReceive,
                                backoffMillis * 2
                            )  // because Rust opens socket asynchronously
                        }
                        is EOFException -> {
                            Log.w(
                                TAG,
                                "Silenced EOFException due to socket closed from peer's side"
                            )
                        }  // OK, socket closed from peer
                        else -> {
                            Log.e(TAG, "Web socket to Nym Run closed due to unexpected error:")
                            Log.e(TAG, t.stackTraceToString())
                        }
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    Log.i(
                        TAG,
                        "Nym Run has indicated that no more messages will be sent. Closing web socket to Nym Run... (code: $code, reason: $reason"
                    )
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.i(
                        TAG,
                        "Web socket to Nym Run was gracefully closed. (code: $code, reason: $reason"
                    )
                }
            }
        )
    }

    /**
     * From the documentation of `WebSocket::send()`:
     * > This method returns true if the message was enqueued. Messages that would overflow the
     * > outgoing message buffer will be rejected and trigger a graceful shutdown of this web socket.
     * > This method returns false in that case, and in any other case where this web socket is
     * > closing, closed, or canceled.
     *
     * > This method returns immediately.
     */
    internal fun sendMessageThroughWebSocket(message: String) = webSocketInstance.send(message)

    companion object {
        @Volatile
        private var instance: NymWebSocketClient? = null

        fun getInstance(): NymWebSocketClient {
            return instance ?: synchronized(this) {
                if (instance == null) {
                    Log.w(TAG, "Requesting WebSocketClient instance")
                    instance = NymWebSocketClient()
                }
                instance!!
            }
        }
    }
}