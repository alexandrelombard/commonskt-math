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
package com.github.alexandrelombard.commonskt.math3.utils

import com.github.alexandrelombard.commonskt.math3.exception.*
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.Field
import org.apache.commons.math3.distribution.UniformIntegerDistribution
import org.apache.commons.math3.random.RandomGenerator
import org.apache.commons.math3.random.Well19937c
import kotlin.math.sqrt

/**
 * Arrays utilities.
 *
 * @since 3.0
 */
object MathArrays {
    /**
     * Create a copy of an array scaled by a value.
     *
     * @param arr Array to scale.
     * @param val Scalar.
     * @return scaled copy of array with each entry multiplied by val.
     * @since 3.2
     */
    fun scale(`val`: Double, arr: DoubleArray): DoubleArray {
        val newArr = DoubleArray(arr.size)
        for (i in arr.indices) {
            newArr[i] = arr[i] * `val`
        }
        return newArr
    }

    /**
     *
     * Multiply each element of an array by a value.
     *
     *
     * The array is modified in place (no copy is created).
     *
     * @param arr Array to scale
     * @param val Scalar
     * @since 3.2
     */
    fun scaleInPlace(`val`: Double, arr: DoubleArray) {
        for (i in arr.indices) {
            arr[i] *= `val`
        }
    }

    /**
     * Creates an array whose contents will be the element-by-element
     * addition of the arguments.
     *
     * @param a First term of the addition.
     * @param b Second term of the addition.
     * @return a new array `r` where `r[i] = a[i] + b[i]`.
     * @throws DimensionMismatchException if the array lengths differ.
     * @since 3.1
     */
    fun ebeAdd(a: DoubleArray, b: DoubleArray): DoubleArray {
        checkEqualLength(a, b)
        val result: DoubleArray = a.copyOf()
        for (i in a.indices) {
            result[i] += b[i]
        }
        return result
    }

    /**
     * Creates an array whose contents will be the element-by-element
     * subtraction of the second argument from the first.
     *
     * @param a First term.
     * @param b Element to be subtracted.
     * @return a new array `r` where `r[i] = a[i] - b[i]`.
     * @throws DimensionMismatchException if the array lengths differ.
     * @since 3.1
     */
    fun ebeSubtract(a: DoubleArray, b: DoubleArray): DoubleArray {
        checkEqualLength(a, b)
        val result: DoubleArray = a.copyOf()
        for (i in a.indices) {
            result[i] -= b[i]
        }
        return result
    }

    /**
     * Creates an array whose contents will be the element-by-element
     * multiplication of the arguments.
     *
     * @param a First factor of the multiplication.
     * @param b Second factor of the multiplication.
     * @return a new array `r` where `r[i] = a[i] * b[i]`.
     * @throws DimensionMismatchException if the array lengths differ.
     * @since 3.1
     */
    fun ebeMultiply(a: DoubleArray, b: DoubleArray): DoubleArray {
        checkEqualLength(a, b)
        val result: DoubleArray = a.copyOf()
        for (i in a.indices) {
            result[i] *= b[i]
        }
        return result
    }

    /**
     * Creates an array whose contents will be the element-by-element
     * division of the first argument by the second.
     *
     * @param a Numerator of the division.
     * @param b Denominator of the division.
     * @return a new array `r` where `r[i] = a[i] / b[i]`.
     * @throws DimensionMismatchException if the array lengths differ.
     * @since 3.1
     */
    fun ebeDivide(a: DoubleArray, b: DoubleArray): DoubleArray {
        checkEqualLength(a, b)
        val result: DoubleArray = a.copyOf()
        for (i in a.indices) {
            result[i] /= b[i]
        }
        return result
    }

    /**
     * Calculates the L<sub>1</sub> (sum of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>1</sub> distance between the two points
     * @throws DimensionMismatchException if the array lengths differ.
     */
    fun distance1(p1: DoubleArray, p2: DoubleArray): Double {
        checkEqualLength(p1, p2)
        var sum = 0.0
        for (i in p1.indices) {
            sum += FastMath.abs(p1[i] - p2[i])
        }
        return sum
    }

    /**
     * Calculates the L<sub>1</sub> (sum of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>1</sub> distance between the two points
     * @throws DimensionMismatchException if the array lengths differ.
     */
    fun distance1(p1: IntArray, p2: IntArray): Int {
        checkEqualLength(p1, p2)
        var sum = 0
        for (i in p1.indices) {
            sum += FastMath.abs(p1[i] - p2[i])
        }
        return sum
    }

    /**
     * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>2</sub> distance between the two points
     * @throws DimensionMismatchException if the array lengths differ.
     */
    fun distance(p1: DoubleArray, p2: DoubleArray): Double {
        checkEqualLength(p1, p2)
        var sum = 0.0
        for (i in p1.indices) {
            val dp = p1[i] - p2[i]
            sum += dp * dp
        }
        return FastMath.sqrt(sum)
    }

    /**
     * Calculates the cosine of the angle between two vectors.
     *
     * @param v1 Cartesian coordinates of the first vector.
     * @param v2 Cartesian coordinates of the second vector.
     * @return the cosine of the angle between the vectors.
     * @since 3.6
     */
    fun cosAngle(v1: DoubleArray, v2: DoubleArray): Double {
        return linearCombination(v1, v2) / (safeNorm(v1) * safeNorm(v2))
    }

    /**
     * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub>2</sub> distance between the two points
     * @throws DimensionMismatchException if the array lengths differ.
     */
    fun distance(p1: IntArray, p2: IntArray): Double {
        checkEqualLength(p1, p2)
        var sum = 0.0
        for (i in p1.indices) {
            val dp = p1[i] - p2[i].toDouble()
            sum += dp * dp
        }
        return FastMath.sqrt(sum)
    }

    /**
     * Calculates the L<sub></sub> (max of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub></sub> distance between the two points
     * @throws DimensionMismatchException if the array lengths differ.
     */
    fun distanceInf(p1: DoubleArray, p2: DoubleArray): Double {
        checkEqualLength(p1, p2)
        var max = 0.0
        for (i in p1.indices) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]))
        }
        return max
    }

    /**
     * Calculates the L<sub></sub> (max of abs) distance between two points.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the L<sub></sub> distance between the two points
     * @throws DimensionMismatchException if the array lengths differ.
     */
    fun distanceInf(p1: IntArray, p2: IntArray): Int {
        checkEqualLength(p1, p2)
        var max = 0
        for (i in p1.indices) {
            max = FastMath.max(max, FastMath.abs(p1[i] - p2[i]))
        }
        return max
    }

    /**
     * Check that an array is monotonically increasing or decreasing.
     *
     * @param <T> the type of the elements in the specified array
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @return `true` if sorted, `false` otherwise.
    </T> */
    fun <T : Comparable<T>?> isMonotonic(
        `val`: Array<T>,
        dir: OrderDirection?,
        strict: Boolean
    ): Boolean {
        var previous = `val`[0]
        val max = `val`.size
        for (i in 1 until max) {
            val comp: Int
            when (dir) {
                OrderDirection.INCREASING -> {
                    comp = previous!!.compareTo(`val`[i])
                    if (strict) {
                        if (comp >= 0) {
                            return false
                        }
                    } else {
                        if (comp > 0) {
                            return false
                        }
                    }
                }
                OrderDirection.DECREASING -> {
                    comp = `val`[i]!!.compareTo(previous)
                    if (strict) {
                        if (comp >= 0) {
                            return false
                        }
                    } else {
                        if (comp > 0) {
                            return false
                        }
                    }
                }
                else -> throw MathInternalError()
            }
            previous = `val`[i]
        }
        return true
    }

    /**
     * Check that an array is monotonically increasing or decreasing.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @return `true` if sorted, `false` otherwise.
     */
    fun isMonotonic(`val`: DoubleArray, dir: OrderDirection?, strict: Boolean): Boolean {
        return checkOrder(`val`, dir, strict, false)
    }

    /**
     * Check that both arrays have the same length.
     *
     * @param a Array.
     * @param b Array.
     * @param abort Whether to throw an exception if the check fails.
     * @return `true` if the arrays have the same length.
     * @throws DimensionMismatchException if the lengths differ and
     * `abort` is `true`.
     * @since 3.6
     */
    fun checkEqualLength(
        a: DoubleArray,
        b: DoubleArray,
        abort: Boolean
    ): Boolean {
        return if (a.size == b.size) {
            true
        } else {
            if (abort) {
                throw DimensionMismatchException(a.size, b.size)
            }
            false
        }
    }

    /**
     * Check that both arrays have the same length.
     *
     * @param a Array.
     * @param b Array.
     * @throws DimensionMismatchException if the lengths differ.
     * @since 3.6
     */
    fun checkEqualLength(
        a: DoubleArray,
        b: DoubleArray
    ) {
        checkEqualLength(a, b, true)
    }

    /**
     * Check that both arrays have the same length.
     *
     * @param a Array.
     * @param b Array.
     * @param abort Whether to throw an exception if the check fails.
     * @return `true` if the arrays have the same length.
     * @throws DimensionMismatchException if the lengths differ and
     * `abort` is `true`.
     * @since 3.6
     */
    fun checkEqualLength(
        a: IntArray,
        b: IntArray,
        abort: Boolean
    ): Boolean {
        return if (a.size == b.size) {
            true
        } else {
            if (abort) {
                throw DimensionMismatchException(a.size, b.size)
            }
            false
        }
    }

    /**
     * Check that both arrays have the same length.
     *
     * @param a Array.
     * @param b Array.
     * @throws DimensionMismatchException if the lengths differ.
     * @since 3.6
     */
    fun checkEqualLength(
        a: IntArray,
        b: IntArray
    ) {
        checkEqualLength(a, b, true)
    }

    /**
     * Check that the given array is sorted.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @param abort Whether to throw an exception if the check fails.
     * @return `true` if the array is sorted.
     * @throws NonMonotonicSequenceException if the array is not sorted
     * and `abort` is `true`.
     */
    fun checkOrder(
        `val`: DoubleArray, dir: OrderDirection?,
        strict: Boolean, abort: Boolean
    ): Boolean {
        var previous = `val`[0]
        val max = `val`.size
        var index: Int
        index = 1
        ITEM@ while (index < max) {
            when (dir) {
                OrderDirection.INCREASING -> if (strict) {
                    if (`val`[index] <= previous) {
                        break@ITEM
                    }
                } else {
                    if (`val`[index] < previous) {
                        break@ITEM
                    }
                }
                OrderDirection.DECREASING -> if (strict) {
                    if (`val`[index] >= previous) {
                        break@ITEM
                    }
                } else {
                    if (`val`[index] > previous) {
                        break@ITEM
                    }
                }
                else -> throw MathInternalError()
            }
            previous = `val`[index]
            index++
        }
        if (index == max) {
            // Loop completed.
            return true
        }

        // Loop early exit means wrong ordering.
        return if (abort) {
            throw NonMonotonicSequenceException(`val`[index], previous, index, dir, strict)
        } else {
            false
        }
    }
    /**
     * Check that the given array is sorted.
     *
     * @param val Values.
     * @param dir Ordering direction.
     * @param strict Whether the order should be strict.
     * @throws NonMonotonicSequenceException if the array is not sorted.
     * @since 2.2
     */
    /**
     * Check that the given array is sorted in strictly increasing order.
     *
     * @param val Values.
     * @throws NonMonotonicSequenceException if the array is not sorted.
     * @since 2.2
     */
    fun checkOrder(
        `val`: DoubleArray, dir: OrderDirection? = OrderDirection.INCREASING,
        strict: Boolean = true
    ) {
        checkOrder(`val`, dir, strict, true)
    }

    /**
     * Throws DimensionMismatchException if the input array is not rectangular.
     *
     * @param in array to be tested
     * @throws DimensionMismatchException if input array is not rectangular
     * @since 3.1
     */
    fun checkRectangular(`in`: Array<LongArray>) {
        MathUtils.checkNotNull(`in`)
        for (i in 1 until `in`.size) {
            if (`in`[i].size != `in`[0].size) {
                throw DimensionMismatchException(
                    LocalizedFormats.DIFFERENT_ROWS_LENGTHS,
                    `in`[i].size, `in`[0].size
                )
            }
        }
    }

    /**
     * Check that all entries of the input array are strictly positive.
     *
     * @param in Array to be tested
     * @throws NotStrictlyPositiveException if any entries of the array are not
     * strictly positive.
     * @since 3.1
     */
    fun checkPositive(`in`: DoubleArray) {
        for (i in `in`.indices) {
            if (`in`[i] <= 0) {
                throw NotStrictlyPositiveException(`in`[i])
            }
        }
    }

    /**
     * Check that no entry of the input array is `NaN`.
     *
     * @param in Array to be tested.
     * @throws NotANumberException if an entry is `NaN`.
     * @since 3.4
     */
    fun checkNotNaN(`in`: DoubleArray) {
        for (i in `in`.indices) {
            if (`in`[i].isNaN()) {
                throw NotANumberException()
            }
        }
    }

    /**
     * Check that all entries of the input array are >= 0.
     *
     * @param in Array to be tested
     * @throws NotPositiveException if any array entries are less than 0.
     * @since 3.1
     */
    fun checkNonNegative(`in`: LongArray) {
        for (i in `in`.indices) {
            if (`in`[i] < 0) {
                throw NotPositiveException(`in`[i])
            }
        }
    }

    /**
     * Check all entries of the input array are >= 0.
     *
     * @param in Array to be tested
     * @throws NotPositiveException if any array entries are less than 0.
     * @since 3.1
     */
    fun checkNonNegative(`in`: Array<LongArray>) {
        for (i in `in`.indices) {
            for (j in 0 until `in`[i].size) {
                if (`in`[i][j] < 0) {
                    throw NotPositiveException(`in`[i][j])
                }
            }
        }
    }

    /**
     * Returns the Cartesian norm (2-norm), handling both overflow and underflow.
     * Translation of the minpack enorm subroutine.
     *
     * The redistribution policy for MINPACK is available
     * [here](http://www.netlib.org/minpack/disclaimer), for
     * convenience, it is reproduced below.
     *
     * <table border="0" width="80%" cellpadding="10" align="center" bgcolor="#E0E0E0">
     * <tr><td>
     * Minpack Copyright Notice (1999) University of Chicago.
     * All rights reserved
    </td></tr> *
     * <tr><td>
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     *
     *  1. Redistributions of source code must retain the above copyright
     * notice, this list of conditions and the following disclaimer.
     *  1. Redistributions in binary form must reproduce the above
     * copyright notice, this list of conditions and the following
     * disclaimer in the documentation and/or other materials provided
     * with the distribution.
     *  1. The end-user documentation included with the redistribution, if any,
     * must include the following acknowledgment:
     * `This product includes software developed by the University of
     * Chicago, as Operator of Argonne National Laboratory.`
     * Alternately, this acknowledgment may appear in the software itself,
     * if and wherever such third-party acknowledgments normally appear.
     *  1. **WARRANTY DISCLAIMER. THE SOFTWARE IS SUPPLIED "AS IS"
     * WITHOUT WARRANTY OF ANY KIND. THE COPYRIGHT HOLDER, THE
     * UNITED STATES, THE UNITED STATES DEPARTMENT OF ENERGY, AND
     * THEIR EMPLOYEES: (1) DISCLAIM ANY WARRANTIES, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES
     * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE
     * OR NON-INFRINGEMENT, (2) DO NOT ASSUME ANY LEGAL LIABILITY
     * OR RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR
     * USEFULNESS OF THE SOFTWARE, (3) DO NOT REPRESENT THAT USE OF
     * THE SOFTWARE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS, (4)
     * DO NOT WARRANT THAT THE SOFTWARE WILL FUNCTION
     * UNINTERRUPTED, THAT IT IS ERROR-FREE OR THAT ANY ERRORS WILL
     * BE CORRECTED.**
     *  1. **LIMITATION OF LIABILITY. IN NO EVENT WILL THE COPYRIGHT
     * HOLDER, THE UNITED STATES, THE UNITED STATES DEPARTMENT OF
     * ENERGY, OR THEIR EMPLOYEES: BE LIABLE FOR ANY INDIRECT,
     * INCIDENTAL, CONSEQUENTIAL, SPECIAL OR PUNITIVE DAMAGES OF
     * ANY KIND OR NATURE, INCLUDING BUT NOT LIMITED TO LOSS OF
     * PROFITS OR LOSS OF DATA, FOR ANY REASON WHATSOEVER, WHETHER
     * SUCH LIABILITY IS ASSERTED ON THE BASIS OF CONTRACT, TORT
     * (INCLUDING NEGLIGENCE OR STRICT LIABILITY), OR OTHERWISE,
     * EVEN IF ANY OF SAID PARTIES HAS BEEN WARNED OF THE
     * POSSIBILITY OF SUCH LOSS OR DAMAGES.**
     * </td></tr>
    </table> *
     *
     * @param v Vector of doubles.
     * @return the 2-norm of the vector.
     * @since 2.2
     */
    fun safeNorm(v: DoubleArray): Double {
        val rdwarf = 3.834e-20
        val rgiant = 1.304e+19
        var s1 = 0.0
        var s2 = 0.0
        var s3 = 0.0
        var x1max = 0.0
        var x3max = 0.0
        val floatn = v.size.toDouble()
        val agiant = rgiant / floatn
        for (i in v.indices) {
            val xabs: Double = FastMath.abs(v[i])
            if (xabs < rdwarf || xabs > agiant) {
                if (xabs > rdwarf) {
                    if (xabs > x1max) {
                        val r = x1max / xabs
                        s1 = 1 + s1 * r * r
                        x1max = xabs
                    } else {
                        val r = xabs / x1max
                        s1 += r * r
                    }
                } else {
                    if (xabs > x3max) {
                        val r = x3max / xabs
                        s3 = 1 + s3 * r * r
                        x3max = xabs
                    } else {
                        if (xabs != 0.0) {
                            val r = xabs / x3max
                            s3 += r * r
                        }
                    }
                }
            } else {
                s2 += xabs * xabs
            }
        }
        val norm: Double
        if (s1 != 0.0) {
            norm = x1max * sqrt(s1 + s2 / x1max / x1max)
        } else {
            if (s2 == 0.0) {
                norm = x3max * sqrt(s3)
            } else {
                norm = if (s2 >= x3max) {
                    sqrt(s2 * (1 + x3max / s2 * (x3max * s3)))
                } else {
                    sqrt(x3max * (s2 / x3max + x3max * s3))
                }
            }
        }
        return norm
    }

    /**
     * Sort an array in ascending order in place and perform the same reordering
     * of entries on other arrays. For example, if
     * `x = [3, 1, 2], y = [1, 2, 3]` and `z = [0, 5, 7]`, then
     * `sortInPlace(x, y, z)` will update `x` to `[1, 2, 3]`,
     * `y` to `[2, 3, 1]` and `z` to `[5, 7, 0]`.
     *
     * @param x Array to be sorted and used as a pattern for permutation
     * of the other arrays.
     * @param yList Set of arrays whose permutations of entries will follow
     * those performed on `x`.
     * @throws DimensionMismatchException if any `y` is not the same
     * size as `x`.
     * @since 3.0
     */
    fun sortInPlace(x: DoubleArray, vararg yList: DoubleArray) {
        sortInPlace(x, OrderDirection.INCREASING, *yList)
    }

    /**
     * Sort an array in place and perform the same reordering of entries on
     * other arrays.  This method works the same as the other
     * [sortInPlace][.sortInPlace] method, but
     * allows the order of the sort to be provided in the `dir`
     * parameter.
     *
     * @param x Array to be sorted and used as a pattern for permutation
     * of the other arrays.
     * @param dir Order direction.
     * @param yList Set of arrays whose permutations of entries will follow
     * those performed on `x`.
     * @throws DimensionMismatchException if any `y` is not the same
     * size as `x`.
     * @since 3.0
     */
    fun sortInPlace(
        x: DoubleArray,
        dir: OrderDirection,
        vararg yList: DoubleArray
    ) {

        // Consistency checks.
        val yListLen = yList.size
        val len = x.size
        for (j in 0 until yListLen) {
            val y = yList[j]
            if (y.size != len) {
                throw DimensionMismatchException(y.size, len)
            }
        }

        // Associate each abscissa "x[i]" with its index "i".
        val list: MutableList<PairDoubleInteger> = MutableList(len) { i ->
            PairDoubleInteger(x[i], i)
        }

        // Create comparators for increasing and decreasing orders.
        val comp: Comparator<PairDoubleInteger> =
            if (dir == OrderDirection.INCREASING) object : Comparator<PairDoubleInteger> {
                /** {@inheritDoc}  */
                override fun compare(
                    a: PairDoubleInteger,
                    b: PairDoubleInteger
                ): Int {
                    return a.key.compareTo(b.key)
                }
            } else object : Comparator<PairDoubleInteger> {
                /** {@inheritDoc}  */
                override fun compare(
                    a: PairDoubleInteger,
                    b: PairDoubleInteger
                ): Int {
                    return b.key.compareTo(a.key)
                }
            }

        // Sort.
        list.sortWith(comp)

        // Modify the original array so that its elements are in
        // the prescribed order.
        // Retrieve indices of original locations.
        val indices = IntArray(len)
        for (i in 0 until len) {
            val e = list[i]
            x[i] = e.key
            indices[i] = e.value
        }

        // In each of the associated arrays, move the
        // elements to their new location.
        for (j in 0 until yListLen) {
            // Input array will be modified in place.
            val yInPlace = yList[j]
            val yOrig: DoubleArray = yInPlace.copyOf()
            for (i in 0 until len) {
                yInPlace[i] = yOrig[indices[i]]
            }
        }
    }
    /**
     * Creates a copy of the `source` array.
     *
     * @param source Array to be copied.
     * @param len Number of entries to copy. If smaller then the source
     * length, the copy will be truncated, if larger it will padded with
     * zeroes.
     * @return the copied array.
     */
    /**
     * Creates a copy of the `source` array.
     *
     * @param source Array to be copied.
     * @return the copied array.
     */
    fun copyOf(source: IntArray, len: Int = source.size): IntArray {
        val output = IntArray(len)
        java.lang.System.arraycopy(source, 0, output, 0, FastMath.min(len, source.size))
        return output
    }
    /**
     * Creates a copy of the `source` array.
     *
     * @param source Array to be copied.
     * @param len Number of entries to copy. If smaller then the source
     * length, the copy will be truncated, if larger it will padded with
     * zeroes.
     * @return the copied array.
     */
    /**
     * Creates a copy of the `source` array.
     *
     * @param source Array to be copied.
     * @return the copied array.
     */
    fun copyOf(source: DoubleArray, len: Int = source.size): DoubleArray {
        val output = DoubleArray(len)
        java.lang.System.arraycopy(source, 0, output, 0, FastMath.min(len, source.size))
        return output
    }

    /**
     * Creates a copy of the `source` array.
     *
     * @param source Array to be copied.
     * @param from Initial index of the range to be copied, inclusive.
     * @param to Final index of the range to be copied, exclusive. (This index may lie outside the array.)
     * @return the copied array.
     */
    fun copyOfRange(source: DoubleArray, from: Int, to: Int): DoubleArray {
        val len = to - from
        val output = DoubleArray(len)
        java.lang.System.arraycopy(source, from, output, 0, FastMath.min(len, source.size - from))
        return output
    }

    /**
     * Compute a linear combination accurately.
     * This method computes the sum of the products
     * `a<sub>i</sub> b<sub>i</sub>` to high accuracy.
     * It does so by using specific multiplication and addition algorithms to
     * preserve accuracy and reduce cancellation effects.
     * <br></br>
     * It is based on the 2005 paper
     * [
 * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita, Siegfried M. Rump,
     * and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a Factors.
     * @param b Factors.
     * @return `<sub>i</sub> a<sub>i</sub> b<sub>i</sub>`.
     * @throws DimensionMismatchException if arrays dimensions don't match
     */
    fun linearCombination(a: DoubleArray, b: DoubleArray): Double {
        checkEqualLength(a, b)
        val len = a.size
        if (len == 1) {
            // Revert to scalar multiplication.
            return a[0] * b[0]
        }
        val prodHigh = DoubleArray(len)
        var prodLowSum = 0.0
        for (i in 0 until len) {
            val ai = a[i]
            val aHigh: Double =
                Double.fromBits(ai.toRawBits() and (-1L shl 27))
            val aLow = ai - aHigh
            val bi = b[i]
            val bHigh: Double =
                Double.fromBits(bi.toRawBits() and (-1L shl 27))
            val bLow = bi - bHigh
            prodHigh[i] = ai * bi
            val prodLow = aLow * bLow - (prodHigh[i] -
                    aHigh * bHigh -
                    aLow * bHigh -
                    aHigh * bLow)
            prodLowSum += prodLow
        }
        val prodHighCur = prodHigh[0]
        var prodHighNext = prodHigh[1]
        var sHighPrev = prodHighCur + prodHighNext
        var sPrime = sHighPrev - prodHighNext
        var sLowSum = prodHighNext - (sHighPrev - sPrime) + (prodHighCur - sPrime)
        val lenMinusOne = len - 1
        for (i in 1 until lenMinusOne) {
            prodHighNext = prodHigh[i + 1]
            val sHighCur = sHighPrev + prodHighNext
            sPrime = sHighCur - prodHighNext
            sLowSum += prodHighNext - (sHighCur - sPrime) + (sHighPrev - sPrime)
            sHighPrev = sHighCur
        }
        var result = sHighPrev + (prodLowSum + sLowSum)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = 0.0
            for (i in 0 until len) {
                result += a[i] * b[i]
            }
        }
        return result
    }

    /**
     * Compute a linear combination accurately.
     *
     *
     * This method computes a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> to high accuracy. It does
     * so by using specific multiplication and addition algorithms to
     * preserve accuracy and reduce cancellation effects. It is based
     * on the 2005 paper [
 * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub>
     * @see .linearCombination
     * @see .linearCombination
     */
    fun linearCombination(
        a1: Double, b1: Double,
        a2: Double, b2: Double
    ): Double {

        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they should NOT be simplified, as they
        // use IEEE754 floating point arithmetic rounding properties.
        // The variable naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits

        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High: Double =
            Double.fromBits(a1.toRawBits() and (-1L shl 27))
        val a1Low = a1 - a1High
        val b1High: Double =
            Double.fromBits(b1.toRawBits() and (-1L shl 27))
        val b1Low = b1 - b1High

        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low =
            a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low)

        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High: Double =
            Double.fromBits(a2.toRawBits() and (-1L shl 27))
        val a2Low = a2 - a2High
        val b2High: Double =
            Double.fromBits(b2.toRawBits() and (-1L shl 27))
        val b2Low = b2 - b2High

        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low =
            a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low)

        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)

        // final rounding, s12 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result = s12High + (prod1Low + prod2Low + s12Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2
        }
        return result
    }

    /**
     * Compute a linear combination accurately.
     *
     *
     * This method computes a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> + a<sub>3</sub>b<sub>3</sub>
     * to high accuracy. It does so by using specific multiplication and
     * addition algorithms to preserve accuracy and reduce cancellation effects.
     * It is based on the 2005 paper [
     * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> + a<sub>3</sub>b<sub>3</sub>
     * @see .linearCombination
     * @see .linearCombination
     */
    fun linearCombination(
        a1: Double, b1: Double,
        a2: Double, b2: Double,
        a3: Double, b3: Double
    ): Double {

        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they should NOT be simplified, as they
        // do use IEEE754 floating point arithmetic rounding properties.
        // The variables naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits

        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High: Double =
            Double.fromBits(a1.toRawBits() and (-1L shl 27))
        val a1Low = a1 - a1High
        val b1High: Double =
            Double.fromBits(b1.toRawBits() and (-1L shl 27))
        val b1Low = b1 - b1High

        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low =
            a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low)

        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High: Double =
            Double.fromBits(a2.toRawBits() and (-1L shl 27))
        val a2Low = a2 - a2High
        val b2High: Double =
            Double.fromBits(b2.toRawBits() and (-1L shl 27))
        val b2Low = b2 - b2High

        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low =
            a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low)

        // split a3 and b3 as one 26 bits number and one 27 bits number
        val a3High: Double =
            Double.fromBits(java.lang.Double.doubleToRawLongBits(a3) and (-1L shl 27))
        val a3Low = a3 - a3High
        val b3High: Double =
            Double.fromBits(java.lang.Double.doubleToRawLongBits(b3) and (-1L shl 27))
        val b3Low = b3 - b3High

        // accurate multiplication a3 * b3
        val prod3High = a3 * b3
        val prod3Low =
            a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low)

        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        val s123High = s12High + prod3High
        val s123Prime = s123High - prod3High
        val s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime)

        // final rounding, s123 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result = s123High + (prod1Low + prod2Low + prod3Low + s12Low + s123Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2 + a3 * b3
        }
        return result
    }

    /**
     * Compute a linear combination accurately.
     *
     *
     * This method computes a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> + a<sub>3</sub>b<sub>3</sub> +
     * a<sub>4</sub>b<sub>4</sub>
     * to high accuracy. It does so by using specific multiplication and
     * addition algorithms to preserve accuracy and reduce cancellation effects.
     * It is based on the 2005 paper [
     * Accurate Sum and Dot Product](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.2.1547) by Takeshi Ogita,
     * Siegfried M. Rump, and Shin'ichi Oishi published in SIAM J. Sci. Comput.
     *
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @param a3 first factor of the third term
     * @param b3 second factor of the third term
     * @param a4 first factor of the third term
     * @param b4 second factor of the third term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub> + a<sub>3</sub>b<sub>3</sub> +
     * a<sub>4</sub>b<sub>4</sub>
     * @see .linearCombination
     * @see .linearCombination
     */
    fun linearCombination(
        a1: Double, b1: Double,
        a2: Double, b2: Double,
        a3: Double, b3: Double,
        a4: Double, b4: Double
    ): Double {

        // the code below is split in many additions/subtractions that may
        // appear redundant. However, they should NOT be simplified, as they
        // do use IEEE754 floating point arithmetic rounding properties.
        // The variables naming conventions are that xyzHigh contains the most significant
        // bits of xyz and xyzLow contains its least significant bits. So theoretically
        // xyz is the sum xyzHigh + xyzLow, but in many cases below, this sum cannot
        // be represented in only one double precision number so we preserve two numbers
        // to hold it as long as we can, combining the high and low order bits together
        // only at the end, after cancellation may have occurred on high order bits

        // split a1 and b1 as one 26 bits number and one 27 bits number
        val a1High: Double =
            Double.fromBits(a1.toRawBits() and (-1L shl 27))
        val a1Low = a1 - a1High
        val b1High: Double =
            Double.fromBits(b1.toRawBits() and (-1L shl 27))
        val b1Low = b1 - b1High

        // accurate multiplication a1 * b1
        val prod1High = a1 * b1
        val prod1Low =
            a1Low * b1Low - (prod1High - a1High * b1High - a1Low * b1High - a1High * b1Low)

        // split a2 and b2 as one 26 bits number and one 27 bits number
        val a2High: Double =
            Double.fromBits(a2.toRawBits() and (-1L shl 27))
        val a2Low = a2 - a2High
        val b2High: Double =
            Double.fromBits(b2.toRawBits() and (-1L shl 27))
        val b2Low = b2 - b2High

        // accurate multiplication a2 * b2
        val prod2High = a2 * b2
        val prod2Low =
            a2Low * b2Low - (prod2High - a2High * b2High - a2Low * b2High - a2High * b2Low)

        // split a3 and b3 as one 26 bits number and one 27 bits number
        val a3High: Double =
            Double.fromBits(a3.toRawBits() and (-1L shl 27))
        val a3Low = a3 - a3High
        val b3High: Double =
            Double.fromBits(b3.toRawBits() and (-1L shl 27))
        val b3Low = b3 - b3High

        // accurate multiplication a3 * b3
        val prod3High = a3 * b3
        val prod3Low =
            a3Low * b3Low - (prod3High - a3High * b3High - a3Low * b3High - a3High * b3Low)

        // split a4 and b4 as one 26 bits number and one 27 bits number
        val a4High: Double =
            Double.fromBits(a4.toRawBits() and (-1L shl 27))
        val a4Low = a4 - a4High
        val b4High: Double =
            Double.fromBits(b4.toRawBits() and (-1L shl 27))
        val b4Low = b4 - b4High

        // accurate multiplication a4 * b4
        val prod4High = a4 * b4
        val prod4Low =
            a4Low * b4Low - (prod4High - a4High * b4High - a4Low * b4High - a4High * b4Low)

        // accurate addition a1 * b1 + a2 * b2
        val s12High = prod1High + prod2High
        val s12Prime = s12High - prod2High
        val s12Low = prod2High - (s12High - s12Prime) + (prod1High - s12Prime)

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3
        val s123High = s12High + prod3High
        val s123Prime = s123High - prod3High
        val s123Low = prod3High - (s123High - s123Prime) + (s12High - s123Prime)

        // accurate addition a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4
        val s1234High = s123High + prod4High
        val s1234Prime = s1234High - prod4High
        val s1234Low = prod4High - (s1234High - s1234Prime) + (s123High - s1234Prime)

        // final rounding, s1234 may have suffered many cancellations, we try
        // to recover some bits from the extra words we have saved up to now
        var result =
            s1234High + (prod1Low + prod2Low + prod3Low + prod4Low + s12Low + s123Low + s1234Low)
        if (result.isNaN()) {
            // either we have split infinite numbers or some coefficients were NaNs,
            // just rely on the naive implementation and let IEEE754 handle this
            result = a1 * b1 + a2 * b2 + a3 * b3 + a4 * b4
        }
        return result
    }

    /**
     * Returns true iff both arguments are null or have same dimensions and all
     * their elements are equal as defined by
     * [Precision.equals].
     *
     * @param x first array
     * @param y second array
     * @return true if the values are both null or have same dimension
     * and equal elements.
     */
    fun equals(x: FloatArray?, y: FloatArray?): Boolean {
        if (x == null || y == null) {
            return !((x == null) xor (y == null))
        }
        if (x.size != y.size) {
            return false
        }
        for (i in x.indices) {
            if (!Precision.equals(x[i], y[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Returns true iff both arguments are null or have same dimensions and all
     * their elements are equal as defined by
     * [this method][Precision.equalsIncludingNaN].
     *
     * @param x first array
     * @param y second array
     * @return true if the values are both null or have same dimension and
     * equal elements
     * @since 2.2
     */
    fun equalsIncludingNaN(x: FloatArray?, y: FloatArray?): Boolean {
        if (x == null || y == null) {
            return !((x == null) xor (y == null))
        }
        if (x.size != y.size) {
            return false
        }
        for (i in x.indices) {
            if (!Precision.equalsIncludingNaN(x[i], y[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Returns `true` iff both arguments are `null` or have same
     * dimensions and all their elements are equal as defined by
     * [Precision.equals].
     *
     * @param x First array.
     * @param y Second array.
     * @return `true` if the values are both `null` or have same
     * dimension and equal elements.
     */
    fun equals(x: DoubleArray?, y: DoubleArray?): Boolean {
        if (x == null || y == null) {
            return !((x == null) xor (y == null))
        }
        if (x.size != y.size) {
            return false
        }
        for (i in x.indices) {
            if (!Precision.equals(x[i], y[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Returns `true` iff both arguments are `null` or have same
     * dimensions and all their elements are equal as defined by
     * [this method][Precision.equalsIncludingNaN].
     *
     * @param x First array.
     * @param y Second array.
     * @return `true` if the values are both `null` or have same
     * dimension and equal elements.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: DoubleArray?, y: DoubleArray?): Boolean {
        if (x == null || y == null) {
            return !((x == null) xor (y == null))
        }
        if (x.size != y.size) {
            return false
        }
        for (i in x.indices) {
            if (!Precision.equalsIncludingNaN(x[i], y[i])) {
                return false
            }
        }
        return true
    }

    /**
     * Normalizes an array to make it sum to a specified value.
     * Returns the result of the transformation
     * <pre>
     * x |-> x * normalizedSum / sum
    </pre> *
     * applied to each non-NaN element x of the input array, where sum is the
     * sum of the non-NaN entries in the input array.
     *
     *
     * Throws IllegalArgumentException if `normalizedSum` is infinite
     * or NaN and ArithmeticException if the input array contains any infinite elements
     * or sums to 0.
     *
     *
     * Ignores (i.e., copies unchanged to the output array) NaNs in the input array.
     *
     * @param values Input array to be normalized
     * @param normalizedSum Target sum for the normalized array
     * @return the normalized array.
     * @throws MathArithmeticException if the input array contains infinite
     * elements or sums to zero.
     * @throws MathIllegalArgumentException if the target sum is infinite or `NaN`.
     * @since 2.1
     */
    fun normalizeArray(values: DoubleArray, normalizedSum: Double): DoubleArray {
        if (normalizedSum.isInfinite()) {
            throw MathIllegalArgumentException(LocalizedFormats.NORMALIZE_INFINITE)
        }
        if (normalizedSum.isNaN()) {
            throw MathIllegalArgumentException(LocalizedFormats.NORMALIZE_NAN)
        }
        var sum = 0.0
        val len = values.size
        val out = DoubleArray(len)
        for (i in 0 until len) {
            if (values[i].isInfinite()) {
                throw MathIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, values[i], i)
            }
            if (!values[i].isNaN()) {
                sum += values[i]
            }
        }
        if (sum == 0.0) {
            throw MathArithmeticException(LocalizedFormats.ARRAY_SUMS_TO_ZERO)
        }
        for (i in 0 until len) {
            if (values[i].isNaN()) {
                out[i] = Double.NaN
            } else {
                out[i] = values[i] * normalizedSum / sum
            }
        }
        return out
    }

    /** Build an array of elements.
     *
     *
     * Arrays are filled with field.getZero()
     *
     * @param <T> the type of the field elements
     * @param field field to which array elements belong
     * @param length of the array
     * @return a new array
     * @since 3.2
    </T> */
    fun <T> buildArray(field: Field<T>, length: Int): Array<T> {
        val array =
            java.lang.reflect.Array.newInstance(field.getRuntimeClass(), length) as Array<T>
        array.fill(field.getZero())
        return array
    }

    /** Build a double dimension  array of elements.
     *
     *
     * Arrays are filled with field.getZero()
     *
     * @param <T> the type of the field elements
     * @param field field to which array elements belong
     * @param rows number of rows in the array
     * @param columns number of columns (may be negative to build partial
     * arrays in the same way `new Field[rows][]` works)
     * @return a new array
     * @since 3.2
    </T> */
    fun <T> buildArray(field: Field<T>, rows: Int, columns: Int): Array<Array<T>> {
        val array: Array<Array<T>>
        if (columns < 0) {
            val dummyRow: Array<T> = buildArray(field, 0)
            array = java.lang.reflect.Array.newInstance(dummyRow.javaClass, rows)
        } else {
            array = java.lang.reflect.Array.newInstance(
                field.getRuntimeClass(), intArrayOf(
                    rows, columns
                )
            )
            for (i in 0 until rows) {
                array[i].fill(field.getZero())
            }
        }
        return array
    }

    /**
     * Calculates the [
 * convolution](http://en.wikipedia.org/wiki/Convolution) between two sequences.
     *
     *
     * The solution is obtained via straightforward computation of the
     * convolution sum (and not via FFT). Whenever the computation needs
     * an element that would be located at an index outside the input arrays,
     * the value is assumed to be zero.
     *
     * @param x First sequence.
     * Typically, this sequence will represent an input signal to a system.
     * @param h Second sequence.
     * Typically, this sequence will represent the impulse response of the system.
     * @return the convolution of `x` and `h`.
     * This array's length will be `x.length + h.length - 1`.
     * @throws NoDataException if either `x` or `h` is empty.
     *
     * @since 3.3
     */
    fun convolve(x: DoubleArray, h: DoubleArray): DoubleArray {
        val xLen = x.size
        val hLen = h.size
        if (xLen == 0 || hLen == 0) {
            throw NoDataException()
        }

        // initialize the output array
        val totalLength = xLen + hLen - 1
        val y = DoubleArray(totalLength)

        // straightforward implementation of the convolution sum
        for (n in 0 until totalLength) {
            var yn = 0.0
            var k: Int = FastMath.max(0, n + 1 - xLen)
            var j = n - k
            while (k < hLen && j >= 0) {
                yn += x[j--] * h[k++]
            }
            y[n] = yn
        }
        return y
    }
    /**
     * Shuffle the entries of the given array, using the
     * [
     * Fisher–Yates](http://en.wikipedia.org/wiki/Fisher–Yates_shuffle#The_modern_algorithm) algorithm.
     * The `start` and `pos` parameters select which portion
     * of the array is randomized and which is left untouched.
     *
     * @param list Array whose entries will be shuffled (in-place).
     * @param start Index at which shuffling begins.
     * @param pos Shuffling is performed for index positions between
     * `start` and either the end (if [Position.TAIL])
     * or the beginning (if [Position.HEAD]) of the array.
     * @param rng Random number generator.
     */
    fun shuffle(
        list: IntArray,
        start: Int,
        pos: Position?,
        rng: RandomGenerator? = Well19937c()
    ) {
        when (pos) {
            Position.TAIL -> {
                var i = list.size - 1
                while (i >= start) {
                    val target: Int
                    target = if (i == start) {
                        start
                    } else {
                        // NumberIsTooLargeException cannot occur.
                        UniformIntegerDistribution(rng, start, i).sample()
                    }
                    val temp = list[target]
                    list[target] = list[i]
                    list[i] = temp
                    i--
                }
            }
            Position.HEAD -> {
                var i = 0
                while (i <= start) {
                    val target: Int
                    target = if (i == start) {
                        start
                    } else {
                        // NumberIsTooLargeException cannot occur.
                        UniformIntegerDistribution(rng, i, start).sample()
                    }
                    val temp = list[target]
                    list[target] = list[i]
                    list[i] = temp
                    i++
                }
            }
            else -> throw MathInternalError() // Should never happen.
        }
    }
    /**
     * Shuffle the entries of the given array.
     *
     * @see .shuffle
     * @param list Array whose entries will be shuffled (in-place).
     * @param rng Random number generator.
     */
    /**
     * Shuffle the entries of the given array.
     *
     * @see .shuffle
     * @param list Array whose entries will be shuffled (in-place).
     */
    fun shuffle(
        list: IntArray,
        rng: RandomGenerator? = Well19937c()
    ) {
        shuffle(list, 0, Position.TAIL, rng)
    }

    /**
     * Returns an array representing the natural number `n`.
     *
     * @param n Natural number.
     * @return an array whose entries are the numbers 0, 1, ..., `n`-1.
     * If `n == 0`, the returned array is empty.
     */
    fun natural(n: Int): IntArray {
        return sequence(n, 0, 1)
    }

    /**
     * Returns an array of `size` integers starting at `start`,
     * skipping `stride` numbers.
     *
     * @param size Natural number.
     * @param start Natural number.
     * @param stride Natural number.
     * @return an array whose entries are the numbers
     * `start, start + stride, ..., start + (size - 1) * stride`.
     * If `size == 0`, the returned array is empty.
     *
     * @since 3.4
     */
    fun sequence(
        size: Int,
        start: Int,
        stride: Int
    ): IntArray {
        val a = IntArray(size)
        for (i in 0 until size) {
            a[i] = start + i * stride
        }
        return a
    }
    /**
     * This method is used
     * to verify that the input parameters designate a subarray of positive length.
     *
     *
     *
     *  * returns `true` iff the parameters designate a subarray of
     * non-negative length
     *  * throws `IllegalArgumentException` if the array is null or
     * or the indices are invalid
     *  * returns `false` if the array is non-null, but
     * `length` is 0 unless `allowEmpty` is `true`
     *
     *
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @param allowEmpty if `true` then zero length arrays are allowed
     * @return true if the parameters are valid
     * @throws MathIllegalArgumentException if the indices are invalid or the array is null
     * @since 3.3
     */
    fun verifyValues(
        values: DoubleArray?, begin: Int,
        length: Int, allowEmpty: Boolean = false
    ): Boolean {
        if (values == null) {
            throw NullArgumentException(LocalizedFormats.INPUT_ARRAY)
        }
        if (begin < 0) {
            throw NotPositiveException(LocalizedFormats.START_POSITION, java.lang.Integer.valueOf(begin))
        }
        if (length < 0) {
            throw NotPositiveException(LocalizedFormats.LENGTH, java.lang.Integer.valueOf(length))
        }
        if (begin + length > values.size) {
            throw NumberIsTooLargeException(
                LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END,
                (begin + length), values.size, true)
        }
        return length != 0 || allowEmpty
    }
    /**
     * This method is used
     * to verify that the begin and length parameters designate a subarray of positive length
     * and the weights are all non-negative, non-NaN, finite, and not all zero.
     *
     *
     *
     *  * returns `true` iff the parameters designate a subarray of
     * non-negative length and the weights array contains legitimate values.
     *  * throws `MathIllegalArgumentException` if any of the following are true:
     *  * the values array is null
     *  * the weights array is null
     *  * the weights array does not have the same length as the values array
     *  * the weights array contains one or more infinite values
     *  * the weights array contains one or more NaN values
     *  * the weights array contains negative values
     *  * the start and length arguments do not determine a valid array
     *
     *  * returns `false` if the array is non-null, but
     * `length` is 0 unless `allowEmpty` is `true`.
     *
     *
     * @param values the input array.
     * @param weights the weights array.
     * @param begin index of the first array element to include.
     * @param length the number of elements to include.
     * @param allowEmpty if `true` than allow zero length arrays to pass.
     * @return `true` if the parameters are valid.
     * @throws NullArgumentException if either of the arrays are null
     * @throws MathIllegalArgumentException if the array indices are not valid,
     * the weights array contains NaN, infinite or negative elements, or there
     * are no positive weights.
     * @since 3.3
     */
    fun verifyValues(
        values: DoubleArray?, weights: DoubleArray?,
        begin: Int, length: Int, allowEmpty: Boolean = false
    ): Boolean {
        if (weights == null || values == null) {
            throw NullArgumentException(LocalizedFormats.INPUT_ARRAY)
        }
        checkEqualLength(weights, values)
        var containsPositiveWeight = false
        for (i in begin until begin + length) {
            val weight = weights[i]
            if (weight.isNaN()) {
                throw MathIllegalArgumentException(LocalizedFormats.NAN_ELEMENT_AT_INDEX, java.lang.Integer.valueOf(i))
            }
            if (weight.isInfinite()) {
                throw MathIllegalArgumentException(
                    LocalizedFormats.INFINITE_ARRAY_ELEMENT,
                    weight,
                    i
                )
            }
            if (weight < 0) {
                throw MathIllegalArgumentException(
                    LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX,
                    i,
                    weight
                )
            }
            if (!containsPositiveWeight && weight > 0.0) {
                containsPositiveWeight = true
            }
        }
        if (!containsPositiveWeight) {
            throw MathIllegalArgumentException(LocalizedFormats.WEIGHT_AT_LEAST_ONE_NON_ZERO)
        }
        return verifyValues(values, begin, length, allowEmpty)
    }

    /**
     * Concatenates a sequence of arrays. The return array consists of the
     * entries of the input arrays concatenated in the order they appear in
     * the argument list.  Null arrays cause NullPointerExceptions; zero
     * length arrays are allowed (contributing nothing to the output array).
     *
     * @param x list of double[] arrays to concatenate
     * @return a new array consisting of the entries of the argument arrays
     * @throws NullPointerException if any of the arrays are null
     * @since 3.6
     */
    fun concatenate(vararg x: DoubleArray): DoubleArray {
        var combinedLength = 0
        for (a in x) {
            combinedLength += a.size
        }
        var offset = 0
        var curLength = 0
        val combined = DoubleArray(combinedLength)
        for (i in x.indices) {
            curLength = x[i].size
            java.lang.System.arraycopy(x[i], 0, combined, offset, curLength)
            offset += curLength
        }
        return combined
    }

    /**
     * Returns an array consisting of the unique values in `data`.
     * The return array is sorted in descending order.  Empty arrays
     * are allowed, but null arrays result in NullPointerException.
     * Infinities are allowed.  NaN values are allowed with maximum
     * sort order - i.e., if there are NaN values in `data`,
     * `Double.NaN` will be the first element of the output array,
     * even if the array also contains `Double.POSITIVE_INFINITY`.
     *
     * @param data array to scan
     * @return descending list of values included in the input array
     * @throws NullPointerException if data is null
     * @since 3.6
     */
    fun unique(data: DoubleArray): DoubleArray {
        val values: TreeSet<Double> = TreeSet<Double>()
        for (i in data.indices) {
            values.add(data[i])
        }
        val count: Int = values.size
        val out = DoubleArray(count)
        val iterator: Iterator<Double> = values.iterator()
        var i = 0
        while (iterator.hasNext()) {
            out[count - ++i] = iterator.next()
        }
        return out
    }

    /**
     * Real-valued function that operate on an array or a part of it.
     * @since 3.1
     */
    interface Function {
        /**
         * Operates on an entire array.
         *
         * @param array Array to operate on.
         * @return the result of the operation.
         */
        fun evaluate(array: DoubleArray?): Double

        /**
         * @param array Array to operate on.
         * @param startIndex Index of the first element to take into account.
         * @param numElements Number of elements to take into account.
         * @return the result of the operation.
         */
        fun evaluate(
            array: DoubleArray?,
            startIndex: Int,
            numElements: Int
        ): Double
    }

    /**
     * Specification of ordering direction.
     */
    enum class OrderDirection {
        /** Constant for increasing direction.  */
        INCREASING,

        /** Constant for decreasing direction.  */
        DECREASING
    }

    /**
     * A helper data structure holding a double and an integer value.
     */
    private class PairDoubleInteger
    /**
     * @param key Key.
     * @param value Value.
     */ internal constructor(
        /** Key  */
        val key: Double,
        /** Value  */
        val value: Int
    ) {
        /** @return the key.
         */

        /** @return the value.
         */

    }

    /**
     * Specification for indicating that some operation applies
     * before or after a given index.
     */
    enum class Position {
        /** Designates the beginning of the array (near index 0).  */
        HEAD,

        /** Designates the end of the array.  */
        TAIL
    }
}
