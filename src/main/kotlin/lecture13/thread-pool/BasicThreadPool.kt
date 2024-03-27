package pt.isel.pc.jht.threadpools.basic

import java.util.*
import java.util.logging.*
import java.util.concurrent.*
import java.util.concurrent.locks.*
import kotlin.concurrent.*

class BasicThreadPool(private val nThreads: Int) {

	private val lock = ReentrantLock()
	private val workAvailable = lock.newCondition()

	private val workQueue = LinkedList<Runnable>()
	
	companion object {
		val logger = Logger.getLogger(BasicThreadPool::class.qualifiedName)
	}

	init {
		for (n in 1..nThreads) {
			thread(isDaemon = true) {
				workerThreadLoop()
			}
		}
	}

	fun execute(workItem: Runnable) =
		lock.withLock {
			workQueue.add(workItem)
			workAvailable.signal()
		}

	private fun workerThreadLoop() {
		while (true) {
			val workItem = getNextWorkItem()
			safeRun(workItem)
		}
	}

	private fun getNextWorkItem() : Runnable {
		lock.withLock {
			while (true) {
				if (workQueue.size > 0) {
					return workQueue.removeFirst()
				}
				try {
					workAvailable.await()
				} catch (ex: InterruptedException) {
					// Auto-interrupt of worker threads is ignored...
				}
			}
		}
	}
	
	private fun safeRun(workItem: Runnable) {
		try {
			workItem.run()
		} catch (ex: Throwable) {
			logger.log(Level.WARNING, "Exception in worker thread. Proceeding.", ex)
		}
	}
}
