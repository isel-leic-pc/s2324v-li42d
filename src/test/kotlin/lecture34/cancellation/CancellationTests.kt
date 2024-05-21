package pt.isel.pc.jht.lecture34.coroutines.cancellation

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.TimeoutCancellationException
import kotlin.coroutines.cancellation.CancellationException

class CancellationTests {

    @Test
    fun first() {
        assertThrows<Exception> {
            runBlocking {
                launch {
                    logger.info("cr1 running")
                    try {
                        delay(500)
                        throw Exception("CR1 DONE!")  // Will cancel the parent, and parent will cancel CR2.
                    } catch (exc: Exception) {
                        logger.info("CR1 EXCEPTION!")
                        logger.info(exc.message)
                        throw exc
                    }
                }

                launch {
                    logger.info("cr2 running")
                    delay(2000)
                    logger.info("cr2 finished")
                }
            }
            logger.info("Terminated properly") // Will not appear in logs as runBlocking will throw an Exception
        }
    }

    @Test
    fun second() {
        runBlocking {

            // A supervisorScope introduces a CoroutineScope with a SupervisorJob.
            // A SupervisorJob is not automatically cancelled when a child coroutine
            // ends with an error.

            supervisorScope {
                launch {
                    logger.info("cr1 running")
                    try {
                        delay(500)
                        throw Exception("CR1 DONE!")  // Will NOT cancel its parent, hence CR2 will run to end.
                    } catch (exc: Exception) {
                        logger.info("CR1 EXCEPTION!")
                        logger.info(exc.message)
                        throw exc
                    }
                }

                launch {
                    logger.info("cr2 running")
                    delay(2000)
                    logger.info("cr2 finished")
                }
            }
        }
        logger.info("Terminated properly")
    }

    @Test
    fun third() {

        // withTimeout will cancel a coroutine and all its children, but propagates to
        // its parent as a CancellationException (just like a Job.cancel()), hence it
        // does not automatically cancel the parent and sibling coroutines.

        runBlocking {
            launch {
                withTimeout(500) {// Automatically cancel cr1 after 500ms
                    logger.info("cr1 running")
                    delay(1000)
                    logger.info("cr1 finished")
                }
            }
            launch {
                logger.info("cr2 running")
                delay(2000)
                logger.info("cr2 finished")
            }
        }
        logger.info("Terminated properly")
    }

    @Test
    fun fourth() {

        // nonCancellableDelay is based on suspendCoroutine and therefore does
        // not react to cancellation.  The coroutine does change to the cancelled
        // state.  The cancelled parent will end with a CancellationException.

        assertThrows<CancellationException> {
            runBlocking {
                launch {
                    logger.info("cr1 running")
                    nonCancellableDelay(1000)
                    logger.info("isCancelled=${coroutineContext.job.isCancelled}")
                    logger.info("cr1 finished")
                }
                delay(500)
                cancel()
            }
        }
    }

    @Test
    fun fifth() {

        // cancellableDelay is based on suspendCancellableCoroutine and does
        // react to cancellation.  CR1 will not finish.

        // cancellableDelay is using invokeOnCancellation to effectively cancel the
        // timed task.  Try commenting the code block with the invocation to
        // invokeOnCancellation [163..167] and observe the difference

        assertThrows<CancellationException> {
            runBlocking {
                launch {
                    logger.info("cr1 running")
                    cancellableDelay(1000)
                    logger.info("isCancelled=${coroutineContext.job.isCancelled}")
                    logger.info("cr1 finished")
                }
                delay(500)
                cancel()
            }
        }
        Thread.sleep(1500)
    }

    companion object {
        private val logger = Logger.getLogger("CancellationTests")
        private val scheduler = Executors.newScheduledThreadPool(1)

        suspend fun nonCancellableDelay(ms: Long) {
            suspendCoroutine<Unit> { continuation ->
                scheduler.schedule({
                    continuation.resume(Unit)
                }, ms, TimeUnit.MILLISECONDS)
            }
        }

        suspend fun cancellableDelay(ms: Long) {
            suspendCancellableCoroutine<Unit> { continuation ->
                val future = scheduler.schedule({
                    logger.info("Calling continuation on scheduled callback")
                    continuation.resume(Unit)
                    logger.info("After calling continuation on scheduled callback")
                }, ms, TimeUnit.MILLISECONDS)

                continuation.invokeOnCancellation {
                    logger.info("before future cancel")
                    future.cancel(false)
                    logger.info("after future cancel")
                }
            }
        }
    }
}