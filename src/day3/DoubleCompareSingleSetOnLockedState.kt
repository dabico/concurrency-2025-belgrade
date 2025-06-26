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
        // TODO: Cover the case of `a.get() === LOCKED`.
        return a.get() as E
    }

    override fun dcss(
        expectedA: E, updateA: E, expectedB: E
    ): Boolean {
        // TODO: Make me thread-safe by "locking" the `a` reference
        // TODO: atomically changing it to the LOCKED state.
        val curA = a.get()
        if (curA !== expectedA) return false
        if (b.get() !== expectedB) return false
        a.set(updateA)
        return true
    }

    override fun setB(value: E) {
        b.set(value)
    }

    override fun getB(): E {
        return b.get()
    }
}

// TODO: Store me in `a` to indicate that the reference is "locked".
// TODO: Other operations should wait in an active loop until the
// TODO: value changes.
private val LOCKED = "Locked"
