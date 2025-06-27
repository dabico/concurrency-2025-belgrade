package day3

import TestBase
import org.jetbrains.lincheck.datastructures.*

class DoubleCompareSingleSetOnDescriptorTest : AbstractDoubleCompareSingleSetTest(
    dcss = DoubleCompareSingleSetOnDescriptor(0),
    checkObstructionFreedom = true
)

class DoubleCompareSingleSetOnReentrantLockTest : AbstractDoubleCompareSingleSetTest(
    dcss = DoubleCompareSingleSetOnReentrantLock(0),
    checkObstructionFreedom = false
)

class DoubleCompareSingleSetOnLockedStateTest : AbstractDoubleCompareSingleSetTest(
    dcss = DoubleCompareSingleSetOnLockedState(0),
    checkObstructionFreedom = false
)

@Param(name = "value", gen = IntGen::class, conf = "0:2")
abstract class AbstractDoubleCompareSingleSetTest(
    dcss: DoubleCompareSingleSet<Int>,
    checkObstructionFreedom: Boolean,
): TestBase(
    sequentialSpecification = DCSSSequential::class,
    checkObstructionFreedom = checkObstructionFreedom,
    scenarios = 1000,
) {
    private val dcss = dcss

    @Operation
    fun getA() = dcss.getA()

    @Operation
    fun getB() = dcss.getB()

    @Operation
    fun setB(@Param("value") value: Int) = dcss.setB(value)

    @Operation(params = ["value", "value", "value"])
    fun dcss(expectedA: Int, updateA: Int, expectedB: Int) = dcss.dcss(expectedA, updateA, expectedB)
}

class DCSSSequential {
    private var a = 0
    private var b = 0

    fun getA(): Int = a

    fun getB(): Int = b

    fun setB(value: Int) {
        b = value
    }

    fun dcss(expectedA: Int, updateA: Int, expectedB: Int): Boolean {
        if (a != expectedA || b != expectedB) return false
        a = updateA
        return true
    }
}