package com.github.alexandrelombard.commonskt.math3.utils

import com.github.alexandrelombard.commonskt.math3.utils.FastMath.ceil
import kotlin.jvm.JvmOverloads
import kotlin.jvm.Transient

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



/**
 * Open addressed map from int to double.
 *
 * This class provides a dedicated map from integers to doubles with a
 * much smaller memory overhead than standard `java.util.Map`.
 *
 * This class is not synchronized. The specialized iterators returned by
 * [.iterator] are fail-fast: they throw a
 * `ConcurrentModificationException` when they detect the map has been
 * modified during iteration.
 * @since 2.0
 */
class OpenIntToDoubleHashMap {
    /** Keys table.  */
    private var keys: IntArray

    /** Values table.  */
    private var values: DoubleArray

    /** States table.  */
    private var states: ByteArray

    /** Return value for missing entries.  */
    private val missingEntries: Double

    /** Current size of the map.  */
    private var size = 0

    /** Bit mask for hash values.  */
    private var mask: Int

    /** Modifications count.  */
    @Transient
    private var count = 0

    /**
     * Build an empty map with default size
     * @param missingEntries value to return when a missing entry is fetched
     */
    @ExperimentalStdlibApi
    constructor(missingEntries: Double) : this(
        DEFAULT_EXPECTED_SIZE,
        missingEntries
    )
    /**
     * Build an empty map with specified size.
     * @param expectedSize expected number of elements in the map
     * @param missingEntries value to return when a missing entry is fetched
     */
    @ExperimentalStdlibApi
    constructor(
        expectedSize: Int = DEFAULT_EXPECTED_SIZE,
        missingEntries: Double = Double.NaN
    ) {
        val capacity = computeCapacity(expectedSize)
        keys = IntArray(capacity)
        values = DoubleArray(capacity)
        states = ByteArray(capacity)
        this.missingEntries = missingEntries
        mask = capacity - 1
    }

    /**
     * Copy constructor.
     * @param source map to copy
     */
    constructor(source: OpenIntToDoubleHashMap) {
        val length = source.keys.size
        keys = IntArray(length)
        source.keys.copyInto(keys, 0, 0, length)
        values = DoubleArray(length)
        source.values.copyInto(values, 0, 0, length)
        states = ByteArray(length)
        source.states.copyInto(states, 0, length)
        missingEntries = source.missingEntries
        size = source.size
        mask = source.mask
        count = source.count
    }

    /**
     * Get the stored value associated with the given key
     * @param key key associated with the data
     * @return data associated with the key
     */
    operator fun get(key: Int): Double {
        val hash = hashOf(key)
        var index = hash and mask
        if (containsKey(key, index)) {
            return values[index]
        }
        if (states[index] == FREE) {
            return missingEntries
        }
        var j = index
        var perturb = perturb(hash)
        while (states[index] != FREE) {
            j = probe(perturb, j)
            index = j and mask
            if (containsKey(key, index)) {
                return values[index]
            }
            perturb = perturb shr PERTURB_SHIFT
        }
        return missingEntries
    }

    /**
     * Check if a value is associated with a key.
     * @param key key to check
     * @return true if a value is associated with key
     */
    fun containsKey(key: Int): Boolean {
        val hash = hashOf(key)
        var index = hash and mask
        if (containsKey(key, index)) {
            return true
        }
        if (states[index] == FREE) {
            return false
        }
        var j = index
        var perturb = perturb(hash)
        while (states[index] != FREE) {
            j = probe(perturb, j)
            index = j and mask
            if (containsKey(key, index)) {
                return true
            }
            perturb = perturb shr PERTURB_SHIFT
        }
        return false
    }

    /**
     * Get an iterator over map elements.
     *
     * The specialized iterators returned are fail-fast: they throw a
     * `ConcurrentModificationException` when they detect the map
     * has been modified during iteration.
     * @return iterator over the map elements
     */
    operator fun iterator(): Iterator {
        return Iterator()
    }

    /**
     * Find the index at which a key should be inserted
     * @param key key to lookup
     * @return index at which key should be inserted
     */
    private fun findInsertionIndex(key: Int): Int {
        return findInsertionIndex(keys, states, key, mask)
    }

    /**
     * Get the number of elements stored in the map.
     * @return number of elements stored in the map
     */
    fun size(): Int {
        return size
    }

    /**
     * Remove the value associated with a key.
     * @param key key to which the value is associated
     * @return removed value
     */
    fun remove(key: Int): Double {
        val hash = hashOf(key)
        var index = hash and mask
        if (containsKey(key, index)) {
            return doRemove(index)
        }
        if (states[index] == FREE) {
            return missingEntries
        }
        var j = index
        var perturb = perturb(hash)
        while (states[index] != FREE) {
            j = probe(perturb, j)
            index = j and mask
            if (containsKey(key, index)) {
                return doRemove(index)
            }
            perturb = perturb shr PERTURB_SHIFT
        }
        return missingEntries
    }

    /**
     * Check if the tables contain an element associated with specified key
     * at specified index.
     * @param key key to check
     * @param index index to check
     * @return true if an element is associated with key at index
     */
    private fun containsKey(key: Int, index: Int): Boolean {
        return (key != 0 || states[index] == FULL) && keys[index] == key
    }

    /**
     * Remove an element at specified index.
     * @param index index of the element to remove
     * @return removed value
     */
    private fun doRemove(index: Int): Double {
        keys[index] = 0
        states[index] = REMOVED
        val previous = values[index]
        values[index] = missingEntries
        --size
        ++count
        return previous
    }

    /**
     * Put a value associated with a key in the map.
     * @param key key to which value is associated
     * @param value value to put in the map
     * @return previous value associated with the key
     */
    fun put(key: Int, value: Double): Double {
        var index = findInsertionIndex(key)
        var previous = missingEntries
        var newMapping = true
        if (index < 0) {
            index = changeIndexSign(index)
            previous = values[index]
            newMapping = false
        }
        keys[index] = key
        states[index] = FULL
        values[index] = value
        if (newMapping) {
            ++size
            if (shouldGrowTable()) {
                growTable()
            }
            ++count
        }
        return previous
    }

    /**
     * Grow the tables.
     */
    private fun growTable() {
        val oldLength = states.size
        val oldKeys = keys
        val oldValues = values
        val oldStates = states
        val newLength = RESIZE_MULTIPLIER * oldLength
        val newKeys = IntArray(newLength)
        val newValues = DoubleArray(newLength)
        val newStates = ByteArray(newLength)
        val newMask = newLength - 1
        for (i in 0 until oldLength) {
            if (oldStates[i] == FULL) {
                val key = oldKeys[i]
                val index = findInsertionIndex(newKeys, newStates, key, newMask)
                newKeys[index] = key
                newValues[index] = oldValues[i]
                newStates[index] = FULL
            }
        }
        mask = newMask
        keys = newKeys
        values = newValues
        states = newStates
    }

    /**
     * Check if tables should grow due to increased size.
     * @return true if  tables should grow
     */
    private fun shouldGrowTable(): Boolean {
        return size > (mask + 1) * LOAD_FACTOR
    }

    /** Iterator class for the map.  */
    inner class Iterator() {
        /** Reference modification count.  */
        private val referenceCount: Int

        /** Index of current element.  */
        private var current = 0

        /** Index of next element.  */
        private var next: Int

        /**
         * Check if there is a next element in the map.
         * @return true if there is a next element
         */
        operator fun hasNext(): Boolean {
            return next >= 0
        }

        /**
         * Get the key of current entry.
         * @return key of current entry
         * @exception ConcurrentModificationException if the map is modified during iteration
         * @exception NoSuchElementException if there is no element left in the map
         */
        fun key(): Int {
            if (referenceCount != count) {
                throw ConcurrentModificationException()
            }
            if (current < 0) {
                throw NoSuchElementException()
            }
            return keys[current]
        }

        /**
         * Get the value of current entry.
         * @return value of current entry
         * @exception ConcurrentModificationException if the map is modified during iteration
         * @exception NoSuchElementException if there is no element left in the map
         */
        fun value(): Double {
            if (referenceCount != count) {
                throw ConcurrentModificationException()
            }
            if (current < 0) {
                throw NoSuchElementException()
            }
            return values[current]
        }

        /**
         * Advance iterator one step further.
         * @exception ConcurrentModificationException if the map is modified during iteration
         * @exception NoSuchElementException if there is no element left in the map
         */
        fun advance() {
            if (referenceCount != count) {
                throw ConcurrentModificationException()
            }

            // advance on step
            current = next

            // prepare next step
            try {
                while (states[++next] != FULL) { // NOPMD
                    // nothing to do
                }
            } catch (e: IndexOutOfBoundsException) {
                next = -2
                if (current < 0) {
                    throw NoSuchElementException()
                }
            }
        }

        /**
         * Simple constructor.
         */
        init {

            // preserve the modification count of the map to detect concurrent modifications later
            referenceCount = count

            // initialize current index
            next = -1
            try {
                advance()
            } catch (nsee: NoSuchElementException) { // NOPMD
                // ignored
            }
        }
    }

    companion object {
        /** Status indicator for free table entries.  */
        protected const val FREE: Byte = 0

        /** Status indicator for full table entries.  */
        protected const val FULL: Byte = 1

        /** Status indicator for removed table entries.  */
        protected const val REMOVED: Byte = 2

        /** Serializable version identifier  */
        private const val serialVersionUID = -3646337053166149105L

        /** Load factor for the map.  */
        private const val LOAD_FACTOR = 0.5f

        /** Default starting size.
         *
         * This must be a power of two for bit mask to work properly.
         */
        private const val DEFAULT_EXPECTED_SIZE = 16

        /** Multiplier for size growth when map fills up.
         *
         * This must be a power of two for bit mask to work properly.
         */
        private const val RESIZE_MULTIPLIER = 2

        /** Number of bits to perturb the index when probing for collision resolution.  */
        private const val PERTURB_SHIFT = 5

        /**
         * Compute the capacity needed for a given size.
         * @param expectedSize expected size of the map
         * @return capacity to use for the specified size
         */
        @ExperimentalStdlibApi
        private fun computeCapacity(expectedSize: Int): Int {
            if (expectedSize == 0) {
                return 1
            }
            val capacity =
                ceil(expectedSize / LOAD_FACTOR.toDouble()).toInt()
            val powerOfTwo: Int = capacity.takeHighestOneBit()
            return if (powerOfTwo == capacity) {
                capacity
            } else nextPowerOfTwo(capacity)
        }

        /**
         * Find the smallest power of two greater than the input value
         * @param i input value
         * @return smallest power of two greater than the input value
         */
        @ExperimentalStdlibApi
        private fun nextPowerOfTwo(i: Int): Int {
            return i.takeHighestOneBit() shl 1
        }

        /**
         * Perturb the hash for starting probing.
         * @param hash initial hash
         * @return perturbed hash
         */
        private fun perturb(hash: Int): Int {
            return hash and 0x7fffffff
        }

        /**
         * Find the index at which a key should be inserted
         * @param keys keys table
         * @param states states table
         * @param key key to lookup
         * @param mask bit mask for hash values
         * @return index at which key should be inserted
         */
        private fun findInsertionIndex(
            keys: IntArray, states: ByteArray,
            key: Int, mask: Int
        ): Int {
            val hash = hashOf(key)
            var index = hash and mask
            if (states[index] == FREE) {
                return index
            } else if (states[index] == FULL && keys[index] == key) {
                return changeIndexSign(index)
            }
            var perturb = perturb(hash)
            var j = index
            if (states[index] == FULL) {
                while (true) {
                    j = probe(perturb, j)
                    index = j and mask
                    perturb = perturb shr PERTURB_SHIFT
                    if (states[index] != FULL || keys[index] == key) {
                        break
                    }
                }
            }
            if (states[index] == FREE) {
                return index
            } else if (states[index] == FULL) {
                // due to the loop exit condition,
                // if (states[index] == FULL) then keys[index] == key
                return changeIndexSign(index)
            }
            val firstRemoved = index
            while (true) {
                j = probe(perturb, j)
                index = j and mask
                if (states[index] == FREE) {
                    return firstRemoved
                } else if (states[index] == FULL && keys[index] == key) {
                    return changeIndexSign(index)
                }
                perturb = perturb shr PERTURB_SHIFT
            }
        }

        /**
         * Compute next probe for collision resolution
         * @param perturb perturbed hash
         * @param j previous probe
         * @return next probe
         */
        private fun probe(perturb: Int, j: Int): Int {
            return (j shl 2) + j + perturb + 1
        }

        /**
         * Change the index sign
         * @param index initial index
         * @return changed index
         */
        private fun changeIndexSign(index: Int): Int {
            return -index - 1
        }

        /**
         * Compute the hash value of a key
         * @param key key to hash
         * @return hash value of the key
         */
        private fun hashOf(key: Int): Int {
            val h = key xor (key ushr 20 xor (key ushr 12))
            return h xor (h ushr 7) xor (h ushr 4)
        }
    }
}
