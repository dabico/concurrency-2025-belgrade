package day4

/**
 * K -- key type, V -- value type.
 */
interface HashTable<K : Any, V : Any> {
    /**
     * Associates the specified value with the specified key in this map.
     * Returns the previous value associated with key, or `null` if there was no mapping for key.
     */
    fun put(key: K, value: V): V?

    /**
     * Returns the value to which the specified key is mapped,
     * or `null` if this map contains no mapping for the key.
     */
    fun get(key: K): V?

    /**
     * Removes the mapping for the specified key from this map if present.
     * Returns the previous value associated with key, or `null` if there was no mapping for key.
     */
    fun remove(key: K): V?
}