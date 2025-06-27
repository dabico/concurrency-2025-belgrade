package day4

import org.jetbrains.lincheck.*
import org.jetbrains.lincheck.datastructures.*
import java.util.concurrent.*
import kotlin.concurrent.*
import kotlin.test.*


/* TODO: Let's write your first Lincheck test.
         1. Add a `test()` function and annotate it with `@Test` to use JUnit.
         2. In this function, create an integer non-atomic counter
            and launch two threads that increment this counter.
         3. In the test thread, wait until the launched threads are finished
            and check that both the increments have been applied.
         4. Run the test -- it will likely succeed even though
            the increments are not atomic.
         5. Wrap the test code with `Lincheck.runConcurrentTest { ... }`
            and re-run the test. The test should fail.
         6. Debug the test with the plugin.
*/
class CounterTest {
    @Test
    fun test() {
        var counter = 0
        val t1 = thread {
            counter++
        }
        val t2 = thread {
            counter++
        }
        t1.join()
        t2.join()
        assert(counter == 2) { "The counter should be equal to 2" }
    }
}

/* TODO: Reveal a bug in `ScheduledThreadPoolExecutor` from the standard Java library.
         1. Create a new `ScheduledThreadPoolExecutor` instance.
         2. In a parallel thread, shutdown this executor.
         3. In the main thread, schedule a task in this executor.
            Please note that this task might be rejected due to the shutdown,
            ignore the corresponding exception.
         4. Wrap the test code with `Lincheck.runConcurrentTest { ... }`.
         5. Run the test -- the execution should get into a livelock.
 */
class ScheduledThreadPoolExecutorTest {
    @Test
    fun test() = Lincheck.runConcurrentTest {
       // TODO
    }
}

/* TODO: Write a Lincheck test for ConcurrentLinkedDeque
         and reveal a concurrent bug. Please use the same API
         for data structures as we use in the course, and check
         all the peek/poll/add first/last operations on the deque.
 */
class ConcurrentLinkedDequeTest {
}