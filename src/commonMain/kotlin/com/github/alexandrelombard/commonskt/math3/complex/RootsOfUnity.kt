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
package com.github.alexandrelombard.commonskt.math3.complex

import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalArgumentException
import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalStateException
import com.github.alexandrelombard.commonskt.math3.exception.OutOfRangeException
import com.github.alexandrelombard.commonskt.math3.exception.ZeroException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.cos
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.sin
import kotlin.jvm.Synchronized

/**
 * A helper class for the computation and caching of the `n`-th roots of
 * unity.
 *
 * @since 3.0
 */
class RootsOfUnity
/**
 * Build an engine for computing the `n`-th roots of unity.
 */ {
    /**
     * Returns the number of roots of unity currently stored. If
     * [.computeRoots] was called with `n`, then this method
     * returns `abs(n)`. If no roots of unity have been computed yet, this
     * method returns 0.
     *
     * @return the number of roots of unity currently stored
     */
    /** Number of roots of unity.  */
    @get:Synchronized
    var numberOfRoots = 0
        private set

    /** Real part of the roots.  */
    private var omegaReal: DoubleArray? = null

    /**
     * Imaginary part of the `n`-th roots of unity, for positive values
     * of `n`. In this array, the roots are stored in counter-clockwise
     * order.
     */
    private var omegaImaginaryCounterClockwise: DoubleArray? = null

    /**
     * Imaginary part of the `n`-th roots of unity, for negative values
     * of `n`. In this array, the roots are stored in clockwise order.
     */
    private var omegaImaginaryClockwise: DoubleArray? = null

    /**
     * `true` if [.computeRoots] was called with a positive
     * value of its argument `n`. In this case, counter-clockwise ordering
     * of the roots of unity should be used.
     */
    private var isCounterClockWise = true

    /**
     * Returns `true` if [.computeRoots] was called with a
     * positive value of its argument `n`. If `true`, then
     * counter-clockwise ordering of the roots of unity should be used.
     *
     * @return `true` if the roots of unity are stored in
     * counter-clockwise order
     * @throws MathIllegalStateException if no roots of unity have been computed
     * yet
     */
    @Synchronized
    fun isCounterClockWise(): Boolean {
        if (numberOfRoots == 0) {
            throw MathIllegalStateException(
                LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET
            )
        }
        return isCounterClockWise
    }

    /**
     *
     *
     * Computes the `n`-th roots of unity. The roots are stored in
     * `omega[]`, such that `omega[k] = w ^ k`, where
     * `k = 0, ..., n - 1`, `w = exp(2 * pi * i / n)` and
     * `i = sqrt(-1)`.
     *
     *
     *
     * Note that `n` can be positive of negative
     *
     *
     *  * `abs(n)` is always the number of roots of unity.
     *  * If `n > 0`, then the roots are stored in counter-clockwise order.
     *  * If `n < 0`, then the roots are stored in clockwise order.
     *
     *
     * @param n the (signed) number of roots of unity to be computed
     * @throws ZeroException if `n = 0`
     */
    @Synchronized
    fun computeRoots(n: Int) {
        if (n == 0) {
            throw ZeroException(
                LocalizedFormats.CANNOT_COMPUTE_0TH_ROOT_OF_UNITY
            )
        }
        isCounterClockWise = n > 0

        // avoid repetitive calculations
        val absN = abs(n)
        if (absN == numberOfRoots) {
            return
        }

        // calculate everything from scratch
        val t = 2.0 * FastMath.PI / absN
        val cosT = cos(t)
        val sinT = sin(t)
        omegaReal = DoubleArray(absN)
        omegaImaginaryCounterClockwise = DoubleArray(absN)
        omegaImaginaryClockwise = DoubleArray(absN)
        omegaReal!![0] = 1.0
        omegaImaginaryCounterClockwise!![0] = 0.0
        omegaImaginaryClockwise!![0] = 0.0
        for (i in 1 until absN) {
            omegaReal!![i] = omegaReal!![i - 1] * cosT -
                    omegaImaginaryCounterClockwise!![i - 1] * sinT
            omegaImaginaryCounterClockwise!![i] = omegaReal!![i - 1] * sinT +
                    omegaImaginaryCounterClockwise!![i - 1] * cosT
            omegaImaginaryClockwise!![i] = -omegaImaginaryCounterClockwise!![i]
        }
        numberOfRoots = absN
    }

    /**
     * Get the real part of the `k`-th `n`-th root of unity.
     *
     * @param k index of the `n`-th root of unity
     * @return real part of the `k`-th `n`-th root of unity
     * @throws MathIllegalStateException if no roots of unity have been
     * computed yet
     * @throws MathIllegalArgumentException if `k` is out of range
     */
    @Synchronized
    fun getReal(k: Int): Double {
        if (numberOfRoots == 0) {
            throw MathIllegalStateException(
                LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET
            )
        }
        if (k < 0 || k >= numberOfRoots) {
            throw OutOfRangeException(
                LocalizedFormats.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX,
                k,
                0,
                numberOfRoots - 1
            )
        }
        return omegaReal!![k]
    }

    /**
     * Get the imaginary part of the `k`-th `n`-th root of unity.
     *
     * @param k index of the `n`-th root of unity
     * @return imaginary part of the `k`-th `n`-th root of unity
     * @throws MathIllegalStateException if no roots of unity have been
     * computed yet
     * @throws OutOfRangeException if `k` is out of range
     */
    @Synchronized
    fun getImaginary(k: Int): Double {
        if (numberOfRoots == 0) {
            throw MathIllegalStateException(
                LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET
            )
        }
        if (k < 0 || k >= numberOfRoots) {
            throw OutOfRangeException(
                LocalizedFormats.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX,
                k,
                0,
                numberOfRoots - 1
            )
        }
        return if (isCounterClockWise) omegaImaginaryCounterClockwise!![k] else omegaImaginaryClockwise!![k]
    }

    companion object {
        /** Serializable version id.  */
        private const val serialVersionUID = 20120201L
    }

}
