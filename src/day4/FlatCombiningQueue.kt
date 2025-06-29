package day4

import day1.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*

open class FlatCombiningQueue<E : Any> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = AtomicBoolean(false) // unlocked initially
    private val tasksForCombiner = AtomicReferenceArray<Task<E>?>(TASKS_FOR_COMBINER_SIZE)

    override fun enqueue(element: E): Unit = enqueueOrDequeue(task = Task.Enqueue(value = element)) {
        queue.add(element)
    }

    override fun dequeue(): E? = enqueueOrDequeue(task = Task.Dequeue) {
        queue.removeFirstOrNull()
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <R> enqueueOrDequeue(task: Task<E>, operation: () -> R): R {
        var idx: Int? = null
        while (true) {
            if (tryAcquireLock()) {
                val result = if (idx == null) {
                    operation()
                } else {
                    val result = getResultAndReset(idx)
                    if (result != null) {
                        result.value as R
                    } else {
                        tasksForCombiner.set(idx, null)
                        operation()
                    }
                }

                helpOthers()
                releaseLock()
                return result
            } else {
                if (idx == null) {
                    idx = randomCellIndex()
                    val success = tasksForCombiner.compareAndSet(idx, null, task)
                    if (success) {
                        val result = getResultAndReset(idx)
                        if (result != null) return result.value as R
                    } else {
                        idx = null
                    }
                } else {
                    val result = getResultAndReset(idx)
                    if (result != null) return result.value as R
                }
            }
        }
    }

    private fun getResultAndReset(index: Int): Task.Result<E>? =
        getResult(index)?.also { tasksForCombiner.set(index, null) }

    private fun getResult(index: Int): Task.Result<E>? =
        when (val task = tasksForCombiner.get(index)) {
            is Task.Result<*> -> task as Task.Result<E>
            else -> null
        }

    private fun helpOthers() {
        for (idx in 0 until tasksForCombiner.length()) {
            when (val task = tasksForCombiner.get(idx)) {
                is Task.Dequeue -> {
                    val value = queue.removeFirstOrNull()
                    val result = Task.Result(value = value)
                    tasksForCombiner.set(idx, result)
                }
                is Task.Enqueue<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    val value = task.value as E
                    val result = Task.Result(value = value)
                    tasksForCombiner.set(idx, result)
                    queue.add(value)
                }
                else -> continue
            }
        }
    }

    private fun tryAcquireLock(): Boolean = combinerLock.compareAndSet(false, true)

    open fun releaseLock() = combinerLock.set(false)

    private fun randomCellIndex(): Int = ThreadLocalRandom.current().nextInt(tasksForCombiner.length())

    private companion object {
        private const val TASKS_FOR_COMBINER_SIZE = 3
    }
}

private sealed interface Task<out T : Any> {

    data object Dequeue : Task<Nothing>
    data class Enqueue<T : Any>(val value: T) : Task<T>
    data class Result<T : Any>(val value: T?) : Task<T>
}
