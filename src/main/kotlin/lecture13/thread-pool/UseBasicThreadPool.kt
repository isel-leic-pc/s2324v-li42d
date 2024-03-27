package pt.isel.pc.jht.threadpools.basic

import java.util.concurrent.*

val executor = BasicThreadPool(
	Runtime.getRuntime().availableProcessors()
)

fun main() {

	val mainTid = Thread.currentThread().id
	println("[T$mainTid] :: STARTING ::")

	for (n in 0..15) {
		executor.execute {
			val tid = Thread.currentThread().id
			println("[T$tid] executing task #$n")		
		}
	}

	println("[T$mainTid] :: WAITING ::")
	Thread.sleep(3000)  // The thread pool is incomplete:
	                    //   it doesn't support shutdown

	println("[T$mainTid] :: DONE ::")
}
