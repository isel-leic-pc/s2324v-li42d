package pt.isel.pc.jht.lecture28.nio2.full_asynch

import java.io.Closeable
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousServerSocketChannel
import java.nio.channels.AsynchronousSocketChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.TimeUnit

class EchoServer(private val port: Int) : Closeable {

    private val serverSocket : AsynchronousServerSocketChannel =
        AsynchronousServerSocketChannel.open().bind(InetSocketAddress(port))

    private var numSessions = 0

    init {
        println("   :: EchoServer($port) ready ::")
    }

    fun start() {
        println("   :: EchoServer($port) running ::")

        accept()
    }

    override fun close() {
        println("   :: EchoServer($port) closing ::")
        serverSocket.close()
    }

    private fun accept() {
        val nextSession = numSessions++
        serverSocket.accept(
            nextSession,
            object : CompletionHandler<AsynchronousSocketChannel, Int> {
                override fun completed(session: AsynchronousSocketChannel, sessionId: Int) {
                    accept()
                    attend(sessionId, session)
                }

                override fun failed(exc: Throwable, sessionId: Int) {
                    val threadId = Thread.currentThread().threadId()

                    println("      -- [S$sessionId:T$threadId] accept failed: ${ exc.message } --")
                    exc.printStackTrace()
                    System.exit(1)
                }
            }
        )
    }

    private fun attend(sessionId: Int, session: AsynchronousSocketChannel) {
        val threadId = Thread.currentThread().threadId()
        val remoteAddr = session.remoteAddress

        println("      ++ [S$sessionId:T$threadId] connection from $remoteAddr ++")

        val buffer = ByteBuffer.allocate(8)

        read(sessionId, session, buffer)
    }

    private fun read(sessionId: Int, session: AsynchronousSocketChannel, buffer: ByteBuffer) {
        session.read(buffer, sessionId, object : CompletionHandler<Int, Int> {
            override fun completed(nb: Int, sessionId: Int) {
                val threadId = Thread.currentThread().threadId()

                if (nb == -1) {
                    println("      ++ [S$sessionId:T$threadId] end of session ++")
                    endSession(sessionId, session)
                    return
                }

                println("      ++ [S$sessionId:T$threadId] processing $nb byte(s)")

                TimeUnit.MILLISECONDS.sleep(500)   // Not really needed. Just pretending to work...

                buffer.flip()

                write(sessionId, session, buffer)
            }

            override fun failed(exc: Throwable, sessionId: Int) {
                printSessionError(sessionId, exc)
                endSession(sessionId, session)
            }
        })
    }

    private fun write(sessionId: Int, session: AsynchronousSocketChannel, buffer: ByteBuffer) {
        session.write(buffer, sessionId, object : CompletionHandler<Int, Int> {
            override fun completed(nb: Int, sessionId: Int) {
                val threadId = Thread.currentThread().threadId()

                println("      ++ [S$sessionId:T$threadId] $nb byte(s) written")

                buffer.clear()

                read(sessionId, session, buffer)
            }

            override fun failed(exc: Throwable, sessionId: Int) {
                printSessionError(sessionId, exc)
                endSession(sessionId, session)
            }
        })
    }

    private fun printSessionError(sessionId: Int, exc: Throwable) {
        val threadId = Thread.currentThread().threadId()
        println("      -- [S$sessionId:T$threadId] session error: ${ exc.message } --")
        exc.printStackTrace()
    }

    private fun endSession(sessionId: Int, session: AsynchronousSocketChannel) {
        val threadId = Thread.currentThread().threadId()
        println("      ++ [S$sessionId:T$threadId] closing ++")
        try { session.close() } catch (exc: Exception) {}
    }

}

const val DEFAULT_PORT = 8888

fun main(args: Array<String>) {
    val argPort = if (args.size > 0) args[0].toIntOrNull() else null
    val runPort = argPort ?: DEFAULT_PORT

    println("## STARTING IN PORT $runPort ##")

    val echoServer = EchoServer(runPort)
    println("## READY IN PORT $runPort ##")
    echoServer.start()
    readLine()   // Can you see why this is needed?

    println("## DONE ##")
}
