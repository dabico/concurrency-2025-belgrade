@file:Suppress("DuplicatedCode", "UNCHECKED_CAST")

package day3

import day3.DoubleCompareSingleSetOnDescriptor.Status.*
import java.util.concurrent.atomic.*

// This implementation never stores `null` values.
class DoubleCompareSingleSetOnDescriptor<E : Any>(initialValue: E) : DoubleCompareSingleSet<E> {
    private val a = AtomicReference<Any>()
    private val b = AtomicReference<E>()

    init {
        a.set(initialValue)
        b.set(initialValue)
    }

    override fun getA(): E {
        // TODO: 'a' can store CAS2Descriptor
        return a.get() as E
    }

    override fun dcss(expectedA: E, updateA: E, expectedB: E): Boolean {
        val descriptor = DcssDescriptor(expectedA, updateA, expectedB)
        descriptor.apply()
        return descriptor.status.get() == SUCCESS
    }

    private inner class DcssDescriptor(
        val expectedA: E, val updateA: E, val expectedB: E
    ) {
        val status = AtomicReference(UNDECIDED)

        fun apply() {
            // TODO: (1) Install the descriptor to 'a'
            // TODO: (2) Apply logically: check whether 'b' == expectedB and update the status
            // TODO: (3) Apply physically: update 'a'
        }
    }

    override fun setB(value: E) {
        b.set(value)
    }

    override fun getB(): E {
        return b.get()
    }

    enum class Status {
        UNDECIDED, SUCCESS, FAILED
    }
}