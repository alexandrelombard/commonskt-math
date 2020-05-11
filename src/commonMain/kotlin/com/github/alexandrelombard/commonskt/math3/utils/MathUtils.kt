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

import com.github.alexandrelombard.commonskt.math3.RealFieldElement
import com.github.alexandrelombard.commonskt.math3.exception.MathArithmeticException
import com.github.alexandrelombard.commonskt.math3.exception.NotFiniteNumberException
import com.github.alexandrelombard.commonskt.math3.exception.NullArgumentException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.floor
import org.apache.commons.math3.RealFieldElement
import org.apache.commons.math3.exception.MathArithmeticException
import org.apache.commons.math3.exception.NotFiniteNumberException
import org.apache.commons.math3.exception.NullArgumentException
import org.apache.commons.math3.exception.util.Localizable
import org.apache.commons.math3.exception.util.LocalizedFormats


/**
 * Miscellaneous utility functions.
 *
 * @see ArithmeticUtils
 *
 * @see Precision
 *
 * @see MathArrays
 */
object MathUtils {
    /**
     * \(2\pi\)
     * @since 2.1
     */
    const val TWO_PI = 2 * FastMath.PI

    /**
     * \(\pi^2\)
     * @since 3.4
     */
    const val PI_SQUARED = FastMath.PI * FastMath.PI

    /**
     * Returns an integer hash code representing the given double value.
     *
     * @param value the value to be hashed
     * @return the hash code
     */
    fun hash(value: Double): Int {
        return value.hashCode()
    }

    /**
     * Returns `true` if the values are equal according to semantics of
     * [Double.equals].
     *
     * @param x Value
     * @param y Value
     * @return `new Double(x).equals(new Double(y))`
     */
    fun equals(x: Double, y: Double): Boolean {
        return x == y
    }

    /**
     * Returns an integer hash code representing the given double array.
     *
     * @param value the value to be hashed (may be null)
     * @return the hash code
     * @since 1.2
     */
    fun hash(value: DoubleArray?): Int {
        return Arrays.hashCode(value)
    }

    /**
     * Normalize an angle in a 2 wide interval around a center value.
     *
     * This method has three main uses:
     *
     *  * normalize an angle between 0 and 2:<br></br>
     * `a = MathUtils.normalizeAngle(a, FastMath.PI);`
     *  * normalize an angle between - and +<br></br>
     * `a = MathUtils.normalizeAngle(a, 0.0);`
     *  * compute the angle between two defining angular positions:<br></br>
     * `angle = MathUtils.normalizeAngle(end, start) - start;`
     *
     *
     * Note that due to numerical accuracy and since  cannot be represented
     * exactly, the result interval is *closed*, it cannot be half-closed
     * as would be more satisfactory in a purely mathematical view.
     * @param a angle to normalize
     * @param center center of the desired 2 interval for the result
     * @return a-2k with integer k and center- &lt;= a-2k &lt;= center+
     * @since 1.2
     */
    fun normalizeAngle(a: Double, center: Double): Double {
        return a - TWO_PI * floor((a + FastMath.PI - center) / TWO_PI)
    }

    /** Find the maximum of two field elements.
     * @param <T> the type of the field elements
     * @param e1 first element
     * @param e2 second element
     * @return max(a1, e2)
     * @since 3.6
    </T> */
    fun <T : RealFieldElement<T>?> max(e1: T, e2: T): T {
        return if (e1!!.subtract(e2)!!.real >= 0) e1 else e2
    }

    /** Find the minimum of two field elements.
     * @param <T> the type of the field elements
     * @param e1 first element
     * @param e2 second element
     * @return min(a1, e2)
     * @since 3.6
    </T> */
    fun <T : RealFieldElement<T>?> min(e1: T, e2: T): T {
        return if (e1!!.subtract(e2)!!.real >= 0) e2 else e1
    }

    /**
     *
     * Reduce `|a - offset|` to the primary interval
     * `[0, |period|)`.
     *
     *
     * Specifically, the value returned is <br></br>
     * `a - |period| * floor((a - offset) / |period|) - offset`.
     *
     *
     * If any of the parameters are `NaN` or infinite, the result is
     * `NaN`.
     *
     * @param a Value to reduce.
     * @param period Period.
     * @param offset Value that will be mapped to `0`.
     * @return the value, within the interval `[0 |period|)`,
     * that corresponds to `a`.
     */
    fun reduce(
        a: Double,
        period: Double,
        offset: Double
    ): Double {
        val p = abs(period)
        return a - p * floor((a - offset) / p) - offset
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to `magnitude` and with the
     * same sign as the `sign` argument.
     * @throws MathArithmeticException if `magnitude == Byte.MIN_VALUE`
     * and `sign >= 0`.
     */
    @Throws(MathArithmeticException::class)
    fun copySign(magnitude: Byte, sign: Byte): Byte {
        return if (magnitude >= 0 && sign >= 0 ||
            magnitude < 0 && sign < 0
        ) { // Sign is OK.
            magnitude
        } else if (sign >= 0 &&
            magnitude == Byte.MIN_VALUE
        ) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW)
        } else {
            (-magnitude).toByte() // Flip sign.
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to `magnitude` and with the
     * same sign as the `sign` argument.
     * @throws MathArithmeticException if `magnitude == Short.MIN_VALUE`
     * and `sign >= 0`.
     */
    @Throws(MathArithmeticException::class)
    fun copySign(magnitude: Short, sign: Short): Short {
        return if (magnitude >= 0 && sign >= 0 ||
            magnitude < 0 && sign < 0
        ) { // Sign is OK.
            magnitude
        } else if (sign >= 0 &&
            magnitude == Short.MIN_VALUE
        ) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW)
        } else {
            (-magnitude).toShort() // Flip sign.
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to `magnitude` and with the
     * same sign as the `sign` argument.
     * @throws MathArithmeticException if `magnitude == Integer.MIN_VALUE`
     * and `sign >= 0`.
     */
    @Throws(MathArithmeticException::class)
    fun copySign(magnitude: Int, sign: Int): Int {
        return if (magnitude >= 0 && sign >= 0 ||
            magnitude < 0 && sign < 0
        ) { // Sign is OK.
            magnitude
        } else if (sign >= 0 &&
            magnitude == Int.MIN_VALUE
        ) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW)
        } else {
            -magnitude // Flip sign.
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     *
     * @param magnitude Magnitude of the returned value.
     * @param sign Sign of the returned value.
     * @return a value with magnitude equal to `magnitude` and with the
     * same sign as the `sign` argument.
     * @throws MathArithmeticException if `magnitude == Long.MIN_VALUE`
     * and `sign >= 0`.
     */
    @Throws(MathArithmeticException::class)
    fun copySign(magnitude: Long, sign: Long): Long {
        return if (magnitude >= 0 && sign >= 0 ||
            magnitude < 0 && sign < 0
        ) { // Sign is OK.
            magnitude
        } else if (sign >= 0 &&
            magnitude == Long.MIN_VALUE
        ) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW)
        } else {
            -magnitude // Flip sign.
        }
    }

    /**
     * Check that the argument is a real number.
     *
     * @param x Argument.
     * @throws NotFiniteNumberException if `x` is not a
     * finite real number.
     */
    @Throws(NotFiniteNumberException::class)
    fun checkFinite(x: Double) {
        if (java.lang.Double.isInfinite(x) || java.lang.Double.isNaN(x)) {
            throw NotFiniteNumberException(x)
        }
    }

    /**
     * Check that all the elements are real numbers.
     *
     * @param val Arguments.
     * @throws NotFiniteNumberException if any values of the array is not a
     * finite real number.
     */
    @Throws(NotFiniteNumberException::class)
    fun checkFinite(`val`: DoubleArray) {
        for (i in `val`.indices) {
            val x = `val`[i]
            if (java.lang.Double.isInfinite(x) || java.lang.Double.isNaN(x)) {
                throw NotFiniteNumberException(LocalizedFormats.ARRAY_ELEMENT, x, i)
            }
        }
    }

    /**
     * Checks that an object is not null.
     *
     * @param o Object to be checked.
     * @param pattern Message pattern.
     * @param args Arguments to replace the placeholders in `pattern`.
     * @throws NullArgumentException if `o` is `null`.
     */
    @Throws(NullArgumentException::class)
    fun checkNotNull(
        o: Any?,
        pattern: Localizable?,
        vararg args: Any?
    ) {
        if (o == null) {
            throw NullArgumentException(pattern, args)
        }
    }

    /**
     * Checks that an object is not null.
     *
     * @param o Object to be checked.
     * @throws NullArgumentException if `o` is `null`.
     */
    @Throws(NullArgumentException::class)
    fun checkNotNull(o: Any?) {
        if (o == null) {
            throw NullArgumentException()
        }
    }
}
