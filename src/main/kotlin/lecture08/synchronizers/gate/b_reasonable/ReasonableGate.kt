package pt.isel.pc.jht.synchronizers.monitor

import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import java.util.concurrent.*
import kotlin.concurrent.withLock
import kotlin.concurrent.thread

class ReasonableGate(private var open : Boolean = false) {
	private val lock = ReentrantLock()
	private val waitSet = lock.newCondition()

	private var groupId = 0
	
	val isOpen
		get() = lock.withLock { open }
	
	fun open() {
		lock.withLock {
			if (!open) {
				open = true;
				waitSet.signalAll()
			}
		}
	}

	fun close() {
		lock.withLock {
			if (open) {
				open = false;
				groupId++
			}
		}
	}
	
	fun await() {
		lock.withLock {
			if (!open) { 
				val myGroupId = groupId
				do {
					waitSet.await()
				} while (!open && myGroupId == groupId)
			}
		}
	}
}


val gate = ReasonableGate()

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