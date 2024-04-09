package pt.isel.pc.jht.lecture17.volatile

import java.util.concurrent.*
import kotlin.concurrent.*

/* The @Volatile annotation introduces synchronizes-with
 * relations between writes and subsequent reads to the
 * 'stop' variable.
 * This allows the function incrementCounterLoop to always
 * observe writes to the 'stop' variable, eventually causing 
 * it to terminate. 
 *
 * Without @Volatile, incrementCounterLoop may or may not
 * terminate. The behaviour cannot be precisely predicted.
 */
@Volatile
var stop = false

var counter = 0

fun incrementCounterLoop() {
	while (!stop) {
		++counter
	}
}

fun main() {
	println(":: STARTING ::")
	println("counter: $counter")
	
	val th = thread(isDaemon = true) { incrementCounterLoop() }
	for (i in 1..3) {
		TimeUnit.SECONDS.sleep(2)
		println("counter: $counter")
	}
	
	println(":: stopping... ::")
	stop = true
	
	for (j in 1..3) {
		TimeUnit.SECONDS.sleep(2)
		println("counter: $counter")
	}
	
	println(":: ENDING ::")
	th.join(2000)
	
	println("counter: $counter")
	println("still running? ${ th.isAlive() }")
}
