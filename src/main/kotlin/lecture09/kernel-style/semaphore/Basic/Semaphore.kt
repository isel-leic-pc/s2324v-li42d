package pt.isel.pc.jht.synchronizers.monitor

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class BasicSemaphore(private var permits: Int) {
	private val locker = ReentrantLock()
	private val waitSet = locker.newCondition()

	fun acquire(units: Int = 1) {
		locker.withLock {
			while (permits < units) {
				waitSet.await()
			}
			permits -= units
		}
	}
	
	fun release(units: Int = 1) {
		locker.withLock {
			permits += units
			waitSet.signalAll()
		}
	}
}
