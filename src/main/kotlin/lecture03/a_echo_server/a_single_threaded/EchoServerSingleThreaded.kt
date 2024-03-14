package pt.isel.pc.jht.echoserver

import java.io.IOException
import java.io.BufferedWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.logging.*

fun main() {
    EchoServerSingleThreaded(8888).run()
}

class EchoServerSingleThreaded(val port : Int) {

    companion object {
        private val logger: Logger = Logger.getLogger(this::class.java.name)
        private const val EXIT_CMD = "exit"
		private const val STOP_CMD = "stop"
    }

    fun run() {
        ServerSocket(port).use { serverSocket ->
            logger.info("server socket bound to %s:%d".format(serverSocket.inetAddress.hostAddress, port))
            acceptLoop(serverSocket)
        }
    }

    private fun acceptLoop(serverSocket: ServerSocket) {
        var clientId = 0
        while (true) {
            logger.info("Server socket waiting in thread %d".format(Thread.currentThread().threadId()))
            val clientSocket = serverSocket.accept()
            logger.info("New connection accepted from [%s]".format(clientSocket.inetAddress.hostAddress))
            attendClient(clientSocket, ++clientId)
        }
    }

    private fun attendClient(socket: Socket, clientId: Int) {
		logger.info("Attending client %d in thread %d".format(clientId, Thread.currentThread().threadId()))
        var lineNum = 0
        try {
            socket.use {
                socket.inputStream.bufferedReader().use { reader ->
                    socket.outputStream.bufferedWriter().use { writer ->
                        writer.writeLine(":: Connected as client %d ::", clientId)
                        while (true) {
							writer.write("type> ")
							writer.flush()
                            val line = reader.readLine()
                            if (line == null || line == EXIT_CMD) {
                                writer.writeLine(":: exit ::")
                                return
                            }
                            if (line == STOP_CMD) {
								logger.info("Stopping per client request")
                                writer.writeLine(":: stop ::")
								// Do not take this as an example!
                                System.exit(1)
                            }
                            logger.info("Echoing '%s'".format(line))
                            writer.writeLine("echo> %d: %s", lineNum++, line)
							writer.flush()
                        }
                    }
                }
            }
        } catch (e: IOException) {
            logger.warning("Connection error: %s".format(e.message))
        }
    }
	
	fun BufferedWriter.writeLine(format: String, vararg values: Any?) {
        write(String.format(format, *values))
        newLine()
        flush()
    }
}
