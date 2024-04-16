package pt.isel.pc.jht.lecture20.futures.basic

import java.util.concurrent.*

val executor: ExecutorService = Executors.newCachedThreadPool()

fun main() {

	val mainTid = Thread.currentThread().threadId()
	println("[T$mainTid] :: STARTING ::")

	val futures = (0..15).map { num ->
		executor.submit<Int> {
			Thread.sleep(num * 1000L)
			val newNum = num * 2
			if (newNum % 10 == 8) {
				throw Exception("$newNum is unacceptable!")
			}
			newNum
		}
	}
	println("[T$mainTid] :: STARTED  ::")

	//close(executor) // See what changes if the executor is closed earlier

	println("[T$mainTid] :: RESULTS  ::")
	futures.forEachIndexed { idx, rf ->
		try {
			println("res[$idx]: ${ rf.get() }")
		} catch (ex: ExecutionException) {
			println("res[$idx]: ${ ex.cause?.message ?: "???" }")
		}
	}

	close(executor)

	println("[T$mainTid] :: DONE ::")
}

private fun close(executor: ExecutorService) {
	val mainTid = Thread.currentThread().threadId()
	println("[T$mainTid] :: SHUTDOWN ::")

	executor.shutdown()
	if (!executor.awaitTermination(20, TimeUnit.SECONDS)) {
		println("[T$mainTid] :: UNEXPECTED FAILURE ::")
	}
}
