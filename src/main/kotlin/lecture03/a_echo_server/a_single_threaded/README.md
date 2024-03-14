Compile with: `kotlinc EchoServerSingleThreaded.kt`

Execute with: `kotlin pt.isel.pc.jht.echoserver.EchoServerSingleThreadedKt`

Access with: `telnet -e ^Q localhost 8888`
   (where ^Q is CTRL-Q)

You will be able to interact with the server until you type `exit` (which terminates the current session) or `stop` (which stops the server). However, you will not be able two start to sessions in parallel (using two separate consoles), as the server is supported by a single thread of execution.
