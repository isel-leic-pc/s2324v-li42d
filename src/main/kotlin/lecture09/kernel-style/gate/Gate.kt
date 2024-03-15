package pt.isel.pc.jht.synchronizers.kernel

import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import java.util.concurrent.*
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

class Gate(private var open : Boolean = false) {
	private val locker = ReentrantLock()

	private inner class Request(
		val condition: Condition = locker.newCondition(),
		var done: Boolean = false
	)

	private var curRequest = Request()
	
	val isOpen
		get() = locker.withLock { open }
	
	fun open() {
		locker.withLock {
			if (!open) {
				open = true;
				curRequest.done = true
				curRequest.condition.signalAll()
			}
		}
	}

	fun close() {
		locker.withLock {
			if (open) {
				open = false;
				curRequest = Request()
			}
		}
	}
	
	fun await() {
		locker.withLock {
			if (!open) { 
				val myRequest = curRequest
				do {
					myRequest.condition.await()
				} while (!myRequest.done)
			}
		}
	}
}


val gate = Gate()

val done1 = AtomicInteger(0)
val done2 = AtomicInteger(0)
val latch = CountDownLatch(1)

fun action1() {
	gate.await()
	done1.incrementAndGet()
}

fun action2() {
	latch.await()
	gate.await()
	done2.incrementAndGet()
}

fun main() {
	repeat(100) { thread(isDaemon = true) { action1() } }
	repeat(100) { thread(isDaemon = true) { action2() } }
	
	TimeUnit.SECONDS.sleep(1)     // WARNING: do not use sleep for synchronization!

	gate.open()
	latch.countDown()
	TimeUnit.MICROSECONDS.sleep(10)     // WARNING: do not use sleep for synchronization!
	gate.close()
	
	TimeUnit.SECONDS.sleep(5)     // WARNING: do not use sleep for synchronization!
	
	println("terminated threads 1: ${ done1.get() }")
	println("terminated threads 2: ${ done2.get() }")
}