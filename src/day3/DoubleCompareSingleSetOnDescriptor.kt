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
        while (true) {
            when (val value = a.get()) {
                is DoubleCompareSingleSetOnDescriptor<*>.DcssDescriptor -> value.help()
                else -> return value as E
            }
        }
    }

    override fun dcss(expectedA: E, updateA: E, expectedB: E): Boolean {
        val descriptor = DcssDescriptor(
            expectedA = expectedA,
            updateA = updateA,
            expectedB = expectedB,
        )
        return with(descriptor) {
            apply()
            status.get() == SUCCESS
        }
    }

    private inner class DcssDescriptor(
        val expectedA: E, val updateA: E, val expectedB: E
    ) {
        val status = AtomicReference(UNDECIDED)

        fun install() {
            while (true) {
                when (val currentA = a.get()) {
                    is DoubleCompareSingleSetOnDescriptor<*>.DcssDescriptor -> currentA.help()
                    expectedA -> {
                        if (!a.compareAndSet(currentA, this)) continue
                        help()
                        return
                    }
                    else -> {
                        status.compareAndSet(UNDECIDED, FAILED)
                        return
                    }
                }
            }
        }

        fun help() {
            val unchanged = b.get() == expectedB
            status.compareAndSet(UNDECIDED, if (unchanged) SUCCESS else FAILED)
            when (status.get()) {
                SUCCESS -> a.compareAndSet(this, updateA)
                FAILED -> a.compareAndSet(this, expectedA)
                else -> error("Can only be in $SUCCESS or $FAILED state!")
            }
        }

        fun apply() {
            install()
            help()
        }
    }

    override fun setB(value: E) = b.set(value)

    override fun getB(): E = b.get()

    enum class Status {
        UNDECIDED, SUCCESS, FAILED
    }
}
