package pt.isel.pc.jht.lecture20.futures.cancel

import java.util.concurrent.*

val executor: ExecutorService = Executors.newCachedThreadPool()

fun main() {

	val mainTid = Thread.currentThread().threadId()
	println("[T$mainTid] :: STARTING ::")
	
	val futures = (0..7).map { 
		executor.submit<Int> {
			val tid = Thread.currentThread().threadId()
			println("[T$tid] :: Task #$it ::")
			try {
				for (s in 0..7) {
					Thread.sleep(500)
				}
				it
			} catch (ex: InterruptedException) {
				println("[T$tid] :: Task #$it INTERRUPTED ::")
				throw ex
			}
		}
	}
	
	Thread.sleep(2000) // See what changes if sleeping 10000
	println("[T$mainTid] :: CANCELLING ::")
	futures.forEachIndexed { idx, fres ->
		println("[T$mainTid] cancelling $idx : ${ fres.cancel(true) }")
	}

	println("[T$mainTid] :: RESULTS ::")
	futures.forEachIndexed { idx, fres ->
		try {
			println("[T$mainTid] res[$idx] : ${ fres.get() }")
		} catch (ex: Throwable) {
			println("[T$mainTid] res[$idx] : EXCEPTION\n$ex")
		}
	}
	
	println("[T$mainTid] :: DONE ::")
	executor.shutdown()
}
