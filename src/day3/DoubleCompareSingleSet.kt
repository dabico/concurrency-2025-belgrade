package day3

/**
 * Interface representing a Double Compare Single Set (DCSS) operation.
 * Provides atomic operations for managing two values A and B of type [E].
 *
 * @param E The type of elements stored in this data structure
 */
interface DoubleCompareSingleSet<E> {
    /**
     * Gets the current value of A.
     */
    fun getA(): E

    /**
     * Performs a Double Compare Single Set operation.
     * Atomically updates value A to [updateA] if A equals [expectedA] and B equals [expectedB].
     *
     * @return true if the operation was successful, false otherwise
     */
    fun dcss(expectedA: E, updateA: E, expectedB: E): Boolean

    /**
     * Sets the value of B.
     */
    fun setB(value: E)

    /**
     * Gets the current value of B.
     */
    fun getB(): E
}