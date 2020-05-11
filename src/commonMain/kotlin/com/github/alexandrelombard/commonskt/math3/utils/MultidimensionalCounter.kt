package com.github.alexandrelombard.commonskt.math3.utils
import com.github.alexandrelombard.commonskt.math3.exception.DimensionMismatchException

import com.github.alexandrelombard.commonskt.math3.exception.NotStrictlyPositiveException


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

import org.apache.commons.math3.exception.DimensionMismatchException
import org.apache.commons.math3.exception.NotStrictlyPositiveException
import org.apache.commons.math3.exception.OutOfRangeException


/**
 * Converter between unidimensional storage structure and multidimensional
 * conceptual structure.
 * This utility will convert from indices in a multidimensional structure
 * to the corresponding index in a one-dimensional array. For example,
 * assuming that the ranges (in 3 dimensions) of indices are 2, 4 and 3,
 * the following correspondences, between 3-tuples indices and unidimensional
 * indices, will hold:
 *
 *  * (0, 0, 0) corresponds to 0
 *  * (0, 0, 1) corresponds to 1
 *  * (0, 0, 2) corresponds to 2
 *  * (0, 1, 0) corresponds to 3
 *  * ...
 *  * (1, 0, 0) corresponds to 12
 *  * ...
 *  * (1, 3, 2) corresponds to 23
 *
 *
 * @since 2.2
 */
class MultidimensionalCounter(vararg size: Int) : Iterable<Int?> {
    /**
     * Get the number of dimensions of the multidimensional counter.
     *
     * @return the number of dimensions.
     */
    /**
     * Number of dimensions.
     */
    val dimension: Int

    /**
     * Offset for each dimension.
     */
    private val uniCounterOffset: IntArray

    /**
     * Counter sizes.
     */
    private val size: IntArray

    /**
     * Get the total number of elements.
     *
     * @return the total size of the unidimensional counter.
     */
    /**
     * Total number of (one-dimensional) slots.
     */
    val size: Int

    /**
     * Index of last dimension.
     */
    private val last: Int

    /**
     * Perform iteration over the multidimensional counter.
     */
    inner class Iterator internal constructor() : MutableIterator<Int?> {
        /**
         * Multidimensional counter.
         */
        private val counter = IntArray(dimension)

        /**
         * Get the current unidimensional counter slot.
         *
         * @return the index within the unidimensionl counter.
         */
        /**
         * Unidimensional counter.
         */
        var count = -1
            private set

        /**
         * Maximum value for [.count].
         */
        private val maxCount: Int = this.size - 1

        /**
         * {@inheritDoc}
         */
        override fun hasNext(): Boolean {
            return count < maxCount
        }

        /**
         * @return the unidimensional count after the counter has been
         * incremented by `1`.
         * @throws NoSuchElementException if [.hasNext] would have
         * returned `false`.
         */
        override fun next(): Int {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            for (i in last downTo 0) {
                if (counter[i] == size.get(i) - 1) {
                    counter[i] = 0
                } else {
                    ++counter[i]
                    break
                }
            }
            return ++count
        }

        /**
         * Get the current multidimensional counter slots.
         *
         * @return the indices within the multidimensional counter.
         */
        val counts: IntArray
            get() = MathArrays.copyOf(counter)

        /**
         * Get the current count in the selected dimension.
         *
         * @param dim Dimension index.
         * @return the count at the corresponding index for the current state
         * of the iterator.
         * @throws IndexOutOfBoundsException if `index` is not in the
         * correct interval (as defined by the length of the argument in the
         * [ constructor of the enclosing class][MultidimensionalCounter.MultidimensionalCounter]).
         */
        fun getCount(dim: Int): Int {
            return counter[dim]
        }

        /**
         * @throws UnsupportedOperationException
         */
        override fun remove() {
            throw UnsupportedOperationException()
        }

        /**
         * Create an iterator
         * @see .iterator
         */
        init {
            counter[last] = -1
        }
    }

    /**
     * Create an iterator over this counter.
     *
     * @return the iterator.
     */
    override fun iterator(): Iterator {
        return Iterator()
    }

    /**
     * Convert to multidimensional counter.
     *
     * @param index Index in unidimensional counter.
     * @return the multidimensional counts.
     * @throws OutOfRangeException if `index` is not between
     * `0` and the value returned by [.getSize] (excluded).
     */
    @Throws(OutOfRangeException::class)
    fun getCounts(index: Int): IntArray {
        if (index < 0 ||
            index >= this.size
        ) {
            throw OutOfRangeException(index, 0, this.size)
        }
        val indices = IntArray(dimension)
        var count = 0
        for (i in 0 until last) {
            var idx = 0
            val offset = uniCounterOffset[i]
            while (count <= index) {
                count += offset
                ++idx
            }
            --idx
            count -= offset
            indices[i] = idx
        }
        indices[last] = index - count
        return indices
    }

    /**
     * Convert to unidimensional counter.
     *
     * @param c Indices in multidimensional counter.
     * @return the index within the unidimensionl counter.
     * @throws DimensionMismatchException if the size of `c`
     * does not match the size of the array given in the constructor.
     * @throws OutOfRangeException if a value of `c` is not in
     * the range of the corresponding dimension, as defined in the
     * [constructor][MultidimensionalCounter.MultidimensionalCounter].
     */
    @Throws(OutOfRangeException::class, DimensionMismatchException::class)
    fun getCount(vararg c: Int): Int {
        if (c.size != dimension) {
            throw DimensionMismatchException(c.size, dimension)
        }
        var count = 0
        for (i in 0 until dimension) {
            val index = c[i]
            if (index < 0 ||
                index >= size.get(i)
            ) {
                throw OutOfRangeException(index, 0, size.get(i) - 1)
            }
            count += uniCounterOffset[i] * c[i]
        }
        return count + c[last]
    }

    /**
     * Get the number of multidimensional counter slots in each dimension.
     *
     * @return the sizes of the multidimensional counter in each dimension.
     */
    val sizes: IntArray
        get() = MathArrays.copyOf(size)

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        val sb: java.lang.StringBuilder = java.lang.StringBuilder()
        for (i in 0 until dimension) {
            sb.append("[").append(getCount(i)).append("]")
        }
        return sb.toString()
    }

    /**
     * Create a counter.
     *
     * @param size Counter sizes (number of slots in each dimension).
     * @throws NotStrictlyPositiveException if one of the sizes is
     * negative or zero.
     */
    init {
        dimension = size.size
        this.size = MathArrays.copyOf(size)
        uniCounterOffset = IntArray(dimension)
        last = dimension - 1
        var tS = size[last]
        for (i in 0 until last) {
            var count = 1
            for (j in i + 1 until dimension) {
                count *= size[j]
            }
            uniCounterOffset[i] = count
            tS *= size[i]
        }
        uniCounterOffset[last] = 0
        if (tS <= 0) {
            throw NotStrictlyPositiveException(tS)
        }
        this.size = tS
    }
}
