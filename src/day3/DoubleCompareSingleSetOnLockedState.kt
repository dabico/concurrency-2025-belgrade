@file:Suppress("DuplicatedCode", "UNCHECKED_CAST")

package day3

import java.util.concurrent.atomic.*

// This implementation never stores `null` values.
class DoubleCompareSingleSetOnLockedState<E : Any>(initialValue: E) : DoubleCompareSingleSet<E> {
    private val a = AtomicReference<Any>()
    private val b = AtomicReference<E>()

    init {
        a.set(initialValue)
        b.set(initialValue)
    }

    override fun getA(): E {
        while (true) {
            when (val current = a.get()) {
                LOCKED -> continue
                else -> return current as E
            }
        }
    }

    override fun dcss(
        expectedA: E, updateA: E, expectedB: E
    ): Boolean {
        while (true) {
            when (a.get()) {
                LOCKED -> continue
                expectedA -> {
                    if (!a.compareAndSet(expectedA, LOCKED)) continue
                    val success = b.get() == expectedB
                    val value = if (success) updateA else expectedA
                    a.set(value)
                    return success
                }
                else -> return false
            }
        }
    }

    override fun setB(value: E) = b.set(value)

    override fun getB(): E = b.get()
}

private const val LOCKED = "Locked"
