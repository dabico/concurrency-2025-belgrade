@file:Suppress("DuplicatedCode", "UNCHECKED_CAST")

package day3

import day3.AtomicArrayWithCAS2SingleWriter.Status.*
import java.util.concurrent.atomic.*

// This implementation never stores `null` values.
class AtomicArrayWithCAS2SingleWriter<E : Any>(size: Int, initialValue: E) {
    private val array = AtomicReferenceArray<Any?>(size)

    init {
        // Fill array with the initial value.
        for (i in 0 until size) {
            array[i] = initialValue
        }
    }

    fun get(index: Int): E = when (val element = array.get(index)) {
        is AtomicArrayWithCAS2SingleWriter<*>.CAS2Descriptor -> element.get(index) as E
        else -> element as E
    }

    fun cas2(
        index1: Int, expected1: E, update1: E,
        index2: Int, expected2: E, update2: E,
    ): Boolean {
        require(index1 != index2) { "The indices should be different" }
        val descriptor = CAS2Descriptor(
            index1 = index1, expected1 = expected1, update1 = update1,
            index2 = index2, expected2 = expected2, update2 = update2,
        )
        return with(descriptor) {
            apply()
            status.get() == SUCCESS
        }
    }

    inner class CAS2Descriptor(
        val index1: Int, val expected1: E, val update1: E,
        val index2: Int, val expected2: E, val update2: E,
    ) {
        val status = AtomicReference(UNDECIDED)

        fun apply() {
            while (true) {
                when (val current1 = array.get(index1)) {
                    is AtomicArrayWithCAS2SingleWriter<*>.CAS2Descriptor -> TODO("Concurrent writes are not supported!")
                    else -> {
                        if (current1 != expected1) {
                            status.set(FAILED)
                            return
                        }
                        if (array.compareAndSet(index1, expected1, this)) {
                            when (val current2 = array.get(index2)) {
                                is AtomicArrayWithCAS2SingleWriter<*>.CAS2Descriptor -> TODO("Concurrent writes are not supported!")
                                else -> {
                                    if (current2 != expected2) {
                                        status.set(FAILED)
                                        array.compareAndSet(index1, this, expected1)
                                        return
                                    }

                                    val installed = array.compareAndSet(index2, expected2, this)
                                    if (!installed) {
                                        status.set(FAILED)
                                        array.compareAndSet(index1, this, expected1)
                                        return
                                    }

                                    status.compareAndSet(UNDECIDED, SUCCESS)
                                    array.compareAndSet(index2, this, update2)
                                    array.compareAndSet(index1, this, update1)
                                    return
                                }
                            }
                        }
                    }
                }
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
}
