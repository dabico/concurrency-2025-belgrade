@file:Suppress("DuplicatedCode", "UNCHECKED_CAST")

package day3

import java.util.concurrent.atomic.*
import java.util.concurrent.locks.*
import kotlin.concurrent.*

// This implementation never stores `null` values.
class DoubleCompareSingleSetOnReentrantLock<E : Any>(initialValue: E) : DoubleCompareSingleSet<E> {
    private val a = AtomicReference<E>()
    private val aLock = ReentrantLock() // TODO: use me for `getA()` and `dcss(..)`!

    private val b = AtomicReference<E>()

    init {
        a.set(initialValue)
        b.set(initialValue)
    }

    override fun getA(): E {
        // TODO: guard this function with the lock.
        return a.get()
    }

    override fun dcss(
        expectedA: E, updateA: E, expectedB: E
    ): Boolean {
        // TODO: guard this function with the lock.
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