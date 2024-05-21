package pt.isel.pc.jht.lecture32.coroutines.scopes.globalScope

import kotlinx.coroutines.*

val tid : Long
    inline get() = Thread.currentThread().threadId()

suspend fun work(crname: String, n: Int) {
    repeat(n) {
        println("[T$tid,$crname] working...")
        delay(2000)
    }
    println("[T$tid,$crname] -- ending --")
}

// main can be declared as a suspend function, and it will run in a
// CoroutineContext, but it will be invoked without a CoroutineScope.
// There is a GlobalScope, but it leaves you out of structured
// concurrency

suspend fun main() {
    println("[T$tid,main/CR0] -- STARTING --")

    println("[T$tid,main/CR0] :: before first launch... ::")

    GlobalScope.launch {  // Don't use GlobalScope!
        work("CR1", 3)
    }

    GlobalScope.launch {  // Don't use GlobalScope!
        work("CR2", 8)
    }

    println("[T$tid,main/CR0] :: after first launch... ::")

    // A new CoroutineScope may be introduced with coroutineScope,
    // which reuses the current CoroutineContext.

    coroutineScope {

        println("[T$tid,main/CR0] >> in a new scope <<")

        launch {
            work("CR3", 4)
        }

        launch {
            work("CR4", 3)
        }

        println("[T$tid,main/CR0] >> end of the new scope <<")
    }
    println("[T$tid,main/CR0] >> OUT of the new scope <<")

    // CR1 and CR2 will be able to run while the coroutineScope waits
    // for the end of CR3 and CR4.  After the end of that scope,
    // there's nothing preventing the program to end, and the
    // following coroutines will likely not run to their end.

    println("[T$tid,main/CR0] :: before second launch... ::")

    // In this case, new coroutines may be launched directly.

    GlobalScope.launch {
        work("CR5", 3)
    }

    GlobalScope.launch {
        work("CR6", 5)
    }

    println("[T$tid,main/CR0] :: after second launch... ::")

    Thread.sleep(2500)  // To have a chance to observe that CR2 is likely still running

    println("[T$tid,main/CR0] -- ENDING --")
}
