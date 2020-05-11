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

import com.github.alexandrelombard.commonskt.math3.exception.MathArithmeticException
import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalArgumentException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.ceil
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.copySign
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.floor
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.max
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.nextAfter
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.pow
import org.apache.commons.math3.exception.MathArithmeticException
import org.apache.commons.math3.exception.MathIllegalArgumentException
import org.apache.commons.math3.exception.util.LocalizedFormats
import kotlin.jvm.JvmOverloads


/**
 * Utilities for comparing numbers.
 *
 * @since 3.0
 */
object Precision {
    /**
     *
     *
     * Largest double-precision floating-point number such that
     * `1 + EPSILON` is numerically equal to 1. This value is an upper
     * bound on the relative error due to rounding real numbers to double
     * precision floating-point numbers.
     *
     *
     *
     * In IEEE 754 arithmetic, this is 2<sup>-53</sup>.
     *
     *
     * @see [Machine epsilon](http://en.wikipedia.org/wiki/Machine_epsilon)
     */
    var EPSILON = 0.0

    /**
     * Safe minimum, such that `1 / SAFE_MIN` does not overflow.
     * <br></br>
     * In IEEE 754 arithmetic, this is also the smallest normalized
     * number 2<sup>-1022</sup>.
     */
    var SAFE_MIN = 0.0

    /** Exponent offset in IEEE754 representation.  */
    private const val EXPONENT_OFFSET = 1023L

    /** Offset to order signed double numbers lexicographically.  */
    private val SGN_MASK = (-0x8000000000000000L).toLong()

    /** Offset to order signed double numbers lexicographically.  */
    private const val SGN_MASK_FLOAT = -0x80000000

    /** Positive zero.  */
    private const val POSITIVE_ZERO = 0.0

    /** Positive zero bits.  */
    private val POSITIVE_ZERO_DOUBLE_BITS: Long = java.lang.Double.doubleToRawLongBits(+0.0)

    /** Negative zero bits.  */
    private val NEGATIVE_ZERO_DOUBLE_BITS: Long = java.lang.Double.doubleToRawLongBits(-0.0)

    /** Positive zero bits.  */
    private val POSITIVE_ZERO_FLOAT_BITS: Int = java.lang.Float.floatToRawIntBits(+0.0f)

    /** Negative zero bits.  */
    private val NEGATIVE_ZERO_FLOAT_BITS: Int = java.lang.Float.floatToRawIntBits(-0.0f)

    /**
     * Compares two numbers given some amount of allowed error.
     *
     * @param x the first number
     * @param y the second number
     * @param eps the amount of error to allow when checking for equality
     * @return  * 0 if  [equals(x, y, eps)][.equals]
     *  * &lt; 0 if ![equals(x, y, eps)][.equals] &amp;&amp; x &lt; y
     *  * > 0 if ![equals(x, y, eps)][.equals] &amp;&amp; x > y or
     * either argument is NaN
     */
    fun compareTo(x: Double, y: Double, eps: Double): Int {
        if (equals(x, y, eps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Compares two numbers given some amount of allowed error.
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from [
 * Bruce Dawson](http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/). Returns `false` if either of the arguments is NaN.
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return  * 0 if  [equals(x, y, maxUlps)][.equals]
     *  * &lt; 0 if ![equals(x, y, maxUlps)][.equals] &amp;&amp; x &lt; y
     *  * &gt; 0 if ![equals(x, y, maxUlps)][.equals] &amp;&amp; x > y
     * or either argument is NaN
     */
    fun compareTo(x: Double, y: Double, maxUlps: Int): Int {
        if (equals(x, y, maxUlps)) {
            return 0
        } else if (x < y) {
            return -1
        }
        return 1
    }

    /**
     * Returns true if both arguments are NaN or they are
     * equal as defined by [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Float, y: Float): Boolean {
        return if (x != x || y != y) x != x xor y == y else equals(x, y, 1)
    }

    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive).  Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other.
     * @since 2.2
     */
    fun equals(x: Float, y: Float, eps: Float): Boolean {
        return equals(x, y, 1) || abs(y - x) <= eps
    }

    /**
     * Returns true if the arguments are both NaN, are equal, or are within the range
     * of allowed error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other,
     * or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Float, y: Float, eps: Float): Boolean {
        return equalsIncludingNaN(x, y) || abs(y - x) <= eps
    }
    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive).
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent floating
     * point numbers are considered equal.
     * Adapted from [
 * Bruce Dawson](http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/).  Returns `false` if either of the arguments is NaN.
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if there are fewer than `maxUlps` floating
     * point values between `x` and `y`.
     * @since 2.2
     */
    /**
     * Returns true iff they are equal as defined by
     * [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal.
     */
    @JvmOverloads
    fun equals(x: Float, y: Float, maxUlps: Int = 1): Boolean {
        val xInt: Int = java.lang.Float.floatToRawIntBits(x)
        val yInt: Int = java.lang.Float.floatToRawIntBits(y)
        val isEqual: Boolean
        if (xInt xor yInt and SGN_MASK_FLOAT == 0) {
            // number have same sign, there is no risk of overflow
            isEqual = abs(xInt - yInt) <= maxUlps
        } else {
            // number have opposite signs, take care of overflow
            val deltaPlus: Int
            val deltaMinus: Int
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_FLOAT_BITS
                deltaMinus = xInt - NEGATIVE_ZERO_FLOAT_BITS
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_FLOAT_BITS
                deltaMinus = yInt - NEGATIVE_ZERO_FLOAT_BITS
            }
            isEqual = if (deltaPlus > maxUlps) {
                false
            } else {
                deltaMinus <= maxUlps - deltaPlus
            }
        }
        return isEqual && !java.lang.Float.isNaN(x) && !java.lang.Float.isNaN(y)
    }

    /**
     * Returns true if the arguments are both NaN or if they are equal as defined
     * by [equals(x, y, maxUlps)][.equals].
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if both arguments are NaN or if there are less than
     * `maxUlps` floating point values between `x` and `y`.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Float, y: Float, maxUlps: Int): Boolean {
        return if (x != x || y != y) x != x xor y == y else equals(x, y, maxUlps)
    }

    /**
     * Returns true if the arguments are both NaN or they are
     * equal as defined by [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Double, y: Double): Boolean {
        return if (x != x || y != y) x != x xor y == y else equals(x, y, 1)
    }

    /**
     * Returns `true` if there is no double value strictly between the
     * arguments or the difference between them is within the range of allowed
     * error (inclusive). Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Amount of allowed absolute error.
     * @return `true` if the values are two adjacent floating point
     * numbers or they are within range of each other.
     */
    fun equals(x: Double, y: Double, eps: Double): Boolean {
        return equals(x, y, 1) || abs(y - x) <= eps
    }

    /**
     * Returns `true` if there is no double value strictly between the
     * arguments or the relative difference between them is less than or equal
     * to the given tolerance. Returns `false` if either of the arguments
     * is NaN.
     *
     * @param x First value.
     * @param y Second value.
     * @param eps Amount of allowed relative error.
     * @return `true` if the values are two adjacent floating point
     * numbers or they are within range of each other.
     * @since 3.1
     */
    fun equalsWithRelativeTolerance(x: Double, y: Double, eps: Double): Boolean {
        if (equals(x, y, 1)) {
            return true
        }
        val absoluteMax = max(abs(x), abs(y))
        val relativeDifference = abs((x - y) / absoluteMax)
        return relativeDifference <= eps
    }

    /**
     * Returns true if the arguments are both NaN, are equal or are within the range
     * of allowed error (inclusive).
     *
     * @param x first value
     * @param y second value
     * @param eps the amount of absolute error to allow.
     * @return `true` if the values are equal or within range of each other,
     * or both are NaN.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Double, y: Double, eps: Double): Boolean {
        return equalsIncludingNaN(x, y) || abs(y - x) <= eps
    }
    /**
     * Returns true if the arguments are equal or within the range of allowed
     * error (inclusive).
     *
     *
     * Two float numbers are considered equal if there are `(maxUlps - 1)`
     * (or fewer) floating point numbers between them, i.e. two adjacent
     * floating point numbers are considered equal.
     *
     *
     *
     * Adapted from [
 * Bruce Dawson](http://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/). Returns `false` if either of the arguments is NaN.
     *
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if there are fewer than `maxUlps` floating
     * point values between `x` and `y`.
     */
    /**
     * Returns true iff they are equal as defined by
     * [equals(x, y, 1)][.equals].
     *
     * @param x first value
     * @param y second value
     * @return `true` if the values are equal.
     */
    @JvmOverloads
    fun equals(x: Double, y: Double, maxUlps: Int = 1): Boolean {
        val xInt: Long = java.lang.Double.doubleToRawLongBits(x)
        val yInt: Long = java.lang.Double.doubleToRawLongBits(y)
        val isEqual: Boolean
        if (xInt xor yInt and SGN_MASK == 0L) {
            // number have same sign, there is no risk of overflow
            isEqual = abs(xInt - yInt) <= maxUlps
        } else {
            // number have opposite signs, take care of overflow
            val deltaPlus: Long
            val deltaMinus: Long
            if (xInt < yInt) {
                deltaPlus = yInt - POSITIVE_ZERO_DOUBLE_BITS
                deltaMinus = xInt - NEGATIVE_ZERO_DOUBLE_BITS
            } else {
                deltaPlus = xInt - POSITIVE_ZERO_DOUBLE_BITS
                deltaMinus = yInt - NEGATIVE_ZERO_DOUBLE_BITS
            }
            isEqual = if (deltaPlus > maxUlps) {
                false
            } else {
                deltaMinus <= maxUlps - deltaPlus
            }
        }
        return isEqual && !java.lang.Double.isNaN(x) && !java.lang.Double.isNaN(y)
    }

    /**
     * Returns true if both arguments are NaN or if they are equal as defined
     * by [equals(x, y, maxUlps)][.equals].
     *
     * @param x first value
     * @param y second value
     * @param maxUlps `(maxUlps - 1)` is the number of floating point
     * values between `x` and `y`.
     * @return `true` if both arguments are NaN or if there are less than
     * `maxUlps` floating point values between `x` and `y`.
     * @since 2.2
     */
    fun equalsIncludingNaN(x: Double, y: Double, maxUlps: Int): Boolean {
        return if (x != x || y != y) x != x xor y == y else equals(x, y, maxUlps)
    }
    /**
     * Rounds the given value to the specified number of decimal places.
     * The value is rounded using the given method which is any method defined
     * in [BigDecimal].
     * If `x` is infinite or `NaN`, then the value of `x` is
     * returned unchanged, regardless of the other parameters.
     *
     * @param x Value to round.
     * @param scale Number of digits to the right of the decimal point.
     * @param roundingMethod Rounding method as defined in [BigDecimal].
     * @return the rounded value.
     * @throws ArithmeticException if `roundingMethod == ROUND_UNNECESSARY`
     * and the specified scaling operation would require rounding.
     * @throws IllegalArgumentException if `roundingMethod` does not
     * represent a valid rounding mode.
     * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
     */
    /**
     * Rounds the given value to the specified number of decimal places.
     * The value is rounded using the [BigDecimal.ROUND_HALF_UP] method.
     *
     * @param x Value to round.
     * @param scale Number of digits to the right of the decimal point.
     * @return the rounded value.
     * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
     */
    @JvmOverloads
    fun round(x: Double, scale: Int, roundingMethod: Int = java.math.BigDecimal.ROUND_HALF_UP): Double {
        return try {
            val rounded: Double = java.math.BigDecimal(java.lang.Double.toString(x))
                .setScale(scale, roundingMethod)
                .doubleValue()
            // MATH-1089: negative values rounded to zero should result in negative zero
            if (rounded == POSITIVE_ZERO) POSITIVE_ZERO * x else rounded
        } catch (ex: NumberFormatException) {
            if (java.lang.Double.isInfinite(x)) {
                x
            } else {
                Double.NaN
            }
        }
    }
    /**
     * Rounds the given value to the specified number of decimal places.
     * The value is rounded using the given method which is any method defined
     * in [BigDecimal].
     *
     * @param x Value to round.
     * @param scale Number of digits to the right of the decimal point.
     * @param roundingMethod Rounding method as defined in [BigDecimal].
     * @return the rounded value.
     * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
     * @throws MathArithmeticException if an exact operation is required but result is not exact
     * @throws MathIllegalArgumentException if `roundingMethod` is not a valid rounding method.
     */
    /**
     * Rounds the given value to the specified number of decimal places.
     * The value is rounded using the [BigDecimal.ROUND_HALF_UP] method.
     *
     * @param x Value to round.
     * @param scale Number of digits to the right of the decimal point.
     * @return the rounded value.
     * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
     */
    @JvmOverloads
    @Throws(MathArithmeticException::class, MathIllegalArgumentException::class)
    fun round(x: Float, scale: Int, roundingMethod: Int = java.math.BigDecimal.ROUND_HALF_UP): Float {
        val sign = copySign(1f, x)
        val factor = pow(10.0, scale).toFloat() * sign
        return roundUnscaled(x * factor.toDouble(), sign.toDouble(), roundingMethod).toFloat() / factor
    }

    /**
     * Rounds the given non-negative value to the "nearest" integer. Nearest is
     * determined by the rounding method specified. Rounding methods are defined
     * in [BigDecimal].
     *
     * @param unscaled Value to round.
     * @param sign Sign of the original, scaled value.
     * @param roundingMethod Rounding method, as defined in [BigDecimal].
     * @return the rounded value.
     * @throws MathArithmeticException if an exact operation is required but result is not exact
     * @throws MathIllegalArgumentException if `roundingMethod` is not a valid rounding method.
     * @since 1.1 (previously in `MathUtils`, moved as of version 3.0)
     */
    @Throws(MathArithmeticException::class, MathIllegalArgumentException::class)
    private fun roundUnscaled(
        unscaled: Double,
        sign: Double,
        roundingMethod: Int
    ): Double {
        var unscaled = unscaled
        when (roundingMethod) {
            java.math.BigDecimal.ROUND_CEILING -> unscaled = if (sign == -1.0) {
                floor(nextAfter(unscaled, Double.NEGATIVE_INFINITY))
            } else {
                ceil(nextAfter(unscaled, Double.POSITIVE_INFINITY))
            }
            java.math.BigDecimal.ROUND_DOWN -> unscaled =
                floor(nextAfter(unscaled, Double.NEGATIVE_INFINITY))
            java.math.BigDecimal.ROUND_FLOOR -> unscaled = if (sign == -1.0) {
                ceil(nextAfter(unscaled, Double.POSITIVE_INFINITY))
            } else {
                floor(nextAfter(unscaled, Double.NEGATIVE_INFINITY))
            }
            java.math.BigDecimal.ROUND_HALF_DOWN -> {
                unscaled = nextAfter(unscaled, Double.NEGATIVE_INFINITY)
                val fraction = unscaled - floor(unscaled)
                unscaled = if (fraction > 0.5) {
                    ceil(unscaled)
                } else {
                    floor(unscaled)
                }
            }
            java.math.BigDecimal.ROUND_HALF_EVEN -> {
                val fraction = unscaled - floor(unscaled)
                unscaled = if (fraction > 0.5) {
                    ceil(unscaled)
                } else if (fraction < 0.5) {
                    floor(unscaled)
                } else {
                    // The following equality test is intentional and needed for rounding purposes
                    if (floor(unscaled) / 2.0 == floor(floor(unscaled) / 2.0)) { // even
                        floor(unscaled)
                    } else { // odd
                        ceil(unscaled)
                    }
                }
            }
            java.math.BigDecimal.ROUND_HALF_UP -> {
                unscaled = nextAfter(unscaled, Double.POSITIVE_INFINITY)
                val fraction = unscaled - floor(unscaled)
                unscaled = if (fraction >= 0.5) {
                    ceil(unscaled)
                } else {
                    floor(unscaled)
                }
            }
            java.math.BigDecimal.ROUND_UNNECESSARY -> if (unscaled != floor(unscaled)) {
                throw MathArithmeticException()
            }
            java.math.BigDecimal.ROUND_UP ->             // do not round if the discarded fraction is equal to zero
                if (unscaled != floor(unscaled)) {
                    unscaled = ceil(nextAfter(unscaled, Double.POSITIVE_INFINITY))
                }
            else -> throw MathIllegalArgumentException(
                LocalizedFormats.INVALID_ROUNDING_METHOD,
                roundingMethod,
                "ROUND_CEILING", java.math.BigDecimal.ROUND_CEILING,
                "ROUND_DOWN", java.math.BigDecimal.ROUND_DOWN,
                "ROUND_FLOOR", java.math.BigDecimal.ROUND_FLOOR,
                "ROUND_HALF_DOWN", java.math.BigDecimal.ROUND_HALF_DOWN,
                "ROUND_HALF_EVEN", java.math.BigDecimal.ROUND_HALF_EVEN,
                "ROUND_HALF_UP", java.math.BigDecimal.ROUND_HALF_UP,
                "ROUND_UNNECESSARY", java.math.BigDecimal.ROUND_UNNECESSARY,
                "ROUND_UP", java.math.BigDecimal.ROUND_UP
            )
        }
        return unscaled
    }

    /**
     * Computes a number `delta` close to `originalDelta` with
     * the property that <pre>`
     * x + delta - x
    `</pre> *
     * is exactly machine-representable.
     * This is useful when computing numerical derivatives, in order to reduce
     * roundoff errors.
     *
     * @param x Value.
     * @param originalDelta Offset value.
     * @return a number `delta` so that `x + delta` and `x`
     * differ by a representable floating number.
     */
    fun representableDelta(
        x: Double,
        originalDelta: Double
    ): Double {
        return x + originalDelta - x
    }

    init {
        /*
         *  This was previously expressed as = 0x1.0p-53;
         *  However, OpenJDK (Sparc Solaris) cannot handle such small
         *  constants: MATH-721
         */
        EPSILON = java.lang.Double.longBitsToDouble(EXPONENT_OFFSET - 53L shl 52)

        /*
         * This was previously expressed as = 0x1.0p-1022;
         * However, OpenJDK (Sparc Solaris) cannot handle such small
         * constants: MATH-721
         */SAFE_MIN = java.lang.Double.longBitsToDouble(EXPONENT_OFFSET - 1022L shl 52)
    }
}
