package pt.isel.pc.jht.lecture20.futures.completable

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.*

val numCores = Runtime.getRuntime().availableProcessors()
val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(numCores)

fun delayedValue(value: Int, delay: Long) =
	CompletableFuture<Int>().apply {
		// after 'delay' milliseconds, complete the future with 'value'
		executor.schedule({ complete(value) }, delay, MILLISECONDS)
	}

fun main() {
	val future = delayedValue(1234, 3000)

	println(":: future created ::")

	future.thenAccept { value ->
		// will run only after the future is completed normally
		println(">> value produced: ${ value } <<")
	}

	println(":: callback registered ::")

	println(":: waiting... ::")

	executor.shutdown()
}
