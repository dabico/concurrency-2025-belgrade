@file:Suppress("DuplicatedCode", "UNCHECKED_CAST")

package day3

import day3.AtomicArrayWithCAS2AndImplementedDCSS.Status.*
import java.util.concurrent.atomic.*

// This implementation never stores `null` values.
class AtomicArrayWithCAS2AndImplementedDCSS<E : Any>(size: Int, initialValue: E) {
    private val array = AtomicReferenceArray<Any?>(size)

    init {
        // Fill array with the initial value.
        for (i in 0 until size) {
            array[i] = initialValue
        }
    }

    fun get(index: Int): E = when (val element = array.get(index)) {
        is AtomicArrayWithCAS2AndImplementedDCSS<*>.CAS2Descriptor -> element.get(index) as E
        else -> element as E
    }

    fun cas2(
        index1: Int, expected1: E, update1: E,
        index2: Int, expected2: E, update2: E,
    ): Boolean {
        require(index1 != index2) { "The indices should be different" }
        val descriptor = when {
            index1 < index2 -> CAS2Descriptor(
                index1 = index1, expected1 = expected1, update1 = update1,
                index2 = index2, expected2 = expected2, update2 = update2,
            )
            else -> CAS2Descriptor(
                index1 = index2, expected1 = expected2, update1 = update2,
                index2 = index1, expected2 = expected1, update2 = update1,
            )
        }
        return with(descriptor) {
            tryInstallDescriptorInIndex1()
            status.get() == SUCCESS
        }
    }

    inner class CAS2Descriptor(
        val index1: Int, val expected1: E, val update1: E,
        val index2: Int, val expected2: E, val update2: E,
    ) {
        val status = AtomicReference(UNDECIDED)

        fun tryInstallDescriptorInIndex1() {
            while (true) {
                when (val value1 = array.get(index1)) {
                    is AtomicArrayWithCAS2AndImplementedDCSS<*>.CAS2Descriptor -> {
                        value1.tryInstallDescriptorInIndex2()
                        value1.applyLogicallyAndPhysically()
                    }

                    else -> {
                        if (value1 != expected1) {
                            status.set(FAILED)
                            return
                        }

                        if (!dcss(index1, expected1, this, status, UNDECIDED)) continue

                        tryInstallDescriptorInIndex2()
                        applyLogicallyAndPhysically()
                        return
                    }
                }
            }
        }

        private fun tryInstallDescriptorInIndex2() {
            while (true) {
                when (val value2 = array.get(index2)) {
                    is AtomicArrayWithCAS2AndImplementedDCSS<*>.CAS2Descriptor -> {
                        if (value2 == this) return
                        value2.tryInstallDescriptorInIndex2()
                        value2.applyLogicallyAndPhysically()
                    }

                    else -> {
                        if (value2 != expected2) {
                            status.compareAndSet(UNDECIDED, FAILED)
                            return
                        }

                        if (status.get() != UNDECIDED) return
                        if (dcss(index2, expected2, this, status, UNDECIDED)) return
                    }
                }
            }
        }

        private fun applyLogicallyAndPhysically() {
            status.compareAndSet(UNDECIDED, SUCCESS)
            when (val status = status.get()) {
                SUCCESS -> {
                    array.compareAndSet(index2, this, update2)
                    array.compareAndSet(index1, this, update1)
                }
                FAILED -> {
                    array.compareAndSet(index2, this, expected2)
                    array.compareAndSet(index1, this, expected1)
                }
                else -> error("Invalid status: $status")
            }
        }

        fun get(index: Int): E {
            val unsuccessful = when (val status = status.get()) {
                SUCCESS -> false
                FAILED, UNDECIDED -> true
                else -> error("Invalid status: $status")
            }

            return when (index) {
                index1 -> if (unsuccessful) expected1 else update1
                index2 -> if (unsuccessful) expected2 else update2
                else -> error("Invalid index: $index")
            }
        }
    }

    enum class Status {
        UNDECIDED, SUCCESS, FAILED
    }

    fun dcss(
        index: Int,
        expectedCellState: Any?,
        updateCellState: Any?,
        statusReference: AtomicReference<*>,
        expectedStatus: Any?
    ): Boolean =
        if (array[index] == expectedCellState && statusReference.get() == expectedStatus) {
            array[index] = updateCellState
            true
        } else {
            false
        }
}
