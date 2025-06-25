package day2

import day1.*
import java.util.concurrent.atomic.*

class FAABasedQueue<E> : Queue<E> {

    private val head: AtomicReference<Segment>
    private val tail: AtomicReference<Segment>

    private val enqIdx = AtomicLong(0)
    private val deqIdx = AtomicLong(0)

    init {
        val sentinel = Segment(id = 0)
        head = AtomicReference(sentinel)
        tail = AtomicReference(sentinel)
    }

    private val shouldTryDequeue get(): Boolean {
        while (true) {
            val deq = deqIdx.get().toInt()
            val enq = enqIdx.get().toInt()
            if (deq != deqIdx.get().toInt()) continue
            return deq < enq
        }
    }

    private fun moveHeadForward(segment: Segment) {
        val current = head.get()
        if (current.id < segment.id)
            head.compareAndSet(current, segment)
    }

    private fun moveTailForward(segment: Segment) {
        val current = tail.get()
        if (current.id < segment.id)
            tail.compareAndSet(current, segment)
    }

    private fun findSegment(start: Segment, id: Long): Segment {
        var current = start
        while (true) {
            if (current.id == id) return current
            val next = current.next.get()
            if (next != null) {
                current = next
            } else {
                val created = Segment(id = current.id + 1)
                if (current.next.compareAndSet(null, created)) {
                    if (created.id == id) return created
                    current = created
                }
            }
        }
    }

    override fun enqueue(element: E) {
        while (true) {
            val current = tail.get()
            val idx = enqIdx.getAndIncrement().toInt()
            val segment = findSegment(
                start = current,
                id = idx.toLong() / SEGMENT_SIZE,
            )
            moveTailForward(segment)
            if (segment.compareAndSet(idx % SEGMENT_SIZE, null, element))
                return
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            if (!shouldTryDequeue) return null
            val current = head.get()
            val idx = deqIdx.getAndIncrement().toInt()
            val segment = findSegment(
                start = current,
                id = idx.toLong() / SEGMENT_SIZE,
            )
            moveHeadForward(segment)
            if (segment.compareAndSet(idx % SEGMENT_SIZE, null, POISONED))
                continue
            return segment.get(idx % SEGMENT_SIZE) as E
        }
    }
}

private class Segment(val id: Long) : AtomicReferenceArray<Any?>(SEGMENT_SIZE) {
    val next = AtomicReference<Segment?>(null)
}

private val POISONED = Any()

private const val SEGMENT_SIZE = 2
