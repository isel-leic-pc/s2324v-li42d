package pt.isel.pc.jht.lecture29.coroutines.delay

import kotlinx.coroutines.*

val tid : Long
	inline get() = Thread.currentThread().threadId()

fun main() {
	runBlocking {

		launch { 
			altDelay(1500)
			println("++ [T$tid] Message 3 ++")
			altDelay(1000)
			println("++ [T$tid] Message 5 ++")
		}

		launch {
			altDelay(1000)
			println("++ [T$tid] Message 2 ++")
			altDelay(1000)
			println("++ [T$tid] Message 4 ++")
		}

		launch {
			altDelay(500)
			println("++ [T$tid] Message 1 ++")
			altDelay(2500)
			println("++ [T$tid] Message 6 ++")
		}

		println(":: [T$tid] STARTING ::")
	}

	altDelayTerminate()
}
