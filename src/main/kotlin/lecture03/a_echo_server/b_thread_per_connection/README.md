Compile with: `kotlinc EchoServerThreadPerConnection.kt`

Execute with: `kotlin pt.isel.pc.jht.echoserver.EchoServerThreadPerConnectionKt`

Access with: `telnet -e ^Q localhost 8888`
   (where ^Q is CTRL-Q)

You will be able to interact with the server until you type `exit` (which terminates the current session) or `stop` (which stops the server).

With this version you will be able to start multiple sessions in parallel (using separate consoles), as the server launches a new thread for each client connection.

The limit to client connections is whatever the local machine supports. This is usually unreasonable in real systems, as resource consumption becomes out of control.
