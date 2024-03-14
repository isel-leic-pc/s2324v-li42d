
var counter = 0

val NTHREADS = 16
val NITERATIONS = 1000000

fun main() {

	(0 until NTHREADS).map {
		val t = Thread { 
			repeat(NITERATIONS) {
				counter++
			}
		}
		t.start()
		t
	}.forEach { t -> t.join() }

	println("counter = $counter (should be ${NTHREADS * NITERATIONS})")
}
