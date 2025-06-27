package day4

import day1.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

open class FlatCombiningQueue<E : Any> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = AtomicBoolean(false) // unlocked initially
    private val tasksForCombiner = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)

    override fun enqueue(element: E): Unit = enqueueOrDequeue(task = element) {
        queue.add(element)
    }

    override fun dequeue(): E? = enqueueOrDequeue(task = Dequeue) {
        queue.removeFirstOrNull()
    }

    private inline fun <R> enqueueOrDequeue(task: Any, operation: () -> R): R {
        // TODO: Make this code thread-safe using the flat-combining technique.
        // TODO: 1.  Try to become a combiner by
        // TODO:     changing `combinerLock` from `false` (unlocked) to `true` (locked).
        // TODO:     (use `tryAcquireLock()` and `releaseLock()` functions).
        // TODO: 2a. On success, apply this operation and help others by traversing
        // TODO:     `tasksForCombiner`, performing the announced operations, and
        // TODO:      updating the corresponding cells to `Result`.
        // TODO:      Put the corresponding helping object into `helpOthers()`.
        // TODO: 2b. If the lock is already acquired, announce this operation in
        // TODO:     `tasksForCombiner` by replacing a random cell state from
        // TODO:      `null` with `Dequeue`. Wait until either the cell state
        // TODO:      updates to `Result` (do not forget to clean it in this case),
        // TODO:      or `combinerLock` becomes available to acquire.
        return operation()
    }

    private fun helpOthers() {
        // TODO: Traverse `tasksForCombiner` and perform the announced operations,
        // TODO: updating the corresponding cells to `Result`.
    }

    private fun tryAcquireLock(): Boolean {
        TODO("Try to acquire combinerLock by changing `combinerLock` from `false` (unlocked) to `true` (locked).")
    }

    open fun releaseLock() {
        TODO("Release combinerLock by changing `combinerLock` to `false` (unlocked).")
    }

    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(tasksForCombiner.length())
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!

// TODO: Put this token in `tasksForCombiner` for dequeue().
// TODO: enqueue()-s should put the inserting element.
private object Dequeue

// TODO: Put the result wrapped with `Result` when the operation in `tasksForCombiner` is processed.
private class Result<V>(
    val value: V
)
