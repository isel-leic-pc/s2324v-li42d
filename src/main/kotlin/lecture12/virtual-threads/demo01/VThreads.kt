package pt.isel.pc.jht.vthreads.demo01

val pbuilder = Thread.ofPlatform().name("pworker-", 0);
val vbuilder = Thread.ofVirtual().name("vworker-", 0);

val task = {
    with(Thread.currentThread()) {
        val threadType = if (isVirtual) "virtual" else "platform"
        println("[$name] Thread ID: ${ threadId() } ($threadType)")
    }
}

fun main() {
    val p1 = pbuilder.start(task)
    val p2 = pbuilder.start(task)
    val v1 = vbuilder.start(task)
    val v2 = vbuilder.start(task)

    v2.join()
    v1.join()
    p2.join()
    p1.join()

    println("[${ p1.name }] Terminated")
    println("[${ p2.name }] Terminated")
    println("[${ v1.name }] Terminated")
    println("[${ v2.name }] Terminated")
}