package day4

import TestBase
import org.jetbrains.lincheck.datastructures.Operation

class SingleWriterHashTableTest : TestBase(
    sequentialSpecification = SequentialHashTableIntInt::class,
    scenarios = 300
) {
    private val hashTable = SingleWriterHashTable<Int, Int>(initialCapacity = 1)

    @Operation(nonParallelGroup = "writer")
    fun put(key: Int, value: Int): Int? = hashTable.put(key, value)

    @Operation
    fun get(key: Int): Int? = hashTable.get(key)

    @Operation(nonParallelGroup = "writer")
    fun remove(key: Int): Int? = hashTable.remove(key)
}

class ConcurrentHashTableWithoutResizeTest : TestBase(
    sequentialSpecification = SequentialHashTableIntInt::class,
    scenarios = 300
) {
    private val hashTable = ConcurrentHashTableWithoutResize<Int, Int>(initialCapacity = 30)

    @Operation
    fun put(key: Int, value: Int): Int? = hashTable.put(key, value)

    @Operation
    fun get(key: Int): Int? = hashTable.get(key)

    @Operation
    fun remove(key: Int): Int? = hashTable.remove(key)
}

class ConcurrentHashTableTest : TestBase(
    sequentialSpecification = SequentialHashTableIntInt::class,
    scenarios = 300
) {
    private val hashTable = ConcurrentHashTable<Int, Int>(initialCapacity = 2)

    @Operation
    fun put(key: Int, value: Int): Int? = hashTable.put(key, value)

    @Operation
    fun get(key: Int): Int? = hashTable.get(key)

    @Operation
    fun remove(key: Int): Int? = hashTable.remove(key)
}

class SequentialHashTableIntInt {
    private val map = HashMap<Int, Int>()

    fun put(key: Int, value: Int): Int? = map.put(key, value)

    fun get(key: Int): Int? = map.get(key)

    fun remove(key: Int): Int? = map.remove(key)
}