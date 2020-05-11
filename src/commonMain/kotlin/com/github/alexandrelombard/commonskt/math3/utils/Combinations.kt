package com.github.alexandrelombard.commonskt.math3.utils


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
import com.github.alexandrelombard.commonskt.math3.exception.DimensionMismatchException
import com.github.alexandrelombard.commonskt.math3.exception.MathInternalError
import com.github.alexandrelombard.commonskt.math3.utils.MathArrays.natural
import org.apache.commons.math3.exception.DimensionMismatchException
import org.apache.commons.math3.exception.MathInternalError
import org.apache.commons.math3.exception.OutOfRangeException


/**
 * Utility to create [
 * combinations](http://en.wikipedia.org/wiki/Combination) `(n, k)` of `k` elements in a set of
 * `n` elements.
 *
 * @since 3.3
 */
class Combinations private constructor(
    n: Int,
    k: Int,
    iterationOrder: IterationOrder
) : Iterable<IntArray?> {
    /**
     * Gets the size of the set from which combinations are drawn.
     *
     * @return the size of the universe.
     */
    /** Size of the set from which combinations are drawn.  */
    val n: Int

    /**
     * Gets the number of elements in each combination.
     *
     * @return the size of the subsets to be enumerated.
     */
    /** Number of elements in each combination.  */
    val k: Int

    /** Iteration order.  */
    private val iterationOrder: IterationOrder

    /**
     * Describes the type of iteration performed by the
     * [iterator][.iterator].
     */
    private enum class IterationOrder {
        /** Lexicographic order.  */
        LEXICOGRAPHIC
    }

    /**
     * Creates an instance whose range is the k-element subsets of
     * {0, ..., n - 1} represented as `int[]` arrays.
     *
     *
     * The iteration order is lexicographic: the arrays returned by the
     * [iterator][.iterator] are sorted in descending order and
     * they are visited in lexicographic order with significance from
     * right to left.
     * For example, `new Combinations(4, 2).iterator()` returns
     * an iterator that will generate the following sequence of arrays
     * on successive calls to
     * `next()`:<br></br>
     * `[0, 1], [0, 2], [1, 2], [0, 3], [1, 3], [2, 3]`
     *
     * If `k == 0` an iterator containing an empty array is returned;
     * if `k == n` an iterator containing [0, ..., n - 1] is returned.
     *
     * @param n Size of the set from which subsets are selected.
     * @param k Size of the subsets to be enumerated.
     * @throws org.apache.commons.math3.exception.NotPositiveException if `n < 0`.
     * @throws org.apache.commons.math3.exception.NumberIsTooLargeException if `k > n`.
     */
    constructor(
        n: Int,
        k: Int
    ) : this(n, k, IterationOrder.LEXICOGRAPHIC) {
    }

    /** {@inheritDoc}  */
    override fun iterator(): Iterator<IntArray> {
        return if (k == 0 ||
            k == n
        ) {
            SingletonIterator(natural(k))
        } else when (iterationOrder) {
            IterationOrder.LEXICOGRAPHIC -> LexicographicIterator(n, k)
            else -> throw MathInternalError() // Should never happen.
        }
    }

    /**
     * Defines a lexicographic ordering of combinations.
     * The returned comparator allows to compare any two combinations
     * that can be produced by this instance's [iterator][.iterator].
     * Its `compare(int[],int[])` method will throw exceptions if
     * passed combinations that are inconsistent with this instance:
     *
     *  * `DimensionMismatchException` if the array lengths are not
     * equal to `k`,
     *  * `OutOfRangeException` if an element of the array is not
     * within the interval [0, `n`).
     *
     * @return a lexicographic comparator.
     */
    fun comparator(): java.util.Comparator<IntArray> {
        return LexicographicComparator(n, k)
    }

    /**
     * Lexicographic combinations iterator.
     *
     *
     * Implementation follows Algorithm T in *The Art of Computer Programming*
     * Internet Draft (PRE-FASCICLE 3A), "A Draft of Section 7.2.1.3 Generating All
     * Combinations, D. Knuth, 2004.
     *
     *
     * The degenerate cases `k == 0` and `k == n` are NOT handled by this
     * implementation.  If constructor arguments satisfy `k == 0`
     * or `k >= n`, no exception is generated, but the iterator is empty.
     *
     *
     */
    private class LexicographicIterator internal constructor(
        n: Int,
        /** Size of subsets returned by the iterator  */
        private val k: Int
    ) :
        MutableIterator<IntArray?> {

        /**
         * c[1], ..., c[k] stores the next combination; c[k + 1], c[k + 2] are
         * sentinels.
         *
         *
         * Note that c[0] is "wasted" but this makes it a little easier to
         * follow the code.
         *
         */
        private val c: IntArray

        /** Return value for [.hasNext]  */
        private var more = true

        /** Marker: smallest index such that c[j + 1] > j  */
        private var j: Int

        /**
         * {@inheritDoc}
         */
        override fun hasNext(): Boolean {
            return more
        }

        /**
         * {@inheritDoc}
         */
        override fun next(): IntArray {
            if (!more) {
                throw NoSuchElementException()
            }
            // Copy return value (prepared by last activation)
            val ret = IntArray(k)
            java.lang.System.arraycopy(c, 1, ret, 0, k)

            // Prepare next iteration
            // T2 and T6 loop
            var x = 0
            if (j > 0) {
                x = j
                c[j] = x
                j--
                return ret
            }
            // T3
            j = if (c[1] + 1 < c[2]) {
                c[1]++
                return ret
            } else {
                2
            }
            // T4
            var stepDone = false
            while (!stepDone) {
                c[j - 1] = j - 2
                x = c[j] + 1
                if (x == c[j + 1]) {
                    j++
                } else {
                    stepDone = true
                }
            }
            // T5
            if (j > k) {
                more = false
                return ret
            }
            // T6
            c[j] = x
            j--
            return ret
        }

        /**
         * Not supported.
         */
        override fun remove() {
            throw UnsupportedOperationException()
        }

        /**
         * Construct a CombinationIterator to enumerate k-sets from n.
         *
         *
         * NOTE: If `k === 0` or `k >= n`, the Iterator will be empty
         * (that is, [.hasNext] will return `false` immediately.
         *
         *
         * @param n size of the set from which subsets are enumerated
         * @param k size of the subsets to enumerate
         */
        init {
            c = IntArray(k + 3)
            if (k == 0 || k >= n) {
                more = false
                return
            }
            // Initialize c to start with lexicographically first k-set
            for (i in 1..k) {
                c[i] = i - 1
            }
            // Initialize sentinels
            c[k + 1] = n
            c[k + 2] = 0
            j = k // Set up invariant: j is smallest index such that c[j + 1] > j
        }
    }

    /**
     * Iterator with just one element to handle degenerate cases (full array,
     * empty array) for combination iterator.
     */
    private class SingletonIterator
    /**
     * Create a singleton iterator providing the given array.
     * @param singleton array returned by the iterator
     */ internal constructor(
        /** Singleton array  */
        private val singleton: IntArray
    ) :
        MutableIterator<IntArray?> {

        /** True on initialization, false after first call to next  */
        private var more = true

        /** @return True until next is called the first time, then false
         */
        override fun hasNext(): Boolean {
            return more
        }

        /** @return the singleton in first activation; throws NSEE thereafter
         */
        override fun next(): IntArray {
            return if (more) {
                more = false
                singleton
            } else {
                throw NoSuchElementException()
            }
        }

        /** Not supported  */
        override fun remove() {
            throw UnsupportedOperationException()
        }

    }

    /**
     * Defines the lexicographic ordering of combinations, using
     * the [.lexNorm] method.
     */
    private class LexicographicComparator
    /**
     * @param n Size of the set from which subsets are selected.
     * @param k Size of the subsets to be enumerated.
     */ internal constructor(
        /** Size of the set from which combinations are drawn.  */
        private val n: Int,
        /** Number of elements in each combination.  */
        private val k: Int
    ) : java.util.Comparator<IntArray?>,
        java.io.Serializable {

        /**
         * {@inheritDoc}
         *
         * @throws DimensionMismatchException if the array lengths are not
         * equal to `k`.
         * @throws OutOfRangeException if an element of the array is not
         * within the interval [0, `n`).
         */
        override fun compare(
            c1: IntArray,
            c2: IntArray
        ): Int {
            if (c1.size != k) {
                throw DimensionMismatchException(c1.size, k)
            }
            if (c2.size != k) {
                throw DimensionMismatchException(c2.size, k)
            }

            // Method "lexNorm" works with ordered arrays.
            val c1s = MathArrays.copyOf(c1)
            Arrays.sort(c1s)
            val c2s = MathArrays.copyOf(c2)
            Arrays.sort(c2s)
            val v1 = lexNorm(c1s)
            val v2 = lexNorm(c2s)
            return if (v1 < v2) {
                -1
            } else if (v1 > v2) {
                1
            } else {
                0
            }
        }

        /**
         * Computes the value (in base 10) represented by the digit
         * (interpreted in base `n`) in the input array in reverse
         * order.
         * For example if `c` is `{3, 2, 1}`, and `n`
         * is 3, the method will return 18.
         *
         * @param c Input array.
         * @return the lexicographic norm.
         * @throws OutOfRangeException if an element of the array is not
         * within the interval [0, `n`).
         */
        private fun lexNorm(c: IntArray): Long {
            var ret: Long = 0
            for (i in c.indices) {
                val digit = c[i]
                if (digit < 0 ||
                    digit >= n
                ) {
                    throw OutOfRangeException(digit, 0, n - 1)
                }
                ret += c[i] * ArithmeticUtils.pow(n, i)
            }
            return ret
        }

        companion object {
            /** Serializable version identifier.  */
            private const val serialVersionUID = 20130906L
        }

    }

    /**
     * Creates an instance whose range is the k-element subsets of
     * {0, ..., n - 1} represented as `int[]` arrays.
     *
     *
     * If the `iterationOrder` argument is set to
     * [IterationOrder.LEXICOGRAPHIC], the arrays returned by the
     * [iterator][.iterator] are sorted in descending order and
     * they are visited in lexicographic order with significance from
     * right to left.
     * For example, `new Combinations(4, 2).iterator()` returns
     * an iterator that will generate the following sequence of arrays
     * on successive calls to
     * `next()`:<br></br>
     * `[0, 1], [0, 2], [1, 2], [0, 3], [1, 3], [2, 3]`
     *
     * If `k == 0` an iterator containing an empty array is returned;
     * if `k == n` an iterator containing [0, ..., n - 1] is returned.
     *
     * @param n Size of the set from which subsets are selected.
     * @param k Size of the subsets to be enumerated.
     * @param iterationOrder Specifies the [iteration order][.iterator].
     * @throws org.apache.commons.math3.exception.NotPositiveException if `n < 0`.
     * @throws org.apache.commons.math3.exception.NumberIsTooLargeException if `k > n`.
     */
    init {
        CombinatoricsUtils.checkBinomial(n, k)
        this.n = n
        this.k = k
        this.iterationOrder = iterationOrder
    }
}
