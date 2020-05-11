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

import com.github.alexandrelombard.commonskt.math3.exception.ConvergenceException
import com.github.alexandrelombard.commonskt.math3.exception.MaxCountExceededException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs

/**
 * Provides a generic means to evaluate continued fractions.  Subclasses simply
 * provided the a and b coefficients to evaluate the continued fraction.
 *
 *
 *
 * References:
 *
 *  * [
 * Continued Fraction](http://mathworld.wolfram.com/ContinuedFraction.html)
 *
 *
 *
 */
abstract class ContinuedFraction
/**
 * Default constructor.
 */
protected constructor() {
    /**
     * Access the n-th a coefficient of the continued fraction.  Since a can be
     * a function of the evaluation point, x, that is passed in as well.
     * @param n the coefficient index to retrieve.
     * @param x the evaluation point.
     * @return the n-th a coefficient.
     */
    protected abstract fun getA(n: Int, x: Double): Double

    /**
     * Access the n-th b coefficient of the continued fraction.  Since b can be
     * a function of the evaluation point, x, that is passed in as well.
     * @param n the coefficient index to retrieve.
     * @param x the evaluation point.
     * @return the n-th b coefficient.
     */
    protected abstract fun getB(n: Int, x: Double): Double

    /**
     * Evaluates the continued fraction at the value x.
     * @param x the evaluation point.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x.
     * @throws ConvergenceException if the algorithm fails to converge.
     * @throws MaxCountExceededException if maximal number of iterations is reached
     */
    fun evaluate(x: Double, maxIterations: Int): Double {
        return evaluate(x, DEFAULT_EPSILON, maxIterations)
    }
    /**
     * Evaluates the continued fraction at the value x.
     *
     *
     * The implementation of this method is based on the modified Lentz algorithm as described
     * on page 18 ff. in:
     *
     *  *
     * I. J. Thompson,  A. R. Barnett. "Coulomb and Bessel Functions of Complex Arguments and Order."
     * [http://www.fresco.org.uk/papers/Thompson-JCP64p490.pdf](http://www.fresco.org.uk/papers/Thompson-JCP64p490.pdf)
     *
     *
     * **Note:** the implementation uses the terms a<sub>i</sub> and b<sub>i</sub> as defined in
     * [Continued Fraction @ MathWorld](http://mathworld.wolfram.com/ContinuedFraction.html).
     *
     *
     * @param x the evaluation point.
     * @param epsilon maximum error allowed.
     * @param maxIterations maximum number of convergents
     * @return the value of the continued fraction evaluated at x.
     * @throws ConvergenceException if the algorithm fails to converge.
     * @throws MaxCountExceededException if maximal number of iterations is reached
     */
    fun evaluate(
        x: Double,
        epsilon: Double = DEFAULT_EPSILON,
        maxIterations: Int = Int.MAX_VALUE
    ): Double {
        val small = 1e-50
        var hPrev = getA(0, x)

        // use the value of small as epsilon criteria for zero checks
        if (Precision.equals(hPrev, 0.0, small)) {
            hPrev = small
        }
        var n = 1
        var dPrev = 0.0
        var cPrev = hPrev
        var hN = hPrev
        while (n < maxIterations) {
            val a = getA(n, x)
            val b = getB(n, x)
            var dN = a + b * dPrev
            if (Precision.equals(dN, 0.0, small)) {
                dN = small
            }
            var cN = a + b / cPrev
            if (Precision.equals(cN, 0.0, small)) {
                cN = small
            }
            dN = 1 / dN
            val deltaN = cN * dN
            hN = hPrev * deltaN
            if (hN.isInfinite()) {
                throw ConvergenceException(
                    LocalizedFormats.CONTINUED_FRACTION_INFINITY_DIVERGENCE,
                    x)
            }
            if (hN.isNaN()) {
                throw ConvergenceException(
                    LocalizedFormats.CONTINUED_FRACTION_NAN_DIVERGENCE,
                    x)
            }
            if (abs(deltaN - 1.0) < epsilon) {
                break
            }
            dPrev = dN
            cPrev = cN
            hPrev = hN
            n++
        }
        if (n >= maxIterations) {
            throw MaxCountExceededException(
                LocalizedFormats.NON_CONVERGENT_CONTINUED_FRACTION,
                maxIterations, x
            )
        }
        return hN
    }

    companion object {
        /** Maximum allowed numerical error.  */
        private const val DEFAULT_EPSILON = 10e-9
    }
}
