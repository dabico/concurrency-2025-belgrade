package day2

import java.util.concurrent.atomic.*

class CountersSnapshot {
    val counter1 = AtomicLong(0)
    val counter2 = AtomicLong(0)
    val counter3 = AtomicLong(0)

    fun incrementCounter1() = counter1.getAndIncrement()
    fun incrementCounter2() = counter2.getAndIncrement()
    fun incrementCounter3() = counter3.getAndIncrement()

    fun countersSnapshot(): Triple<Long, Long, Long> {
        // TODO: make me atomic using the double-collect technique.
        return Triple(counter1.get(), counter2.get(), counter3.get())
    }
}