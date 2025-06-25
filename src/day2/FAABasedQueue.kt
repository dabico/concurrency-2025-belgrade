package day2

import day1.*
import java.util.concurrent.atomic.*

// TODO: Copy the code from `FAABasedQueueSimplified`
// TODO: and implement the infinite array on a linked list
// TODO: of fixed-size `Segment`s.
class FAABasedQueue<E> : Queue<E> {
    override fun enqueue(element: E) {
        TODO("implement me")
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        TODO("implement me")
    }
}

// TODO: Use me to construct a linked list of segments.
private class Segment(val id: Long) : AtomicReferenceArray<Any?>(SEGMENT_SIZE) {
    val next = AtomicReference<Segment?>(null)
}

// TODO: Use me to mark a cell poisoned.
private val POISONED = Any()

// DO NOT CHANGE THIS CONSTANT
private const val SEGMENT_SIZE = 2
