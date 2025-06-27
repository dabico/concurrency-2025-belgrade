package day4

import AbstractQueueTest
import day1.*
import org.junit.*
import java.util.concurrent.atomic.*
import kotlin.concurrent.*

class FlatCombiningQueueTest : AbstractQueueTest(FlatCombiningQueue(), checkObstructionFreedom = false) {

    @Test
    fun testHelping() {
        val lockReleases = AtomicInteger(0)
        val q = object : FlatCombiningQueue<Int>() {
            override fun releaseLock() {
                super.releaseLock()
                lockReleases.incrementAndGet()
            }
        }
        runParallelQueueOperations(q)
        check(lockReleases.get() < THREADS * ENQ_DEQ_PAIRS_PER_THREAD) {
            "The combiner does not help other threads"
        }
        lockReleases.set(0)
        runParallelQueueOperations(q)
        check(lockReleases.get() < THREADS * ENQ_DEQ_PAIRS_PER_THREAD) {
            "The combiner helped other threads during the first execution, " + "but did not help during the second one. " + "Probably, you clean the array slots incorrectly."
        }
    }

    private fun runParallelQueueOperations(q: Queue<Int>) {
        (1..THREADS).map {
            thread {
                repeat(ENQ_DEQ_PAIRS_PER_THREAD) {
                    q.enqueue(it)
                    q.dequeue()
                }
            }
        }.forEach { it.join() }
    }

    private val THREADS = 4
    private val ENQ_DEQ_PAIRS_PER_THREAD = 1000_000
}