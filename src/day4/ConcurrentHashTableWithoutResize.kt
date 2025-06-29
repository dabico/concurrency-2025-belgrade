package day4

import java.util.concurrent.atomic.*
import kotlin.math.absoluteValue

class ConcurrentHashTableWithoutResize<K : Any, V : Any>(initialCapacity: Int) : HashTable<K, V> {
    private val table = AtomicReference(Table<K, V>(initialCapacity))

    override fun put(key: K, value: V): V? {
        while (true) {
            // Try to insert the key/value pair.
            val putResult = table.get().put(key, value)
            if (putResult === NEEDS_REHASH) {
                // The current table is too small to insert a new key.
                // Create a new table of x2 capacity,
                // copy all elements to it,
                // and restart the current operation.
                resize()
            } else {
                // The operation has been successfully performed,
                // return the previous value associated with the key.
                return putResult as V?
            }
        }
    }

    override fun get(key: K): V? {
        return table.get().get(key)
    }

    override fun remove(key: K): V? {
        return table.get().remove(key)
    }

    private fun resize() {
        error("Should not be called in this task")
    }

    class Table<K : Any, V : Any>(val capacity: Int) {
        val keys = AtomicReferenceArray<Any?>(capacity)
        val values = AtomicReferenceArray<V?>(capacity)

        fun put(key: K, value: V): Any? {
            var index = index(key)
            repeat(MAX_PROBES) {
                val curKey = keys.get(index)
                when (curKey) {
                    key -> { // The cell contains the specified key.
                        // Update the value and return the previous one.
                        return values.getAndSet(index, value)
                    }
                    null -> { // The cell does not store a key.
                        // Insert the key/value pair into this cell.
                        if (keys.compareAndSet(index, null, key)) {
                            return values.getAndSet(index, value)
                        } else if (keys.get(index) == key) {
                            return values.getAndSet(index, value)
                        }
                    }
                }
                // Process the next cell, use linear probing.
                index = (index + 1) % capacity
            }
            // Inform the caller that the table should be resized.
            return NEEDS_REHASH
        }

        fun get(key: K): V? {
            var index = index(key)
            // Search for a specified key probing `MAX_PROBES` cells.
            // If neither the key is not found after that,
            // the table does not contain it.
            repeat(MAX_PROBES) {
                val curKey = keys[index]
                when (curKey) {
                    // The cell contains the required key.
                    key -> return values[index]
                    // Empty cell. The key has not been found.
                    null -> return null
                    // Process the next cell, use linear probing.
                    else -> index = (index + 1) % capacity
                }
            }
            // The key has not been found.
            return null
        }

        fun remove(key: K): V? {
            var index = index(key)
            // Search for a specified key probing `MAX_PROBES` cells.
            // If neither the key is not found after that,
            // the table does not contain it.
            repeat(MAX_PROBES) {
                val curKey = keys.get(index)
                when (curKey) {
                    // The cell contains the required key.
                    key -> return values.getAndSet(index, null)
                    // Empty cell. The key has not been found.
                    null -> return null
                    // Process the next cell, use linear probing.
                    else -> index = (index + 1) % capacity
                }
            }
            // The key has not been found.
            return null
        }

        private fun index(key: Any) = ((key.hashCode() * MAGIC) % capacity).absoluteValue
    }
}

private const val MAGIC = -0x61c88647 // golden ratio
private const val MAX_PROBES = 2
private val NEEDS_REHASH = Any()
