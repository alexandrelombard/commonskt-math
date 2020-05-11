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
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.cos
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.sin

/**
 * Static implementations of common
 * [org.apache.commons.math3.complex.Complex] utilities functions.
 *
 */
object ComplexUtils {
    /**
     * Creates a complex number from the given polar representation.
     *
     *
     * The value returned is `re<sup>itheta</sup>`,
     * computed as `rcos(theta) + rsin(theta)i`
     *
     *
     * If either `r` or `theta` is NaN, or
     * `theta` is infinite, [Complex.NaN] is returned.
     *
     *
     * If `r` is infinite and `theta` is finite,
     * infinite or NaN values may be returned in parts of the result, following
     * the rules for double arithmetic.<pre>
     * Examples:
     * `
     * polar2Complex(INFINITY, /4) = INFINITY + INFINITY i
     * polar2Complex(INFINITY, 0) = INFINITY + NaN i
     * polar2Complex(INFINITY, -/4) = INFINITY - INFINITY i
     * polar2Complex(INFINITY, 5/4) = -INFINITY - INFINITY i `</pre>
     *
     * @param r the modulus of the complex number to create
     * @param theta  the argument of the complex number to create
     * @return `re<sup>itheta</sup>`
     * @throws MathIllegalArgumentException if `r` is negative.
     * @since 1.1
     */
    fun polar2Complex(r: Double, theta: Double): Complex {
        if (r < 0) {
            throw MathIllegalArgumentException(
                LocalizedFormats.NEGATIVE_COMPLEX_MODULE, r
            )
        }
        return Complex(r * cos(theta), r * sin(theta))
    }

    /**
     * Convert an array of primitive doubles to an array of `Complex` objects.
     *
     * @param real Array of numbers to be converted to their `Complex`
     * equivalent.
     * @return an array of `Complex` objects.
     *
     * @since 3.1
     */
    fun convertToComplex(real: DoubleArray): Array<Complex> {
        val c: Array<Complex> = Array(real.size) { i ->
            Complex(real[i], 0.0)
        }
        return c
    }
}
