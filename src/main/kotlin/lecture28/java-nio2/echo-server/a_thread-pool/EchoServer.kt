package pt.isel.pc.jht.lecture28.nio2.thread_pool

import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/* A basic multi-threaded Echo server over TCP using a thread pool.
   Processes one connection per thread, which either severely limits
   the maximum number of simultaneous clients or forces the thread
   pool to become unreasonably large, wasting substantial resources.
 */

class EchoServer(private val port: Int) : Closeable {

    private val serverSocket : ServerSocketChannel =
        ServerSocketChannel.open().bind(InetSocketAddress(port))

    private val executor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    private var numSessions = 0

    init {
        println("   :: EchoServer($port) ready ::")
    }

    fun run() {
        println("   :: EchoServer($port) running ::")

        while (true) {
            val session : SocketChannel = serverSocket.accept()
            val sessionId = ++numSessions

            executor.execute {
                attend(sessionId, session)
            }
        }
    }

    override fun close() {
        println("   :: EchoServer($port) closing ::")
        serverSocket.close()
    }

    private fun attend(sessionId: Int, session: SocketChannel) {
        val threadId = Thread.currentThread().threadId()
        val remoteAddr = session.remoteAddress

        println("      ++ [S$sessionId:T$threadId] connection from $remoteAddr ++")

        val buffer = ByteBuffer.allocate(8)

        try {
            var nb : Int
            while (true) {
                nb = session.read(buffer)

                if (nb == -1) {
                    println("      ++ [S$sessionId:T$threadId] end of session ++")
                    return
                }

                println("      ++ [S$sessionId:T$threadId] processing $nb byte(s)")

                TimeUnit.MILLISECONDS.sleep(500)   // Not really needed. Just pretending to work...

                buffer.flip()

                session.write(buffer)

                buffer.clear()
            }
        } catch (exc: Exception) {
            println("      ++ [S$sessionId:T$threadId] session error: ${ exc.message } ++")
            exc.printStackTrace()
        } finally {
            println("      ++ [S$sessionId:T$threadId] closing ++")
            try { session.close() } catch (exc: Exception) {}
        }
    }
}

const val DEFAULT_PORT = 8888

fun main(args: Array<String>) {
    val argPort = if (args.size > 0) args[0].toIntOrNull() else null
    val runPort = argPort ?: DEFAULT_PORT

    println("## STARTING IN PORT $runPort ##")

    EchoServer(runPort).use { echoServer ->
        println("## READY IN PORT $runPort ##")
        echoServer.run()
    }

    println("## DONE ##")
}
