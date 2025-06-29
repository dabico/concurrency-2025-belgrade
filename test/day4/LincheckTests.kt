package day4

import org.jetbrains.lincheck.*
import org.jetbrains.lincheck.datastructures.*
import java.util.concurrent.*
import kotlin.concurrent.*
import kotlin.test.*


class CounterTest {
    @Test
    fun test() = Lincheck.runConcurrentTest {
        var counter = 0
        val t1 = thread { counter++ }
        val t2 = thread { counter++ }
        t1.join()
        t2.join()
        assert(counter == 2) { "The counter should be equal to 2" }
    }
}

class ScheduledThreadPoolExecutorTest {
    @Test
    fun test() = Lincheck.runConcurrentTest {
       val executor = ScheduledThreadPoolExecutor(1)
       thread { executor.shutdown() }
       try {
           val result = executor.schedule({
               println("Hello, Lincheck!")
           }, 10, TimeUnit.SECONDS)
           result.get()
       } catch (_: RejectedExecutionException) {
           // task might be rejected due to the shutdown,
           // ignore the corresponding exception.
       }
    }
}

@Suppress("unused")
@Param(name = "value", gen = IntGen::class, conf = "0:3")
class ConcurrentLinkedDequeTest {

    private val deque = ConcurrentLinkedDeque<Int>()

    @Operation
    fun addFirst(@Param(name = "value") value: Int) = deque.addFirst(value)

    @Operation
    fun addLast(@Param(name = "value") value: Int) = deque.addLast(value)

    @Operation
    fun peekFirst(): Int = deque.peekFirst()

    @Operation
    fun peekLast(): Int = deque.peekLast()

    @Operation
    fun pollFirst(): Int = deque.pollFirst()

    @Operation
    fun pollLast(): Int = deque.pollLast()

    @Test
    fun modelCheckingTest() = ModelCheckingOptions().check(this::class)
}
