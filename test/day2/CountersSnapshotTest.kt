package day2

import TestBase
import org.jetbrains.lincheck.datastructures.*

class CountersSnapshotTest : TestBase(
    sequentialSpecification = CountersSnapshotSequential::class,
    scenarios = 30,
) {
    private val countersSnapshot = CountersSnapshot()

    @Operation
    fun incrementCounter1() = countersSnapshot.incrementCounter1()

    @Operation
    fun incrementCounter2() = countersSnapshot.incrementCounter2()

    @Operation
    fun incrementCounter3() = countersSnapshot.incrementCounter3()

    @Operation
    fun countersSnapshot() = countersSnapshot.countersSnapshot()
}

class CountersSnapshotSequential {
    var counter1 = 0L
    var counter2 = 0L
    var counter3 = 0L

    fun incrementCounter1() = counter1++
    fun incrementCounter2() = counter2++
    fun incrementCounter3() = counter3++

    fun countersSnapshot(): Triple<Long, Long, Long> =
        Triple(counter1, counter2, counter3)
}