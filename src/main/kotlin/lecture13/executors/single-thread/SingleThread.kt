package pt.isel.pc.jht.executors.singlethread

import java.util.concurrent.*

val executor = Executors.newSingleThreadExecutor()



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
	executor.shutdown()

	if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
		println("[T$mainTid] :: timed out ::")
	} else {
		println("[T$mainTid] :: DONE ::")
	}
}
