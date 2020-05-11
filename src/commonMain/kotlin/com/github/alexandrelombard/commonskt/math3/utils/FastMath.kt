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
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import org.apache.commons.math3.exception.MathArithmeticException
import org.apache.commons.math3.exception.util.LocalizedFormats
import kotlin.jvm.JvmStatic


/**
 * Faster, more accurate, portable alternative to [Math] and
 * [StrictMath] for large scale computation.
 *
 *
 * FastMath is a drop-in replacement for both Math and StrictMath. This
 * means that for any method in Math (say `Math.sin(x)` or
 * `Math.cbrt(y)`), user can directly change the class and use the
 * methods as is (using `FastMath.sin(x)` or `FastMath.cbrt(y)`
 * in the previous example).
 *
 *
 *
 * FastMath speed is achieved by relying heavily on optimizing compilers
 * to native code present in many JVMs today and use of large tables.
 * The larger tables are lazily initialised on first use, so that the setup
 * time does not penalise methods that don't need them.
 *
 *
 *
 * Note that FastMath is
 * extensively used inside Apache Commons Math, so by calling some algorithms,
 * the overhead when the the tables need to be intialised will occur
 * regardless of the end-user calling FastMath methods directly or not.
 * Performance figures for a specific JVM and hardware can be evaluated by
 * running the FastMathTestPerformance tests in the test directory of the source
 * distribution.
 *
 *
 *
 * FastMath accuracy should be mostly independent of the JVM as it relies only
 * on IEEE-754 basic operations and on embedded tables. Almost all operations
 * are accurate to about 0.5 ulp throughout the domain range. This statement,
 * of course is only a rough global observed behavior, it is *not* a
 * guarantee for *every* double numbers input (see William Kahan's [Table
 * Maker's Dilemma](http://en.wikipedia.org/wiki/Rounding#The_table-maker.27s_dilemma)).
 *
 *
 *
 * FastMath additionally implements the following methods not found in Math/StrictMath:
 *
 *  * [.asinh]
 *  * [.acosh]
 *  * [.atanh]
 *
 * The following methods are found in Math/StrictMath since 1.6 only, they are provided
 * by FastMath even in 1.5 Java virtual machines
 *
 *  * [.copySign]
 *  * [.getExponent]
 *  * [.nextAfter]
 *  * [.nextUp]
 *  * [.scalb]
 *  * [.copySign]
 *  * [.getExponent]
 *  * [.nextAfter]
 *  * [.nextUp]
 *  * [.scalb]
 *
 *
 * @since 2.2
 */
object FastMath {
    /** Archimede's constant PI, ratio of circle circumference to diameter.  */
    const val PI = 105414357.0 / 33554432.0 + 1.984187159361080883e-9

    /** Napier's constant e, base of the natural logarithm.  */
    const val E = 2850325.0 / 1048576.0 + 8.254840070411028747e-8

    /** Index of exp(0) in the array of integer exponentials.  */
    const val EXP_INT_TABLE_MAX_INDEX = 750

    /** Length of the array of integer exponentials.  */
    const val EXP_INT_TABLE_LEN = EXP_INT_TABLE_MAX_INDEX * 2

    /** Logarithm table length.  */
    const val LN_MANT_LEN = 1024

    /** Exponential fractions table length.  */
    const val EXP_FRAC_TABLE_LEN = 1025 // 0, 1/1024, ... 1024/1024

    /** StrictMath.log(Double.MAX_VALUE): {@value}  */
    private val LOG_MAX_VALUE: Double = java.lang.StrictMath.log(Double.MAX_VALUE)

    /** Indicator for tables initialization.
     *
     *
     * This compile-time constant should be set to true only if one explicitly
     * wants to compute the tables at class loading time instead of using the
     * already computed ones provided as literal arrays below.
     *
     */
    private const val RECOMPUTE_TABLES_AT_RUNTIME = false

    /** log(2) (high bits).  */
    private const val LN_2_A = 0.693147063255310059

    /** log(2) (low bits).  */
    private const val LN_2_B = 1.17304635250823482e-7

    /** Coefficients for log, when input 0.99 < x < 1.01.  */
    private val LN_QUICK_COEF = arrayOf(
        doubleArrayOf(1.0, 5.669184079525E-24),
        doubleArrayOf(-0.25, -0.25),
        doubleArrayOf(0.3333333134651184, 1.986821492305628E-8),
        doubleArrayOf(-0.25, -6.663542893624021E-14),
        doubleArrayOf(0.19999998807907104, 1.1921056801463227E-8),
        doubleArrayOf(-0.1666666567325592, -7.800414592973399E-9),
        doubleArrayOf(0.1428571343421936, 5.650007086920087E-9),
        doubleArrayOf(-0.12502530217170715, -7.44321345601866E-11),
        doubleArrayOf(0.11113807559013367, 9.219544613762692E-9)
    )

    /** Coefficients for log in the range of 1.0 < x < 1.0 + 2^-10.  */
    private val LN_HI_PREC_COEF = arrayOf(
        doubleArrayOf(1.0, -6.032174644509064E-23),
        doubleArrayOf(-0.25, -0.25),
        doubleArrayOf(0.3333333134651184, 1.9868161777724352E-8),
        doubleArrayOf(-0.2499999701976776, -2.957007209750105E-8),
        doubleArrayOf(0.19999954104423523, 1.5830993332061267E-10),
        doubleArrayOf(-0.16624879837036133, -2.6033824355191673E-8)
    )

    /** Sine, Cosine, Tangent tables are for 0, 1/8, 2/8, ... 13/8 = PI/2 approx.  */
    private const val SINE_TABLE_LEN = 14

    /** Sine table (high bits).  */
    private val SINE_TABLE_A = doubleArrayOf(
        +0.0,
        +0.1246747374534607,
        +0.24740394949913025,
        +0.366272509098053,
        +0.4794255495071411,
        +0.5850973129272461,
        +0.6816387176513672,
        +0.7675435543060303,
        +0.8414709568023682,
        +0.902267575263977,
        +0.9489846229553223,
        +0.9808930158615112,
        +0.9974949359893799,
        +0.9985313415527344
    )

    /** Sine table (low bits).  */
    private val SINE_TABLE_B = doubleArrayOf(
        +0.0,
        -4.068233003401932E-9,
        +9.755392680573412E-9,
        +1.9987994582857286E-8,
        -1.0902938113007961E-8,
        -3.9986783938944604E-8,
        +4.23719669792332E-8,
        -5.207000323380292E-8,
        +2.800552834259E-8,
        +1.883511811213715E-8,
        -3.5997360512765566E-9,
        +4.116164446561962E-8,
        +5.0614674548127384E-8,
        -1.0129027912496858E-9
    )

    /** Cosine table (high bits).  */
    private val COSINE_TABLE_A = doubleArrayOf(
        +1.0,
        +0.9921976327896118,
        +0.9689123630523682,
        +0.9305076599121094,
        +0.8775825500488281,
        +0.8109631538391113,
        +0.7316888570785522,
        +0.6409968137741089,
        +0.5403022766113281,
        +0.4311765432357788,
        +0.3153223395347595,
        +0.19454771280288696,
        +0.07073719799518585,
        -0.05417713522911072
    )

    /** Cosine table (low bits).  */
    private val COSINE_TABLE_B = doubleArrayOf(
        +0.0,
        +3.4439717236742845E-8,
        +5.865827662008209E-8,
        -3.7999795083850525E-8,
        +1.184154459111628E-8,
        -3.43338934259355E-8,
        +1.1795268640216787E-8,
        +4.438921624363781E-8,
        +2.925681159240093E-8,
        -2.6437112632041807E-8,
        +2.2860509143963117E-8,
        -4.813899778443457E-9,
        +3.6725170580355583E-9,
        +2.0217439756338078E-10
    )

    /** Tangent table, used by atan() (high bits).  */
    private val TANGENT_TABLE_A = doubleArrayOf(
        +0.0,
        +0.1256551444530487,
        +0.25534194707870483,
        +0.3936265707015991,
        +0.5463024377822876,
        +0.7214844226837158,
        +0.9315965175628662,
        +1.1974215507507324,
        +1.5574076175689697,
        +2.092571258544922,
        +3.0095696449279785,
        +5.041914939880371,
        +14.101419448852539,
        -18.430862426757812
    )

    /** Tangent table, used by atan() (low bits).  */
    private val TANGENT_TABLE_B = doubleArrayOf(
        +0.0,
        -7.877917738262007E-9,
        -2.5857668567479893E-8,
        +5.2240336371356666E-9,
        +5.206150291559893E-8,
        +1.8307188599677033E-8,
        -5.7618793749770706E-8,
        +7.848361555046424E-8,
        +1.0708593250394448E-7,
        +1.7827257129423813E-8,
        +2.893485277253286E-8,
        +3.1660099222737955E-7,
        +4.983191803254889E-7,
        -3.356118100840571E-7
    )

    /** Bits of 1/(2*pi), need for reducePayneHanek().  */
    private val RECIP_2PI = longArrayOf(
        0x28be60dbL shl 32 or 0x9391054aL,
        0x7f09d5f4L shl 32 or 0x7d4d3770L,
        0x36d8a566L shl 32 or 0x4f10e410L,
        0x7f9458eaL shl 32 or 0xf7aef158L,
        0x6dc91b8eL shl 32 or 0x909374b8L,
        0x01924bbaL shl 32 or 0x82746487L,
        0x3f877ac7L shl 32 or 0x2c4a69cfL,
        0xba208d7dL shl 32 or 0x4baed121L,
        0x3a671c09L shl 32 or 0xad17df90L,
        0x4e64758eL shl 32 or 0x60d4ce7dL,
        0x272117e2L shl 32 or 0xef7e4a0eL,
        0xc7fe25ffL shl 32 or 0xf7816603L,
        0xfbcbc462L shl 32 or 0xd6829b47L,
        0xdb4d9fb3L shl 32 or 0xc9f2c26dL,
        0xd3d18fd9L shl 32 or 0xa797fa8bL,
        0x5d49eeb1L shl 32 or 0xfaf97c5eL,
        0xcf41ce7dL shl 32 or 0xe294a4baL,
        0x9afed7ecL shl 32
    )

    /** Bits of pi/4, need for reducePayneHanek().  */
    private val PI_O_4_BITS = longArrayOf(
        0xc90fdaa2L shl 32 or 0x2168c234L,
        0xc4c6628bL shl 32 or 0x80dc1cd1L
    )

    /** Eighths.
     * This is used by sinQ, because its faster to do a table lookup than
     * a multiply in this time-critical routine
     */
    private val EIGHTHS =
        doubleArrayOf(0.0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875, 1.0, 1.125, 1.25, 1.375, 1.5, 1.625)

    /** Table of 2^((n+2)/3)  */
    private val CBRTTWO = doubleArrayOf(
        0.6299605249474366,
        0.7937005259840998,
        1.0,
        1.2599210498948732,
        1.5874010519681994
    )
    /*
     *  There are 52 bits in the mantissa of a double.
     *  For additional precision, the code splits double numbers into two parts,
     *  by clearing the low order 30 bits if possible, and then performs the arithmetic
     *  on each half separately.
     */
    /**
     * 0x40000000 - used to split a double into two parts, both with the low order bits cleared.
     * Equivalent to 2^30.
     */
    private const val HEX_40000000 = 0x40000000L // 1073741824L

    /** Mask used to clear low order 30 bits  */
    private const val MASK_30BITS = -1L - (HEX_40000000 - 1) // 0xFFFFFFFFC0000000L;

    /** Mask used to clear the non-sign part of an int.  */
    private const val MASK_NON_SIGN_INT = 0x7fffffff

    /** Mask used to clear the non-sign part of a long.  */
    private const val MASK_NON_SIGN_LONG = 0x7fffffffffffffffL

    /** Mask used to extract exponent from double bits.  */
    private const val MASK_DOUBLE_EXPONENT = 0x7ff0000000000000L

    /** Mask used to extract mantissa from double bits.  */
    private const val MASK_DOUBLE_MANTISSA = 0x000fffffffffffffL

    /** Mask used to add implicit high order bit for normalized double.  */
    private const val IMPLICIT_HIGH_BIT = 0x0010000000000000L

    /** 2^52 - double numbers this large must be integral (no fraction) or NaN or Infinite  */
    private const val TWO_POWER_52 = 4503599627370496.0

    /** Constant: {@value}.  */
    private const val F_1_3 = 1.0 / 3.0

    /** Constant: {@value}.  */
    private const val F_1_5 = 1.0 / 5.0

    /** Constant: {@value}.  */
    private const val F_1_7 = 1.0 / 7.0

    /** Constant: {@value}.  */
    private const val F_1_9 = 1.0 / 9.0

    /** Constant: {@value}.  */
    private const val F_1_11 = 1.0 / 11.0

    /** Constant: {@value}.  */
    private const val F_1_13 = 1.0 / 13.0

    /** Constant: {@value}.  */
    private const val F_1_15 = 1.0 / 15.0

    /** Constant: {@value}.  */
    private const val F_1_17 = 1.0 / 17.0

    /** Constant: {@value}.  */
    private const val F_3_4 = 3.0 / 4.0

    /** Constant: {@value}.  */
    private const val F_15_16 = 15.0 / 16.0

    /** Constant: {@value}.  */
    private const val F_13_14 = 13.0 / 14.0

    /** Constant: {@value}.  */
    private const val F_11_12 = 11.0 / 12.0

    /** Constant: {@value}.  */
    private const val F_9_10 = 9.0 / 10.0

    /** Constant: {@value}.  */
    private const val F_7_8 = 7.0 / 8.0

    /** Constant: {@value}.  */
    private const val F_5_6 = 5.0 / 6.0

    /** Constant: {@value}.  */
    private const val F_1_2 = 1.0 / 2.0

    /** Constant: {@value}.  */
    private const val F_1_4 = 1.0 / 4.0
    // Generic helper methods
    /**
     * Get the high order bits from the mantissa.
     * Equivalent to adding and subtracting HEX_40000 but also works for very large numbers
     *
     * @param d the value to split
     * @return the high order part of the mantissa
     */
    private fun doubleHighPart(d: Double): Double {
        if (d > -Precision.SAFE_MIN && d < Precision.SAFE_MIN) {
            return d // These are un-normalised - don't try to convert
        }
        var xl: Long = java.lang.Double.doubleToRawLongBits(d) // can take raw bits because just gonna convert it back
        xl = xl and MASK_30BITS // Drop low order bits
        return java.lang.Double.longBitsToDouble(xl)
    }

    /** Compute the square root of a number.
     *
     * **Note:** this implementation currently delegates to [Math.sqrt]
     * @param a number on which evaluation is done
     * @return square root of a
     */
    fun sqrt(a: Double): Double {
        return java.lang.Math.sqrt(a)
    }

    /** Compute the hyperbolic cosine of a number.
     * @param x number on which evaluation is done
     * @return hyperbolic cosine of x
     */
    fun cosh(x: Double): Double {
        var x = x
        if (x != x) {
            return x
        }

        // cosh[z] = (exp(z) + exp(-z))/2

        // for numbers with magnitude 20 or so,
        // exp(-z) can be ignored in comparison with exp(z)
        if (x > 20) {
            return if (x >= LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                val t = exp(0.5 * x)
                0.5 * t * t
            } else {
                0.5 * exp(x)
            }
        } else if (x < -20) {
            return if (x <= -LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                val t = exp(-0.5 * x)
                0.5 * t * t
            } else {
                0.5 * exp(-x)
            }
        }
        val hiPrec = DoubleArray(2)
        if (x < 0.0) {
            x = -x
        }
        exp(x, 0.0, hiPrec)
        var ya = hiPrec[0] + hiPrec[1]
        var yb = -(ya - hiPrec[0] - hiPrec[1])
        var temp = ya * HEX_40000000
        val yaa = ya + temp - temp
        val yab = ya - yaa

        // recip = 1/y
        val recip = 1.0 / ya
        temp = recip * HEX_40000000
        val recipa = recip + temp - temp
        var recipb = recip - recipa

        // Correct for rounding in division
        recipb += (1.0 - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip
        // Account for yb
        recipb += -yb * recip * recip

        // y = y + 1/y
        temp = ya + recipa
        yb += -(temp - ya - recipa)
        ya = temp
        temp = ya + recipb
        yb += -(temp - ya - recipb)
        ya = temp
        var result = ya + yb
        result *= 0.5
        return result
    }

    /** Compute the hyperbolic sine of a number.
     * @param x number on which evaluation is done
     * @return hyperbolic sine of x
     */
    fun sinh(x: Double): Double {
        var x = x
        var negate = false
        if (x != x) {
            return x
        }

        // sinh[z] = (exp(z) - exp(-z) / 2

        // for values of z larger than about 20,
        // exp(-z) can be ignored in comparison with exp(z)
        if (x > 20) {
            return if (x >= LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                val t = exp(0.5 * x)
                0.5 * t * t
            } else {
                0.5 * exp(x)
            }
        } else if (x < -20) {
            return if (x <= -LOG_MAX_VALUE) {
                // Avoid overflow (MATH-905).
                val t = exp(-0.5 * x)
                -0.5 * t * t
            } else {
                -0.5 * exp(-x)
            }
        }
        if (x == 0.0) {
            return x
        }
        if (x < 0.0) {
            x = -x
            negate = true
        }
        var result: Double
        if (x > 0.25) {
            val hiPrec = DoubleArray(2)
            exp(x, 0.0, hiPrec)
            var ya = hiPrec[0] + hiPrec[1]
            var yb = -(ya - hiPrec[0] - hiPrec[1])
            var temp = ya * HEX_40000000
            val yaa = ya + temp - temp
            val yab = ya - yaa

            // recip = 1/y
            val recip = 1.0 / ya
            temp = recip * HEX_40000000
            var recipa = recip + temp - temp
            var recipb = recip - recipa

            // Correct for rounding in division
            recipb += (1.0 - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip
            // Account for yb
            recipb += -yb * recip * recip
            recipa = -recipa
            recipb = -recipb

            // y = y + 1/y
            temp = ya + recipa
            yb += -(temp - ya - recipa)
            ya = temp
            temp = ya + recipb
            yb += -(temp - ya - recipb)
            ya = temp
            result = ya + yb
            result *= 0.5
        } else {
            val hiPrec = DoubleArray(2)
            expm1(x, hiPrec)
            var ya = hiPrec[0] + hiPrec[1]
            var yb = -(ya - hiPrec[0] - hiPrec[1])

            /* Compute expm1(-x) = -expm1(x) / (expm1(x) + 1) */
            val denom = 1.0 + ya
            val denomr = 1.0 / denom
            val denomb = -(denom - 1.0 - ya) + yb
            val ratio = ya * denomr
            var temp = ratio * HEX_40000000
            val ra = ratio + temp - temp
            var rb = ratio - ra
            temp = denom * HEX_40000000
            val za = denom + temp - temp
            val zb = denom - za
            rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr

            // Adjust for yb
            rb += yb * denomr // numerator
            rb += -ya * denomb * denomr * denomr // denominator

            // y = y - 1/y
            temp = ya + ra
            yb += -(temp - ya - ra)
            ya = temp
            temp = ya + rb
            yb += -(temp - ya - rb)
            ya = temp
            result = ya + yb
            result *= 0.5
        }
        if (negate) {
            result = -result
        }
        return result
    }

    /** Compute the hyperbolic tangent of a number.
     * @param x number on which evaluation is done
     * @return hyperbolic tangent of x
     */
    fun tanh(x: Double): Double {
        var x = x
        var negate = false
        if (x != x) {
            return x
        }

        // tanh[z] = sinh[z] / cosh[z]
        // = (exp(z) - exp(-z)) / (exp(z) + exp(-z))
        // = (exp(2x) - 1) / (exp(2x) + 1)

        // for magnitude > 20, sinh[z] == cosh[z] in double precision
        if (x > 20.0) {
            return 1.0
        }
        if (x < -20) {
            return -1.0
        }
        if (x == 0.0) {
            return x
        }
        if (x < 0.0) {
            x = -x
            negate = true
        }
        var result: Double
        if (x >= 0.5) {
            val hiPrec = DoubleArray(2)
            // tanh(x) = (exp(2x) - 1) / (exp(2x) + 1)
            exp(x * 2.0, 0.0, hiPrec)
            val ya = hiPrec[0] + hiPrec[1]
            val yb = -(ya - hiPrec[0] - hiPrec[1])

            /* Numerator */
            var na = -1.0 + ya
            var nb = -(na + 1.0 - ya)
            var temp = na + yb
            nb += -(temp - na - yb)
            na = temp

            /* Denominator */
            var da = 1.0 + ya
            var db = -(da - 1.0 - ya)
            temp = da + yb
            db += -(temp - da - yb)
            da = temp
            temp = da * HEX_40000000
            val daa = da + temp - temp
            val dab = da - daa

            // ratio = na/da
            val ratio = na / da
            temp = ratio * HEX_40000000
            val ratioa = ratio + temp - temp
            var ratiob = ratio - ratioa

            // Correct for rounding in division
            ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da

            // Account for nb
            ratiob += nb / da
            // Account for db
            ratiob += -db * na / da / da
            result = ratioa + ratiob
        } else {
            val hiPrec = DoubleArray(2)
            // tanh(x) = expm1(2x) / (expm1(2x) + 2)
            expm1(x * 2.0, hiPrec)
            val ya = hiPrec[0] + hiPrec[1]
            val yb = -(ya - hiPrec[0] - hiPrec[1])

            /* Numerator */

            /* Denominator */
            var da = 2.0 + ya
            var db = -(da - 2.0 - ya)
            var temp = da + yb
            db += -(temp - da - yb)
            da = temp
            temp = da * HEX_40000000
            val daa = da + temp - temp
            val dab = da - daa

            // ratio = na/da
            val ratio = ya / da
            temp = ratio * HEX_40000000
            val ratioa = ratio + temp - temp
            var ratiob = ratio - ratioa

            // Correct for rounding in division
            ratiob += (ya - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da

            // Account for nb
            ratiob += yb / da
            // Account for db
            ratiob += -db * ya / da / da
            result = ratioa + ratiob
        }
        if (negate) {
            result = -result
        }
        return result
    }

    /** Compute the inverse hyperbolic cosine of a number.
     * @param a number on which evaluation is done
     * @return inverse hyperbolic cosine of a
     */
    fun acosh(a: Double): Double {
        return log(a + sqrt(a * a - 1))
    }

    /** Compute the inverse hyperbolic sine of a number.
     * @param a number on which evaluation is done
     * @return inverse hyperbolic sine of a
     */
    fun asinh(a: Double): Double {
        var a = a
        var negative = false
        if (a < 0) {
            negative = true
            a = -a
        }
        val absAsinh: Double
        absAsinh = if (a > 0.167) {
            log(sqrt(a * a + 1) + a)
        } else {
            val a2 = a * a
            if (a > 0.097) {
                a * (1 - a2 * (F_1_3 - a2 * (F_1_5 - a2 * (F_1_7 - a2 * (F_1_9 - a2 * (F_1_11 - a2 * (F_1_13 - a2 * (F_1_15 - a2 * F_1_17 * F_15_16) * F_13_14) * F_11_12) * F_9_10) * F_7_8) * F_5_6) * F_3_4) * F_1_2)
            } else if (a > 0.036) {
                a * (1 - a2 * (F_1_3 - a2 * (F_1_5 - a2 * (F_1_7 - a2 * (F_1_9 - a2 * (F_1_11 - a2 * F_1_13 * F_11_12) * F_9_10) * F_7_8) * F_5_6) * F_3_4) * F_1_2)
            } else if (a > 0.0036) {
                a * (1 - a2 * (F_1_3 - a2 * (F_1_5 - a2 * (F_1_7 - a2 * F_1_9 * F_7_8) * F_5_6) * F_3_4) * F_1_2)
            } else {
                a * (1 - a2 * (F_1_3 - a2 * F_1_5 * F_3_4) * F_1_2)
            }
        }
        return if (negative) -absAsinh else absAsinh
    }

    /** Compute the inverse hyperbolic tangent of a number.
     * @param a number on which evaluation is done
     * @return inverse hyperbolic tangent of a
     */
    fun atanh(a: Double): Double {
        var a = a
        var negative = false
        if (a < 0) {
            negative = true
            a = -a
        }
        val absAtanh: Double
        absAtanh = if (a > 0.15) {
            0.5 * log((1 + a) / (1 - a))
        } else {
            val a2 = a * a
            if (a > 0.087) {
                a * (1 + a2 * (F_1_3 + a2 * (F_1_5 + a2 * (F_1_7 + a2 * (F_1_9 + a2 * (F_1_11 + a2 * (F_1_13 + a2 * (F_1_15 + a2 * F_1_17))))))))
            } else if (a > 0.031) {
                a * (1 + a2 * (F_1_3 + a2 * (F_1_5 + a2 * (F_1_7 + a2 * (F_1_9 + a2 * (F_1_11 + a2 * F_1_13))))))
            } else if (a > 0.003) {
                a * (1 + a2 * (F_1_3 + a2 * (F_1_5 + a2 * (F_1_7 + a2 * F_1_9))))
            } else {
                a * (1 + a2 * (F_1_3 + a2 * F_1_5))
            }
        }
        return if (negative) -absAtanh else absAtanh
    }

    /** Compute the signum of a number.
     * The signum is -1 for negative numbers, +1 for positive numbers and 0 otherwise
     * @param a number on which evaluation is done
     * @return -1.0, -0.0, +0.0, +1.0 or NaN depending on sign of a
     */
    fun signum(a: Double): Double {
        return if (a < 0.0) -1.0 else if (a > 0.0) 1.0 else a // return +0.0/-0.0/NaN depending on a
    }

    /** Compute the signum of a number.
     * The signum is -1 for negative numbers, +1 for positive numbers and 0 otherwise
     * @param a number on which evaluation is done
     * @return -1.0, -0.0, +0.0, +1.0 or NaN depending on sign of a
     */
    fun signum(a: Float): Float {
        return if (a < 0.0f) -1.0f else if (a > 0.0f) 1.0f else a // return +0.0/-0.0/NaN depending on a
    }

    /** Compute next number towards positive infinity.
     * @param a number to which neighbor should be computed
     * @return neighbor of a towards positive infinity
     */
    fun nextUp(a: Double): Double {
        return nextAfter(a, Double.POSITIVE_INFINITY)
    }

    /** Compute next number towards positive infinity.
     * @param a number to which neighbor should be computed
     * @return neighbor of a towards positive infinity
     */
    fun nextUp(a: Float): Float {
        return nextAfter(a, Float.POSITIVE_INFINITY.toDouble())
    }

    /** Compute next number towards negative infinity.
     * @param a number to which neighbor should be computed
     * @return neighbor of a towards negative infinity
     * @since 3.4
     */
    fun nextDown(a: Double): Double {
        return nextAfter(a, Double.NEGATIVE_INFINITY)
    }

    /** Compute next number towards negative infinity.
     * @param a number to which neighbor should be computed
     * @return neighbor of a towards negative infinity
     * @since 3.4
     */
    fun nextDown(a: Float): Float {
        return nextAfter(a, Float.NEGATIVE_INFINITY.toDouble())
    }

    /** Returns a pseudo-random number between 0.0 and 1.0.
     *
     * **Note:** this implementation currently delegates to [Math.random]
     * @return a random number between 0.0 and 1.0
     */
    fun random(): Double {
        return java.lang.Math.random()
    }

    /**
     * Exponential function.
     *
     * Computes exp(x), function result is nearly rounded.   It will be correctly
     * rounded to the theoretical value for 99.9% of input values, otherwise it will
     * have a 1 ULP error.
     *
     * Method:
     * Lookup intVal = exp(int(x))
     * Lookup fracVal = exp(int(x-int(x) / 1024.0) * 1024.0 );
     * Compute z as the exponential of the remaining bits by a polynomial minus one
     * exp(x) = intVal * fracVal * (1 + z)
     *
     * Accuracy:
     * Calculation is done with 63 bits of precision, so result should be correctly
     * rounded for 99.9% of input values, with less than 1 ULP error otherwise.
     *
     * @param x   a double
     * @return double e<sup>x</sup>
     */
    fun exp(x: Double): Double {
        return exp(x, 0.0, null)
    }

    /**
     * Internal helper method for exponential function.
     * @param x original argument of the exponential function
     * @param extra extra bits of precision on input (To Be Confirmed)
     * @param hiPrec extra bits of precision on output (To Be Confirmed)
     * @return exp(x)
     */
    private fun exp(x: Double, extra: Double, hiPrec: DoubleArray?): Double {
        val intPartA: Double
        val intPartB: Double
        var intVal = x.toInt()

        /* Lookup exp(floor(x)).
         * intPartA will have the upper 22 bits, intPartB will have the lower
         * 52 bits.
         */if (x < 0.0) {

            // We don't check against intVal here as conversion of large negative double values
            // may be affected by a JIT bug. Subsequent comparisons can safely use intVal
            if (x < -746.0) {
                if (hiPrec != null) {
                    hiPrec[0] = 0.0
                    hiPrec[1] = 0.0
                }
                return 0.0
            }
            if (intVal < -709) {
                /* This will produce a subnormal output */
                val result = exp(x + 40.19140625, extra, hiPrec) / 285040095144011776.0
                if (hiPrec != null) {
                    hiPrec[0] /= 285040095144011776.0
                    hiPrec[1] /= 285040095144011776.0
                }
                return result
            }
            if (intVal == -709) {
                /* exp(1.494140625) is nearly a machine number... */
                val result = exp(x + 1.494140625, extra, hiPrec) / 4.455505956692756620
                if (hiPrec != null) {
                    hiPrec[0] /= 4.455505956692756620
                    hiPrec[1] /= 4.455505956692756620
                }
                return result
            }
            intVal--
        } else {
            if (intVal > 709) {
                if (hiPrec != null) {
                    hiPrec[0] = Double.POSITIVE_INFINITY
                    hiPrec[1] = 0.0
                }
                return Double.POSITIVE_INFINITY
            }
        }
        intPartA = ExpIntTable.EXP_INT_TABLE_A[EXP_INT_TABLE_MAX_INDEX + intVal]
        intPartB = ExpIntTable.EXP_INT_TABLE_B[EXP_INT_TABLE_MAX_INDEX + intVal]

        /* Get the fractional part of x, find the greatest multiple of 2^-10 less than
         * x and look up the exp function of it.
         * fracPartA will have the upper 22 bits, fracPartB the lower 52 bits.
         */
        val intFrac = ((x - intVal) * 1024.0) as Int
        val fracPartA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac]
        val fracPartB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac]

        /* epsilon is the difference in x from the nearest multiple of 2^-10.  It
         * has a value in the range 0 <= epsilon < 2^-10.
         * Do the subtraction from x as the last step to avoid possible loss of precision.
         */
        val epsilon = x - (intVal + intFrac / 1024.0)

        /* Compute z = exp(epsilon) - 1.0 via a minimax polynomial.  z has
       full double precision (52 bits).  Since z < 2^-10, we will have
       62 bits of precision when combined with the constant 1.  This will be
       used in the last addition below to get proper rounding. */

        /* Remez generated polynomial.  Converges on the interval [0, 2^-10], error
       is less than 0.5 ULP */
        var z = 0.04168701738764507
        z = z * epsilon + 0.1666666505023083
        z = z * epsilon + 0.5000000000042687
        z = z * epsilon + 1.0
        z = z * epsilon + -3.940510424527919E-20

        /* Compute (intPartA+intPartB) * (fracPartA+fracPartB) by binomial
       expansion.
       tempA is exact since intPartA and intPartB only have 22 bits each.
       tempB will have 52 bits of precision.
         */
        val tempA = intPartA * fracPartA
        val tempB = intPartA * fracPartB + intPartB * fracPartA + intPartB * fracPartB

        /* Compute the result.  (1+z)(tempA+tempB).  Order of operations is
       important.  For accuracy add by increasing size.  tempA is exact and
       much larger than the others.  If there are extra bits specified from the
       pow() function, use them. */
        val tempC = tempB + tempA

        // If tempC is positive infinite, the evaluation below could result in NaN,
        // because z could be negative at the same time.
        if (tempC == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY
        }
        val result: Double
        result = if (extra != 0.0) {
            tempC * extra * z + tempC * extra + tempC * z + tempB + tempA
        } else {
            tempC * z + tempB + tempA
        }
        if (hiPrec != null) {
            // If requesting high precision
            hiPrec[0] = tempA
            hiPrec[1] = tempC * extra * z + tempC * extra + tempC * z + tempB
        }
        return result
    }

    /** Compute exp(x) - 1
     * @param x number to compute shifted exponential
     * @return exp(x) - 1
     */
    fun expm1(x: Double): Double {
        return expm1(x, null)
    }

    /** Internal helper method for expm1
     * @param x number to compute shifted exponential
     * @param hiPrecOut receive high precision result for -1.0 < x < 1.0
     * @return exp(x) - 1
     */
    private fun expm1(x: Double, hiPrecOut: DoubleArray?): Double {
        var x = x
        if (x != x || x == 0.0) { // NaN or zero
            return x
        }
        if (x <= -1.0 || x >= 1.0) {
            // If not between +/- 1.0
            //return exp(x) - 1.0;
            val hiPrec = DoubleArray(2)
            exp(x, 0.0, hiPrec)
            return if (x > 0.0) {
                -1.0 + hiPrec[0] + hiPrec[1]
            } else {
                val ra = -1.0 + hiPrec[0]
                var rb = -(ra + 1.0 - hiPrec[0])
                rb += hiPrec[1]
                ra + rb
            }
        }
        var baseA: Double
        var baseB: Double
        var epsilon: Double
        var negative = false
        if (x < 0.0) {
            x = -x
            negative = true
        }
        run {
            val intFrac = (x * 1024.0).toInt()
            var tempA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac] - 1.0
            var tempB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac]
            var temp = tempA + tempB
            tempB = -(temp - tempA - tempB)
            tempA = temp
            temp = tempA * HEX_40000000
            baseA = tempA + temp - temp
            baseB = tempB + (tempA - baseA)
            epsilon = x - intFrac / 1024.0
        }


        /* Compute expm1(epsilon) */
        var zb = 0.008336750013465571
        zb = zb * epsilon + 0.041666663879186654
        zb = zb * epsilon + 0.16666666666745392
        zb = zb * epsilon + 0.49999999999999994
        zb *= epsilon
        zb *= epsilon
        var za = epsilon
        var temp = za + zb
        zb = -(temp - za - zb)
        za = temp
        temp = za * HEX_40000000
        temp = za + temp - temp
        zb += za - temp
        za = temp

        /* Combine the parts.   expm1(a+b) = expm1(a) + expm1(b) + expm1(a)*expm1(b) */
        var ya = za * baseA
        //double yb = za*baseB + zb*baseA + zb*baseB;
        temp = ya + za * baseB
        var yb = -(temp - ya - za * baseB)
        ya = temp
        temp = ya + zb * baseA
        yb += -(temp - ya - zb * baseA)
        ya = temp
        temp = ya + zb * baseB
        yb += -(temp - ya - zb * baseB)
        ya = temp

        //ya = ya + za + baseA;
        //yb = yb + zb + baseB;
        temp = ya + baseA
        yb += -(temp - baseA - ya)
        ya = temp
        temp = ya + za
        //yb += (ya > za) ? -(temp - ya - za) : -(temp - za - ya);
        yb += -(temp - ya - za)
        ya = temp
        temp = ya + baseB
        //yb += (ya > baseB) ? -(temp - ya - baseB) : -(temp - baseB - ya);
        yb += -(temp - ya - baseB)
        ya = temp
        temp = ya + zb
        //yb += (ya > zb) ? -(temp - ya - zb) : -(temp - zb - ya);
        yb += -(temp - ya - zb)
        ya = temp
        if (negative) {
            /* Compute expm1(-x) = -expm1(x) / (expm1(x) + 1) */
            val denom = 1.0 + ya
            val denomr = 1.0 / denom
            val denomb = -(denom - 1.0 - ya) + yb
            val ratio = ya * denomr
            temp = ratio * HEX_40000000
            val ra = ratio + temp - temp
            var rb = ratio - ra
            temp = denom * HEX_40000000
            za = denom + temp - temp
            zb = denom - za
            rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr

            // f(x) = x/1+x
            // Compute f'(x)
            // Product rule:  d(uv) = du*v + u*dv
            // Chain rule:  d(f(g(x)) = f'(g(x))*f(g'(x))
            // d(1/x) = -1/(x*x)
            // d(1/1+x) = -1/( (1+x)^2) *  1 =  -1/((1+x)*(1+x))
            // d(x/1+x) = -x/((1+x)(1+x)) + 1/1+x = 1 / ((1+x)(1+x))

            // Adjust for yb
            rb += yb * denomr // numerator
            rb += -ya * denomb * denomr * denomr // denominator

            // negate
            ya = -ra
            yb = -rb
        }
        if (hiPrecOut != null) {
            hiPrecOut[0] = ya
            hiPrecOut[1] = yb
        }
        return ya + yb
    }

    /**
     * Natural logarithm.
     *
     * @param x   a double
     * @return log(x)
     */
    fun log(x: Double): Double {
        return log(x, null)
    }

    /**
     * Internal helper method for natural logarithm function.
     * @param x original argument of the natural logarithm function
     * @param hiPrec extra bits of precision on output (To Be Confirmed)
     * @return log(x)
     */
    private fun log(x: Double, hiPrec: DoubleArray?): Double {
        if (x == 0.0) { // Handle special case of +0/-0
            return Double.NEGATIVE_INFINITY
        }
        var bits: Long = java.lang.Double.doubleToRawLongBits(x)

        /* Handle special cases of negative input, and NaN */if ((bits and (-0x8000000000000000L).toLong() != 0L || x != x) && x != 0.0) {
            if (hiPrec != null) {
                hiPrec[0] = Double.NaN
            }
            return Double.NaN
        }

        /* Handle special cases of Positive infinity. */if (x == Double.POSITIVE_INFINITY) {
            if (hiPrec != null) {
                hiPrec[0] = Double.POSITIVE_INFINITY
            }
            return Double.POSITIVE_INFINITY
        }

        /* Extract the exponent */
        var exp = (bits shr 52).toInt() - 1023
        if (bits and 0x7ff0000000000000L == 0L) {
            // Subnormal!
            if (x == 0.0) {
                // Zero
                if (hiPrec != null) {
                    hiPrec[0] = Double.NEGATIVE_INFINITY
                }
                return Double.NEGATIVE_INFINITY
            }

            /* Normalize the subnormal number. */bits = bits shl 1
            while (bits and 0x0010000000000000L == 0L) {
                --exp
                bits = bits shl 1
            }
        }
        if ((exp == -1 || exp == 0) && x < 1.01 && x > 0.99 && hiPrec == null) {
            /* The normal method doesn't work well in the range [0.99, 1.01], so call do a straight
           polynomial expansion in higer precision. */

            /* Compute x - 1.0 and split it */
            var xa = x - 1.0
            var xb = xa - x + 1.0
            var tmp = xa * HEX_40000000
            var aa = xa + tmp - tmp
            var ab = xa - aa
            xa = aa
            xb = ab
            val lnCoef_last = LN_QUICK_COEF[LN_QUICK_COEF.size - 1]
            var ya = lnCoef_last[0]
            var yb = lnCoef_last[1]
            for (i in LN_QUICK_COEF.size - 2 downTo 0) {
                /* Multiply a = y * x */
                aa = ya * xa
                ab = ya * xb + yb * xa + yb * xb
                /* split, so now y = a */tmp = aa * HEX_40000000
                ya = aa + tmp - tmp
                yb = aa - ya + ab

                /* Add  a = y + lnQuickCoef */
                val lnCoef_i = LN_QUICK_COEF[i]
                aa = ya + lnCoef_i[0]
                ab = yb + lnCoef_i[1]
                /* Split y = a */tmp = aa * HEX_40000000
                ya = aa + tmp - tmp
                yb = aa - ya + ab
            }

            /* Multiply a = y * x */aa = ya * xa
            ab = ya * xb + yb * xa + yb * xb
            /* split, so now y = a */tmp = aa * HEX_40000000
            ya = aa + tmp - tmp
            yb = aa - ya + ab
            return ya + yb
        }

        // lnm is a log of a number in the range of 1.0 - 2.0, so 0 <= lnm < ln(2)
        val lnm = lnMant.LN_MANT[(bits and 0x000ffc0000000000L shr 42).toInt()]

        /*
    double epsilon = x / Double.longBitsToDouble(bits & 0xfffffc0000000000L);

    epsilon -= 1.0;
         */

        // y is the most significant 10 bits of the mantissa
        //double y = Double.longBitsToDouble(bits & 0xfffffc0000000000L);
        //double epsilon = (x - y) / y;
        val epsilon =
            (bits and 0x3ffffffffffL) / (TWO_POWER_52 + (bits and 0x000ffc0000000000L))
        var lnza = 0.0
        var lnzb = 0.0
        if (hiPrec != null) {
            /* split epsilon -> x */
            var tmp = epsilon * HEX_40000000
            var aa = epsilon + tmp - tmp
            var ab = epsilon - aa
            val xa = aa
            var xb = ab

            /* Need a more accurate epsilon, so adjust the division. */
            val numer = (bits and 0x3ffffffffffL.toDouble().toLong()).toDouble()
            val denom = TWO_POWER_52 + (bits and 0x000ffc0000000000L)
            aa = numer - xa * denom - xb * denom
            xb += aa / denom

            /* Remez polynomial evaluation */
            val lnCoef_last = LN_HI_PREC_COEF[LN_HI_PREC_COEF.size - 1]
            var ya = lnCoef_last[0]
            var yb = lnCoef_last[1]
            for (i in LN_HI_PREC_COEF.size - 2 downTo 0) {
                /* Multiply a = y * x */
                aa = ya * xa
                ab = ya * xb + yb * xa + yb * xb
                /* split, so now y = a */tmp = aa * HEX_40000000
                ya = aa + tmp - tmp
                yb = aa - ya + ab

                /* Add  a = y + lnHiPrecCoef */
                val lnCoef_i = LN_HI_PREC_COEF[i]
                aa = ya + lnCoef_i[0]
                ab = yb + lnCoef_i[1]
                /* Split y = a */tmp = aa * HEX_40000000
                ya = aa + tmp - tmp
                yb = aa - ya + ab
            }

            /* Multiply a = y * x */aa = ya * xa
            ab = ya * xb + yb * xa + yb * xb

            /* split, so now lnz = a */
            /*
      tmp = aa * 1073741824.0;
      lnza = aa + tmp - tmp;
      lnzb = aa - lnza + ab;
             */lnza = aa + ab
            lnzb = -(lnza - aa - ab)
        } else {
            /* High precision not required.  Eval Remez polynomial
         using standard double precision */
            lnza = -0.16624882440418567
            lnza = lnza * epsilon + 0.19999954120254515
            lnza = lnza * epsilon + -0.2499999997677497
            lnza = lnza * epsilon + 0.3333333333332802
            lnza = lnza * epsilon + -0.5
            lnza = lnza * epsilon + 1.0
            lnza *= epsilon
        }

        /* Relative sizes:
         * lnzb     [0, 2.33E-10]
         * lnm[1]   [0, 1.17E-7]
         * ln2B*exp [0, 1.12E-4]
         * lnza      [0, 9.7E-4]
         * lnm[0]   [0, 0.692]
         * ln2A*exp [0, 709]
         */

        /* Compute the following sum:
         * lnzb + lnm[1] + ln2B*exp + lnza + lnm[0] + ln2A*exp;
         */

        //return lnzb + lnm[1] + ln2B*exp + lnza + lnm[0] + ln2A*exp;
        var a = LN_2_A * exp
        var b = 0.0
        var c = a + lnm!![0]
        var d = -(c - a - lnm[0])
        a = c
        b += d
        c = a + lnza
        d = -(c - a - lnza)
        a = c
        b += d
        c = a + LN_2_B * exp
        d = -(c - a - LN_2_B * exp)
        a = c
        b += d
        c = a + lnm[1]
        d = -(c - a - lnm[1])
        a = c
        b += d
        c = a + lnzb
        d = -(c - a - lnzb)
        a = c
        b += d
        if (hiPrec != null) {
            hiPrec[0] = a
            hiPrec[1] = b
        }
        return a + b
    }

    /**
     * Computes log(1 + x).
     *
     * @param x Number.
     * @return `log(1 + x)`.
     */
    fun log1p(x: Double): Double {
        if (x == -1.0) {
            return Double.NEGATIVE_INFINITY
        }
        if (x == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY
        }
        return if (x > 1e-6 ||
            x < -1e-6
        ) {
            val xpa = 1 + x
            val xpb = -(xpa - 1 - x)
            val hiPrec = DoubleArray(2)
            val lores = log(xpa, hiPrec)
            if (java.lang.Double.isInfinite(lores)) { // Don't allow this to be converted to NaN
                return lores
            }

            // Do a taylor series expansion around xpa:
            //   f(x+y) = f(x) + f'(x) y + f''(x)/2 y^2
            val fx1 = xpb / xpa
            val epsilon = 0.5 * fx1 + 1
            epsilon * fx1 + hiPrec[1] + hiPrec[0]
        } else {
            // Value is small |x| < 1e6, do a Taylor series centered on 1.
            val y = (x * F_1_3 - F_1_2) * x + 1
            y * x
        }
    }

    /** Compute the base 10 logarithm.
     * @param x a number
     * @return log10(x)
     */
    fun log10(x: Double): Double {
        val hiPrec = DoubleArray(2)
        val lores = log(x, hiPrec)
        if (java.lang.Double.isInfinite(lores)) { // don't allow this to be converted to NaN
            return lores
        }
        val tmp = hiPrec[0] * HEX_40000000
        val lna = hiPrec[0] + tmp - tmp
        val lnb = hiPrec[0] - lna + hiPrec[1]
        val rln10a = 0.4342944622039795
        val rln10b = 1.9699272335463627E-8
        return rln10b * lnb + rln10b * lna + rln10a * lnb + rln10a * lna
    }

    /**
     * Computes the [
 * logarithm](http://mathworld.wolfram.com/Logarithm.html) in a given base.
     *
     * Returns `NaN` if either argument is negative.
     * If `base` is 0 and `x` is positive, 0 is returned.
     * If `base` is positive and `x` is 0,
     * `Double.NEGATIVE_INFINITY` is returned.
     * If both arguments are 0, the result is `NaN`.
     *
     * @param base Base of the logarithm, must be greater than 0.
     * @param x Argument, must be greater than 0.
     * @return the value of the logarithm, i.e. the number `y` such that
     * `base<sup>y</sup> = x`.
     * @since 1.2 (previously in `MathUtils`, moved as of version 3.0)
     */
    fun log(base: Double, x: Double): Double {
        return log(x) / log(base)
    }

    /**
     * Power function.  Compute x^y.
     *
     * @param x   a double
     * @param y   a double
     * @return double
     */
    fun pow(x: Double, y: Double): Double {
        return if (y == 0.0) {
            // y = -0 or y = +0
            1.0
        } else {
            val yBits: Long = java.lang.Double.doubleToRawLongBits(y)
            val yRawExp = (yBits and MASK_DOUBLE_EXPONENT shr 52).toInt()
            val yRawMantissa = yBits and MASK_DOUBLE_MANTISSA
            val xBits: Long = java.lang.Double.doubleToRawLongBits(x)
            val xRawExp = (xBits and MASK_DOUBLE_EXPONENT shr 52).toInt()
            val xRawMantissa = xBits and MASK_DOUBLE_MANTISSA
            if (yRawExp > 1085) {
                // y is either a very large integral value that does not fit in a long or it is a special number
                if (yRawExp == 2047 && yRawMantissa != 0L ||
                    xRawExp == 2047 && xRawMantissa != 0L
                ) {
                    // NaN
                    Double.NaN
                } else if (xRawExp == 1023 && xRawMantissa == 0L) {
                    // x = -1.0 or x = +1.0
                    if (yRawExp == 2047) {
                        // y is infinite
                        Double.NaN
                    } else {
                        // y is a large even integer
                        1.0
                    }
                } else {
                    // the absolute value of x is either greater or smaller than 1.0

                    // if yRawExp == 2047 and mantissa is 0, y = -infinity or y = +infinity
                    // if 1085 < yRawExp < 2047, y is simply a large number, however, due to limited
                    // accuracy, at this magnitude it behaves just like infinity with regards to x
                    if ((y > 0) xor (xRawExp < 1023)) {
                        // either y = +infinity (or large engouh) and abs(x) > 1.0
                        // or     y = -infinity (or large engouh) and abs(x) < 1.0
                        Double.POSITIVE_INFINITY
                    } else {
                        // either y = +infinity (or large engouh) and abs(x) < 1.0
                        // or     y = -infinity (or large engouh) and abs(x) > 1.0
                        +0.0
                    }
                }
            } else {
                // y is a regular non-zero number
                if (yRawExp >= 1023) {
                    // y may be an integral value, which should be handled specifically
                    val yFullMantissa = IMPLICIT_HIGH_BIT or yRawMantissa
                    if (yRawExp < 1075) {
                        // normal number with negative shift that may have a fractional part
                        val integralMask = -1L shl 1075 - yRawExp
                        if (yFullMantissa and integralMask == yFullMantissa) {
                            // all fractional bits are 0, the number is really integral
                            val l = yFullMantissa shr 1075 - yRawExp
                            return pow(x, if (y < 0) -l else l)
                        }
                    } else {
                        // normal number with positive shift, always an integral value
                        // we know it fits in a primitive long because yRawExp > 1085 has been handled above
                        val l = yFullMantissa shl yRawExp - 1075
                        return pow(x, if (y < 0) -l else l)
                    }
                }

                // y is a non-integral value
                if (x == 0.0) {
                    // x = -0 or x = +0
                    // the integer powers have already been handled above
                    if (y < 0) Double.POSITIVE_INFINITY else +0.0
                } else if (xRawExp == 2047) {
                    if (xRawMantissa == 0L) {
                        // x = -infinity or x = +infinity
                        if (y < 0) +0.0 else Double.POSITIVE_INFINITY
                    } else {
                        // NaN
                        Double.NaN
                    }
                } else if (x < 0) {
                    // the integer powers have already been handled above
                    Double.NaN
                } else {

                    // this is the general case, for regular fractional numbers x and y

                    // Split y into ya and yb such that y = ya+yb
                    val tmp = y * HEX_40000000
                    val ya = y + tmp - tmp
                    val yb = y - ya

                    /* Compute ln(x) */
                    val lns = DoubleArray(2)
                    val lores = log(x, lns)
                    if (java.lang.Double.isInfinite(lores)) { // don't allow this to be converted to NaN
                        return lores
                    }
                    var lna = lns[0]
                    var lnb = lns[1]

                    /* resplit lns */
                    val tmp1 = lna * HEX_40000000
                    val tmp2 = lna + tmp1 - tmp1
                    lnb += lna - tmp2
                    lna = tmp2

                    // y*ln(x) = (aa+ab)
                    val aa = lna * ya
                    val ab = lna * yb + lnb * ya + lnb * yb
                    lna = aa + ab
                    lnb = -(lna - aa - ab)
                    var z = 1.0 / 120.0
                    z = z * lnb + 1.0 / 24.0
                    z = z * lnb + 1.0 / 6.0
                    z = z * lnb + 0.5
                    z = z * lnb + 1.0
                    z *= lnb
                    //result = result + result * z;
                    exp(lna, z, null)
                }
            }
        }
    }

    /**
     * Raise a double to an int power.
     *
     * @param d Number to raise.
     * @param e Exponent.
     * @return d<sup>e</sup>
     * @since 3.1
     */
    fun pow(d: Double, e: Int): Double {
        return pow(d, e.toLong())
    }

    /**
     * Raise a double to a long power.
     *
     * @param d Number to raise.
     * @param e Exponent.
     * @return d<sup>e</sup>
     * @since 3.6
     */
    fun pow(d: Double, e: Long): Double {
        return if (e == 0L) {
            1.0
        } else if (e > 0) {
            Split(d).pow(e).full
        } else {
            Split(d).reciprocal().pow(-e).full
        }
    }

    /**
     * Computes sin(x) - x, where |x| < 1/16.
     * Use a Remez polynomial approximation.
     * @param x a number smaller than 1/16
     * @return sin(x) - x
     */
    private fun polySine(x: Double): Double {
        val x2 = x * x
        var p = 2.7553817452272217E-6
        p = p * x2 + -1.9841269659586505E-4
        p = p * x2 + 0.008333333333329196
        p = p * x2 + -0.16666666666666666
        //p *= x2;
        //p *= x;
        p = p * x2 * x
        return p
    }

    /**
     * Computes cos(x) - 1, where |x| < 1/16.
     * Use a Remez polynomial approximation.
     * @param x a number smaller than 1/16
     * @return cos(x) - 1
     */
    private fun polyCosine(x: Double): Double {
        val x2 = x * x
        var p = 2.479773539153719E-5
        p = p * x2 + -0.0013888888689039883
        p = p * x2 + 0.041666666666621166
        p = p * x2 + -0.49999999999999994
        p *= x2
        return p
    }

    /**
     * Compute sine over the first quadrant (0 < x < pi/2).
     * Use combination of table lookup and rational polynomial expansion.
     * @param xa number from which sine is requested
     * @param xb extra bits for x (may be 0.0)
     * @return sin(xa + xb)
     */
    private fun sinQ(xa: Double, xb: Double): Double {
        val idx = (xa * 8.0 + 0.5).toInt()
        val epsilon = xa - EIGHTHS[idx] //idx*0.125;

        // Table lookups
        val sintA = SINE_TABLE_A[idx]
        val sintB = SINE_TABLE_B[idx]
        val costA = COSINE_TABLE_A[idx]
        val costB = COSINE_TABLE_B[idx]

        // Polynomial eval of sin(epsilon), cos(epsilon)
        var sinEpsA = epsilon
        var sinEpsB = polySine(epsilon)
        val cosEpsA = 1.0
        val cosEpsB = polyCosine(epsilon)

        // Split epsilon   xa + xb = x
        val temp = sinEpsA * HEX_40000000
        val temp2 = sinEpsA + temp - temp
        sinEpsB += sinEpsA - temp2
        sinEpsA = temp2

        /* Compute sin(x) by angle addition formula */
        val result: Double

        /* Compute the following sum:
         *
         * result = sintA + costA*sinEpsA + sintA*cosEpsB + costA*sinEpsB +
         *          sintB + costB*sinEpsA + sintB*cosEpsB + costB*sinEpsB;
         *
         * Ranges of elements
         *
         * xxxtA   0            PI/2
         * xxxtB   -1.5e-9      1.5e-9
         * sinEpsA -0.0625      0.0625
         * sinEpsB -6e-11       6e-11
         * cosEpsA  1.0
         * cosEpsB  0           -0.0625
         *
         */

        //result = sintA + costA*sinEpsA + sintA*cosEpsB + costA*sinEpsB +
        //          sintB + costB*sinEpsA + sintB*cosEpsB + costB*sinEpsB;

        //result = sintA + sintA*cosEpsB + sintB + sintB * cosEpsB;
        //result += costA*sinEpsA + costA*sinEpsB + costB*sinEpsA + costB * sinEpsB;
        var a = 0.0
        var b = 0.0
        var t = sintA
        var c = a + t
        var d = -(c - a - t)
        a = c
        b += d
        t = costA * sinEpsA
        c = a + t
        d = -(c - a - t)
        a = c
        b += d
        b = b + sintA * cosEpsB + costA * sinEpsB
        /*
    t = sintA*cosEpsB;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b = b + d;

    t = costA*sinEpsB;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b = b + d;
         */b = b + sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB
        /*
    t = sintB;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b = b + d;

    t = costB*sinEpsA;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b = b + d;

    t = sintB*cosEpsB;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b = b + d;

    t = costB*sinEpsB;
    c = a + t;
    d = -(c - a - t);
    a = c;
    b = b + d;
         */if (xb != 0.0) {
            t = ((costA + costB) * (cosEpsA + cosEpsB) -
                    (sintA + sintB) * (sinEpsA + sinEpsB)) * xb // approximate cosine*xb
            c = a + t
            d = -(c - a - t)
            a = c
            b += d
        }
        result = a + b
        return result
    }

    /**
     * Compute cosine in the first quadrant by subtracting input from PI/2 and
     * then calling sinQ.  This is more accurate as the input approaches PI/2.
     * @param xa number from which cosine is requested
     * @param xb extra bits for x (may be 0.0)
     * @return cos(xa + xb)
     */
    private fun cosQ(xa: Double, xb: Double): Double {
        val pi2a = 1.5707963267948966
        val pi2b = 6.123233995736766E-17
        val a = pi2a - xa
        var b = -(a - pi2a + xa)
        b += pi2b - xb
        return sinQ(a, b)
    }

    /**
     * Compute tangent (or cotangent) over the first quadrant.   0 < x < pi/2
     * Use combination of table lookup and rational polynomial expansion.
     * @param xa number from which sine is requested
     * @param xb extra bits for x (may be 0.0)
     * @param cotanFlag if true, compute the cotangent instead of the tangent
     * @return tan(xa+xb) (or cotangent, depending on cotanFlag)
     */
    private fun tanQ(xa: Double, xb: Double, cotanFlag: Boolean): Double {
        val idx = (xa * 8.0 + 0.5).toInt()
        val epsilon = xa - EIGHTHS[idx] //idx*0.125;

        // Table lookups
        val sintA = SINE_TABLE_A[idx]
        val sintB = SINE_TABLE_B[idx]
        val costA = COSINE_TABLE_A[idx]
        val costB = COSINE_TABLE_B[idx]

        // Polynomial eval of sin(epsilon), cos(epsilon)
        var sinEpsA = epsilon
        var sinEpsB = polySine(epsilon)
        val cosEpsA = 1.0
        val cosEpsB = polyCosine(epsilon)

        // Split epsilon   xa + xb = x
        var temp = sinEpsA * HEX_40000000
        val temp2 = sinEpsA + temp - temp
        sinEpsB += sinEpsA - temp2
        sinEpsA = temp2

        /* Compute sin(x) by angle addition formula */

        /* Compute the following sum:
         *
         * result = sintA + costA*sinEpsA + sintA*cosEpsB + costA*sinEpsB +
         *          sintB + costB*sinEpsA + sintB*cosEpsB + costB*sinEpsB;
         *
         * Ranges of elements
         *
         * xxxtA   0            PI/2
         * xxxtB   -1.5e-9      1.5e-9
         * sinEpsA -0.0625      0.0625
         * sinEpsB -6e-11       6e-11
         * cosEpsA  1.0
         * cosEpsB  0           -0.0625
         *
         */

        //result = sintA + costA*sinEpsA + sintA*cosEpsB + costA*sinEpsB +
        //          sintB + costB*sinEpsA + sintB*cosEpsB + costB*sinEpsB;

        //result = sintA + sintA*cosEpsB + sintB + sintB * cosEpsB;
        //result += costA*sinEpsA + costA*sinEpsB + costB*sinEpsA + costB * sinEpsB;
        var a = 0.0
        var b = 0.0

        // Compute sine
        var t = sintA
        var c = a + t
        var d = -(c - a - t)
        a = c
        b += d
        t = costA * sinEpsA
        c = a + t
        d = -(c - a - t)
        a = c
        b += d
        b += sintA * cosEpsB + costA * sinEpsB
        b += sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB
        var sina = a + b
        var sinb = -(sina - a - b)

        // Compute cosine
        d = 0.0
        c = d
        b = c
        a = b
        t = costA * cosEpsA
        c = a + t
        d = -(c - a - t)
        a = c
        b += d
        t = -sintA * sinEpsA
        c = a + t
        d = -(c - a - t)
        a = c
        b += d
        b += costB * cosEpsA + costA * cosEpsB + costB * cosEpsB
        b -= sintB * sinEpsA + sintA * sinEpsB + sintB * sinEpsB
        var cosa = a + b
        var cosb = -(cosa - a - b)
        if (cotanFlag) {
            var tmp: Double
            tmp = cosa
            cosa = sina
            sina = tmp
            tmp = cosb
            cosb = sinb
            sinb = tmp
        }


        /* estimate and correct, compute 1.0/(cosa+cosb) */
        /*
    double est = (sina+sinb)/(cosa+cosb);
    double err = (sina - cosa*est) + (sinb - cosb*est);
    est += err/(cosa+cosb);
    err = (sina - cosa*est) + (sinb - cosb*est);
         */

        // f(x) = 1/x,   f'(x) = -1/x^2
        val est = sina / cosa

        /* Split the estimate to get more accurate read on division rounding */temp = est * HEX_40000000
        val esta = est + temp - temp
        val estb = est - esta
        temp = cosa * HEX_40000000
        val cosaa = cosa + temp - temp
        val cosab = cosa - cosaa

        //double err = (sina - est*cosa)/cosa;  // Correction for division rounding
        var err =
            (sina - esta * cosaa - esta * cosab - estb * cosaa - estb * cosab) / cosa // Correction for division rounding
        err += sinb / cosa // Change in est due to sinb
        err += -sina * cosb / cosa / cosa // Change in est due to cosb
        if (xb != 0.0) {
            // tan' = 1 + tan^2      cot' = -(1 + cot^2)
            // Approximate impact of xb
            var xbadj = xb + est * est * xb
            if (cotanFlag) {
                xbadj = -xbadj
            }
            err += xbadj
        }
        return est + err
    }

    /** Reduce the input argument using the Payne and Hanek method.
     * This is good for all inputs 0.0 < x < inf
     * Output is remainder after dividing by PI/2
     * The result array should contain 3 numbers.
     * result[0] is the integer portion, so mod 4 this gives the quadrant.
     * result[1] is the upper bits of the remainder
     * result[2] is the lower bits of the remainder
     *
     * @param x number to reduce
     * @param result placeholder where to put the result
     */
    private fun reducePayneHanek(x: Double, result: DoubleArray) {
        /* Convert input double to bits */
        var inbits: Long = java.lang.Double.doubleToRawLongBits(x)
        var exponent = (inbits shr 52 and 0x7ff).toInt() - 1023

        /* Convert to fixed point representation */inbits = inbits and 0x000fffffffffffffL
        inbits = inbits or 0x0010000000000000L

        /* Normalize input to be between 0.5 and 1.0 */exponent++
        inbits = inbits shl 11

        /* Based on the exponent, get a shifted copy of recip2pi */
        var shpi0: Long
        val shpiA: Long
        val shpiB: Long
        val idx = exponent shr 6
        val shift = exponent - (idx shl 6)
        if (shift != 0) {
            shpi0 = if (idx == 0) 0 else RECIP_2PI[idx - 1] shl shift
            shpi0 = shpi0 or (RECIP_2PI[idx] ushr 64 - shift)
            shpiA = RECIP_2PI[idx] shl shift or (RECIP_2PI[idx + 1] ushr 64 - shift)
            shpiB = RECIP_2PI[idx + 1] shl shift or (RECIP_2PI[idx + 2] ushr 64 - shift)
        } else {
            shpi0 = if (idx == 0) 0 else RECIP_2PI[idx - 1]
            shpiA = RECIP_2PI[idx]
            shpiB = RECIP_2PI[idx + 1]
        }

        /* Multiply input by shpiA */
        var a = inbits ushr 32
        var b = inbits and 0xffffffffL
        var c = shpiA ushr 32
        var d = shpiA and 0xffffffffL
        var ac = a * c
        var bd = b * d
        var bc = b * c
        var ad = a * d
        var prodB = bd + (ad shl 32)
        var prodA = ac + (ad ushr 32)
        var bita = bd and (-0x8000000000000000L).toLong() != 0L
        var bitb = ad and 0x80000000L != 0L
        var bitsum = prodB and (-0x8000000000000000L).toLong() != 0L

        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prodA++
        }
        bita = prodB and (-0x8000000000000000L).toLong() != 0L
        bitb = bc and 0x80000000L != 0L
        prodB += bc shl 32
        prodA += bc ushr 32
        bitsum = prodB and (-0x8000000000000000L).toLong() != 0L

        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prodA++
        }

        /* Multiply input by shpiB */c = shpiB ushr 32
        d = shpiB and 0xffffffffL
        ac = a * c
        bc = b * c
        ad = a * d

        /* Collect terms */ac += bc + ad ushr 32
        bita = prodB and (-0x8000000000000000L).toLong() != 0L
        bitb = ac and (-0x8000000000000000L).toLong() != 0L
        prodB += ac
        bitsum = prodB and (-0x8000000000000000L).toLong() != 0L
        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prodA++
        }

        /* Multiply by shpi0 */c = shpi0 ushr 32
        d = shpi0 and 0xffffffffL
        bd = b * d
        bc = b * c
        ad = a * d
        prodA += bd + (bc + ad shl 32)

        /*
         * prodA, prodB now contain the remainder as a fraction of PI.  We want this as a fraction of
         * PI/2, so use the following steps:
         * 1.) multiply by 4.
         * 2.) do a fixed point muliply by PI/4.
         * 3.) Convert to floating point.
         * 4.) Multiply by 2
         */

        /* This identifies the quadrant */
        val intPart = (prodA ushr 62).toInt()

        /* Multiply by 4 */prodA = prodA shl 2
        prodA = prodA or (prodB ushr 62)
        prodB = prodB shl 2

        /* Multiply by PI/4 */a = prodA ushr 32
        b = prodA and 0xffffffffL
        c = PI_O_4_BITS[0] ushr 32
        d = PI_O_4_BITS[0] and 0xffffffffL
        ac = a * c
        bd = b * d
        bc = b * c
        ad = a * d
        var prod2B = bd + (ad shl 32)
        var prod2A = ac + (ad ushr 32)
        bita = bd and (-0x8000000000000000L).toLong() != 0L
        bitb = ad and 0x80000000L != 0L
        bitsum = prod2B and (-0x8000000000000000L).toLong() != 0L

        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prod2A++
        }
        bita = prod2B and (-0x8000000000000000L).toLong() != 0L
        bitb = bc and 0x80000000L != 0L
        prod2B += bc shl 32
        prod2A += bc ushr 32
        bitsum = prod2B and (-0x8000000000000000L).toLong() != 0L

        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prod2A++
        }

        /* Multiply input by pio4bits[1] */c = PI_O_4_BITS[1] ushr 32
        d = PI_O_4_BITS[1] and 0xffffffffL
        ac = a * c
        bc = b * c
        ad = a * d

        /* Collect terms */ac += bc + ad ushr 32
        bita = prod2B and (-0x8000000000000000L).toLong() != 0L
        bitb = ac and (-0x8000000000000000L).toLong() != 0L
        prod2B += ac
        bitsum = prod2B and (-0x8000000000000000L).toLong() != 0L
        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prod2A++
        }

        /* Multiply inputB by pio4bits[0] */a = prodB ushr 32
        b = prodB and 0xffffffffL
        c = PI_O_4_BITS[0] ushr 32
        d = PI_O_4_BITS[0] and 0xffffffffL
        ac = a * c
        bc = b * c
        ad = a * d

        /* Collect terms */ac += bc + ad ushr 32
        bita = prod2B and (-0x8000000000000000L).toLong() != 0L
        bitb = ac and (-0x8000000000000000L).toLong() != 0L
        prod2B += ac
        bitsum = prod2B and (-0x8000000000000000L).toLong() != 0L
        /* Carry */if (bita && bitb ||
            (bita || bitb) && !bitsum
        ) {
            prod2A++
        }

        /* Convert to double */
        val tmpA = (prod2A ushr 12) / TWO_POWER_52 // High order 52 bits
        val tmpB =
            ((prod2A and 0xfffL shl 40) + (prod2B ushr 24)) / TWO_POWER_52 / TWO_POWER_52 // Low bits
        val sumA = tmpA + tmpB
        val sumB = -(sumA - tmpA - tmpB)

        /* Multiply by PI/2 and return */result[0] = intPart.toDouble()
        result[1] = sumA * 2.0
        result[2] = sumB * 2.0
    }

    /**
     * Sine function.
     *
     * @param x Argument.
     * @return sin(x)
     */
    fun sin(x: Double): Double {
        var negative = false
        var quadrant = 0
        var xa: Double
        var xb = 0.0

        /* Take absolute value of the input */xa = x
        if (x < 0) {
            negative = true
            xa = -xa
        }

        /* Check for zero and negative zero */if (xa == 0.0) {
            val bits: Long = java.lang.Double.doubleToRawLongBits(x)
            return if (bits < 0) {
                -0.0
            } else 0.0
        }
        if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN
        }

        /* Perform any argument reduction */if (xa > 3294198.0) {
            // PI * (2**20)
            // Argument too big for CodyWaite reduction.  Must use
            // PayneHanek.
            val reduceResults = DoubleArray(3)
            reducePayneHanek(xa, reduceResults)
            quadrant = reduceResults[0].toInt() and 3
            xa = reduceResults[1]
            xb = reduceResults[2]
        } else if (xa > 1.5707963267948966) {
            val cw = CodyWaite(xa)
            quadrant = cw.k and 3
            xa = cw.remA
            xb = cw.remB
        }
        if (negative) {
            quadrant = quadrant xor 2 // Flip bit 1
        }
        return when (quadrant) {
            0 -> sinQ(xa, xb)
            1 -> cosQ(xa, xb)
            2 -> -sinQ(xa, xb)
            3 -> -cosQ(xa, xb)
            else -> Double.NaN
        }
    }

    /**
     * Cosine function.
     *
     * @param x Argument.
     * @return cos(x)
     */
    fun cos(x: Double): Double {
        var quadrant = 0

        /* Take absolute value of the input */
        var xa = x
        if (x < 0) {
            xa = -xa
        }
        if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN
        }

        /* Perform any argument reduction */
        var xb = 0.0
        if (xa > 3294198.0) {
            // PI * (2**20)
            // Argument too big for CodyWaite reduction.  Must use
            // PayneHanek.
            val reduceResults = DoubleArray(3)
            reducePayneHanek(xa, reduceResults)
            quadrant = reduceResults[0].toInt() and 3
            xa = reduceResults[1]
            xb = reduceResults[2]
        } else if (xa > 1.5707963267948966) {
            val cw = CodyWaite(xa)
            quadrant = cw.k and 3
            xa = cw.remA
            xb = cw.remB
        }
        return when (quadrant) {
            0 -> cosQ(xa, xb)
            1 -> -sinQ(xa, xb)
            2 -> -cosQ(xa, xb)
            3 -> sinQ(xa, xb)
            else -> Double.NaN
        }
    }

    /**
     * Tangent function.
     *
     * @param x Argument.
     * @return tan(x)
     */
    fun tan(x: Double): Double {
        var negative = false
        var quadrant = 0

        /* Take absolute value of the input */
        var xa = x
        if (x < 0) {
            negative = true
            xa = -xa
        }

        /* Check for zero and negative zero */if (xa == 0.0) {
            val bits: Long = java.lang.Double.doubleToRawLongBits(x)
            return if (bits < 0) {
                -0.0
            } else 0.0
        }
        if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN
        }

        /* Perform any argument reduction */
        var xb = 0.0
        if (xa > 3294198.0) {
            // PI * (2**20)
            // Argument too big for CodyWaite reduction.  Must use
            // PayneHanek.
            val reduceResults = DoubleArray(3)
            reducePayneHanek(xa, reduceResults)
            quadrant = reduceResults[0].toInt() and 3
            xa = reduceResults[1]
            xb = reduceResults[2]
        } else if (xa > 1.5707963267948966) {
            val cw = CodyWaite(xa)
            quadrant = cw.k and 3
            xa = cw.remA
            xb = cw.remB
        }
        if (xa > 1.5) {
            // Accuracy suffers between 1.5 and PI/2
            val pi2a = 1.5707963267948966
            val pi2b = 6.123233995736766E-17
            val a = pi2a - xa
            var b = -(a - pi2a + xa)
            b += pi2b - xb
            xa = a + b
            xb = -(xa - a - b)
            quadrant = quadrant xor 1
            negative = negative xor true
        }
        var result: Double
        result = if (quadrant and 1 == 0) {
            tanQ(xa, xb, false)
        } else {
            -tanQ(xa, xb, true)
        }
        if (negative) {
            result = -result
        }
        return result
    }

    /**
     * Arctangent function
     * @param x a number
     * @return atan(x)
     */
    fun atan(x: Double): Double {
        return atan(x, 0.0, false)
    }

    /** Internal helper function to compute arctangent.
     * @param xa number from which arctangent is requested
     * @param xb extra bits for x (may be 0.0)
     * @param leftPlane if true, result angle must be put in the left half plane
     * @return atan(xa + xb) (or angle shifted by `PI` if leftPlane is true)
     */
    private fun atan(xa: Double, xb: Double, leftPlane: Boolean): Double {
        var xa = xa
        var xb = xb
        if (xa == 0.0) { // Matches +/- 0.0; return correct sign
            return if (leftPlane) copySign(java.lang.Math.PI, xa) else xa
        }
        val negate: Boolean
        if (xa < 0) {
            // negative
            xa = -xa
            xb = -xb
            negate = true
        } else {
            negate = false
        }
        if (xa > 1.633123935319537E16) { // Very large input
            return if (negate xor leftPlane) -java.lang.Math.PI * F_1_2 else java.lang.Math.PI * F_1_2
        }

        /* Estimate the closest tabulated arctan value, compute eps = xa-tangentTable */
        val idx: Int
        idx = if (xa < 1) {
            ((-1.7168146928204136 * xa * xa + 8.0) * xa + 0.5).toInt()
        } else {
            val oneOverXa = 1 / xa
            (-((-1.7168146928204136 * oneOverXa * oneOverXa + 8.0) * oneOverXa) + 13.07).toInt()
        }
        val ttA = TANGENT_TABLE_A[idx]
        val ttB = TANGENT_TABLE_B[idx]
        var epsA = xa - ttA
        var epsB = -(epsA - xa + ttA)
        epsB += xb - ttB
        var temp = epsA + epsB
        epsB = -(temp - epsA - epsB)
        epsA = temp

        /* Compute eps = eps / (1.0 + xa*tangent) */temp = xa * HEX_40000000
        var ya = xa + temp - temp
        var yb = xb + xa - ya
        xa = ya
        xb += yb

        //if (idx > 8 || idx == 0)
        if (idx == 0) {
            /* If the slope of the arctan is gentle enough (< 0.45), this approximation will suffice */
            //double denom = 1.0 / (1.0 + xa*tangentTableA[idx] + xb*tangentTableA[idx] + xa*tangentTableB[idx] + xb*tangentTableB[idx]);
            val denom = 1.0 / (1.0 + (xa + xb) * (ttA + ttB))
            //double denom = 1.0 / (1.0 + xa*tangentTableA[idx]);
            ya = epsA * denom
            yb = epsB * denom
        } else {
            var temp2 = xa * ttA
            var za = 1.0 + temp2
            var zb = -(za - 1.0 - temp2)
            temp2 = xb * ttA + xa * ttB
            temp = za + temp2
            zb += -(temp - za - temp2)
            za = temp
            zb += xb * ttB
            ya = epsA / za
            temp = ya * HEX_40000000
            val yaa = ya + temp - temp
            val yab = ya - yaa
            temp = za * HEX_40000000
            val zaa = za + temp - temp
            val zab = za - zaa

            /* Correct for rounding in division */yb =
                (epsA - yaa * zaa - yaa * zab - yab * zaa - yab * zab) / za
            yb += -epsA * zb / za / za
            yb += epsB / za
        }
        epsA = ya
        epsB = yb

        /* Evaluate polynomial */
        val epsA2 = epsA * epsA

        /*
    yb = -0.09001346640161823;
    yb = yb * epsA2 + 0.11110718400605211;
    yb = yb * epsA2 + -0.1428571349122913;
    yb = yb * epsA2 + 0.19999999999273194;
    yb = yb * epsA2 + -0.33333333333333093;
    yb = yb * epsA2 * epsA;
         */yb = 0.07490822288864472
        yb = yb * epsA2 - 0.09088450866185192
        yb = yb * epsA2 + 0.11111095942313305
        yb = yb * epsA2 - 0.1428571423679182
        yb = yb * epsA2 + 0.19999999999923582
        yb = yb * epsA2 - 0.33333333333333287
        yb = yb * epsA2 * epsA
        ya = epsA
        temp = ya + yb
        yb = -(temp - ya - yb)
        ya = temp

        /* Add in effect of epsB.   atan'(x) = 1/(1+x^2) */yb += epsB / (1.0 + epsA * epsA)
        val eighths = EIGHTHS[idx]

        //result = yb + eighths[idx] + ya;
        var za = eighths + ya
        var zb = -(za - eighths - ya)
        temp = za + yb
        zb += -(temp - za - yb)
        za = temp
        var result = za + zb
        if (leftPlane) {
            // Result is in the left plane
            val resultb = -(result - za - zb)
            val pia = 1.5707963267948966 * 2
            val pib = 6.123233995736766E-17 * 2
            za = pia - result
            zb = -(za - pia + result)
            zb += pib - resultb
            result = za + zb
        }
        if (negate xor leftPlane) {
            result = -result
        }
        return result
    }

    /**
     * Two arguments arctangent function
     * @param y ordinate
     * @param x abscissa
     * @return phase angle of point (x,y) between `-PI` and `PI`
     */
    fun atan2(y: Double, x: Double): Double {
        if (x != x || y != y) {
            return Double.NaN
        }
        if (y == 0.0) {
            val result = x * y
            val invx = 1.0 / x
            val invy = 1.0 / y
            if (invx == 0.0) { // X is infinite
                return if (x > 0) {
                    y // return +/- 0.0
                } else {
                    copySign(java.lang.Math.PI, y)
                }
            }
            return if (x < 0 || invx < 0) {
                if (y < 0 || invy < 0) {
                    -java.lang.Math.PI
                } else {
                    java.lang.Math.PI
                }
            } else {
                result
            }
        }

        // y cannot now be zero
        if (y == Double.POSITIVE_INFINITY) {
            if (x == Double.POSITIVE_INFINITY) {
                return java.lang.Math.PI * F_1_4
            }
            return if (x == Double.NEGATIVE_INFINITY) {
                java.lang.Math.PI * F_3_4
            } else java.lang.Math.PI * F_1_2
        }
        if (y == Double.NEGATIVE_INFINITY) {
            if (x == Double.POSITIVE_INFINITY) {
                return -java.lang.Math.PI * F_1_4
            }
            return if (x == Double.NEGATIVE_INFINITY) {
                -java.lang.Math.PI * F_3_4
            } else -java.lang.Math.PI * F_1_2
        }
        if (x == Double.POSITIVE_INFINITY) {
            if (y > 0 || 1 / y > 0) {
                return 0.0
            }
            if (y < 0 || 1 / y < 0) {
                return -0.0
            }
        }
        if (x == Double.NEGATIVE_INFINITY) {
            if (y > 0.0 || 1 / y > 0.0) {
                return java.lang.Math.PI
            }
            if (y < 0 || 1 / y < 0) {
                return -java.lang.Math.PI
            }
        }

        // Neither y nor x can be infinite or NAN here
        if (x == 0.0) {
            if (y > 0 || 1 / y > 0) {
                return java.lang.Math.PI * F_1_2
            }
            if (y < 0 || 1 / y < 0) {
                return -java.lang.Math.PI * F_1_2
            }
        }

        // Compute ratio r = y/x
        val r = y / x
        if (java.lang.Double.isInfinite(r)) { // bypass calculations that can create NaN
            return atan(r, 0.0, x < 0)
        }
        var ra = doubleHighPart(r)
        var rb = r - ra

        // Split x
        val xa = doubleHighPart(x)
        val xb = x - xa
        rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x
        val temp = ra + rb
        rb = -(temp - ra - rb)
        ra = temp
        if (ra == 0.0) { // Fix up the sign so atan works correctly
            ra = copySign(0.0, y)
        }

        // Call atan
        return atan(ra, rb, x < 0)
    }

    /** Compute the arc sine of a number.
     * @param x number on which evaluation is done
     * @return arc sine of x
     */
    fun asin(x: Double): Double {
        if (x != x) {
            return Double.NaN
        }
        if (x > 1.0 || x < -1.0) {
            return Double.NaN
        }
        if (x == 1.0) {
            return java.lang.Math.PI / 2.0
        }
        if (x == -1.0) {
            return -java.lang.Math.PI / 2.0
        }
        if (x == 0.0) { // Matches +/- 0.0; return correct sign
            return x
        }

        /* Compute asin(x) = atan(x/sqrt(1-x*x)) */

        /* Split x */
        var temp = x * HEX_40000000
        val xa = x + temp - temp
        val xb = x - xa

        /* Square it */
        var ya = xa * xa
        var yb = xa * xb * 2.0 + xb * xb

        /* Subtract from 1 */ya = -ya
        yb = -yb
        var za = 1.0 + ya
        var zb = -(za - 1.0 - ya)
        temp = za + yb
        zb += -(temp - za - yb)
        za = temp

        /* Square root */
        val y: Double
        y = sqrt(za)
        temp = y * HEX_40000000
        ya = y + temp - temp
        yb = y - ya

        /* Extend precision of sqrt */yb += (za - ya * ya - 2 * ya * yb - yb * yb) / (2.0 * y)

        /* Contribution of zb to sqrt */
        val dx = zb / (2.0 * y)

        // Compute ratio r = x/y
        val r = x / y
        temp = r * HEX_40000000
        var ra = r + temp - temp
        var rb = r - ra
        rb += (x - ra * ya - ra * yb - rb * ya - rb * yb) / y // Correct for rounding in division
        rb += -x * dx / y / y // Add in effect additional bits of sqrt.
        temp = ra + rb
        rb = -(temp - ra - rb)
        ra = temp
        return atan(ra, rb, false)
    }

    /** Compute the arc cosine of a number.
     * @param x number on which evaluation is done
     * @return arc cosine of x
     */
    fun acos(x: Double): Double {
        if (x != x) {
            return Double.NaN
        }
        if (x > 1.0 || x < -1.0) {
            return Double.NaN
        }
        if (x == -1.0) {
            return java.lang.Math.PI
        }
        if (x == 1.0) {
            return 0.0
        }
        if (x == 0.0) {
            return java.lang.Math.PI / 2.0
        }

        /* Compute acos(x) = atan(sqrt(1-x*x)/x) */

        /* Split x */
        var temp = x * HEX_40000000
        val xa = x + temp - temp
        val xb = x - xa

        /* Square it */
        var ya = xa * xa
        var yb = xa * xb * 2.0 + xb * xb

        /* Subtract from 1 */ya = -ya
        yb = -yb
        var za = 1.0 + ya
        var zb = -(za - 1.0 - ya)
        temp = za + yb
        zb += -(temp - za - yb)
        za = temp

        /* Square root */
        var y = sqrt(za)
        temp = y * HEX_40000000
        ya = y + temp - temp
        yb = y - ya

        /* Extend precision of sqrt */yb += (za - ya * ya - 2 * ya * yb - yb * yb) / (2.0 * y)

        /* Contribution of zb to sqrt */yb += zb / (2.0 * y)
        y = ya + yb
        yb = -(y - ya - yb)

        // Compute ratio r = y/x
        val r = y / x

        // Did r overflow?
        if (java.lang.Double.isInfinite(r)) { // x is effectively zero
            return java.lang.Math.PI / 2 // so return the appropriate value
        }
        var ra = doubleHighPart(r)
        var rb = r - ra
        rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x // Correct for rounding in division
        rb += yb / x // Add in effect additional bits of sqrt.
        temp = ra + rb
        rb = -(temp - ra - rb)
        ra = temp
        return atan(ra, rb, x < 0)
    }

    /** Compute the cubic root of a number.
     * @param x number on which evaluation is done
     * @return cubic root of x
     */
    fun cbrt(x: Double): Double {
        /* Convert input double to bits */
        var x = x
        var inbits: Long = java.lang.Double.doubleToRawLongBits(x)
        var exponent = (inbits shr 52 and 0x7ff).toInt() - 1023
        var subnormal = false
        if (exponent == -1023) {
            if (x == 0.0) {
                return x
            }

            /* Subnormal, so normalize */subnormal = true
            x *= 1.8014398509481984E16 // 2^54
            inbits = java.lang.Double.doubleToRawLongBits(x)
            exponent = (inbits shr 52 and 0x7ff).toInt() - 1023
        }
        if (exponent == 1024) {
            // Nan or infinity.  Don't care which.
            return x
        }

        /* Divide the exponent by 3 */
        val exp3 = exponent / 3

        /* p2 will be the nearest power of 2 to x with its exponent divided by 3 */
        val p2: Double = java.lang.Double.longBitsToDouble(
            inbits and (-0x8000000000000000L).toLong() or
                    (exp3 + 1023 and 0x7ff).toLong() shl 52
        )

        /* This will be a number between 1 and 2 */
        val mant: Double =
            java.lang.Double.longBitsToDouble(inbits and 0x000fffffffffffffL or 0x3ff0000000000000L)

        /* Estimate the cube root of mant by polynomial */
        var est = -0.010714690733195933
        est = est * mant + 0.0875862700108075
        est = est * mant + -0.3058015757857271
        est = est * mant + 0.7249995199969751
        est = est * mant + 0.5039018405998233
        est *= CBRTTWO[exponent % 3 + 2]

        // est should now be good to about 15 bits of precision.   Do 2 rounds of
        // Newton's method to get closer,  this should get us full double precision
        // Scale down x for the purpose of doing newtons method.  This avoids over/under flows.
        val xs = x / (p2 * p2 * p2)
        est += (xs - est * est * est) / (3 * est * est)
        est += (xs - est * est * est) / (3 * est * est)

        // Do one round of Newton's method in extended precision to get the last bit right.
        var temp = est * HEX_40000000
        val ya = est + temp - temp
        val yb = est - ya
        var za = ya * ya
        var zb = ya * yb * 2.0 + yb * yb
        temp = za * HEX_40000000
        val temp2 = za + temp - temp
        zb += za - temp2
        za = temp2
        zb = za * yb + ya * zb + zb * yb
        za *= ya
        val na = xs - za
        var nb = -(na - xs + za)
        nb -= zb
        est += (na + nb) / (3 * est * est)

        /* Scale by a power of two, so this is exact. */est *= p2
        if (subnormal) {
            est *= 3.814697265625E-6 // 2^-18
        }
        return est
    }

    /**
     * Convert degrees to radians, with error of less than 0.5 ULP
     * @param x angle in degrees
     * @return x converted into radians
     */
    fun toRadians(x: Double): Double {
        if (java.lang.Double.isInfinite(x) || x == 0.0) { // Matches +/- 0.0; return correct sign
            return x
        }

        // These are PI/180 split into high and low order bits
        val facta = 0.01745329052209854
        val factb = 1.997844754509471E-9
        val xa = doubleHighPart(x)
        val xb = x - xa
        var result = xb * factb + xb * facta + xa * factb + xa * facta
        if (result == 0.0) {
            result *= x // ensure correct sign if calculation underflows
        }
        return result
    }

    /**
     * Convert radians to degrees, with error of less than 0.5 ULP
     * @param x angle in radians
     * @return x converted into degrees
     */
    fun toDegrees(x: Double): Double {
        if (java.lang.Double.isInfinite(x) || x == 0.0) { // Matches +/- 0.0; return correct sign
            return x
        }

        // These are 180/PI split into high and low order bits
        val facta = 57.2957763671875
        val factb = 3.145894820876798E-6
        val xa = doubleHighPart(x)
        val xb = x - xa
        return xb * factb + xb * facta + xa * factb + xa * facta
    }

    /**
     * Absolute value.
     * @param x number from which absolute value is requested
     * @return abs(x)
     */
    fun abs(x: Int): Int {
        val i = x ushr 31
        return (x xor i.inv() + 1) + i
    }

    /**
     * Absolute value.
     * @param x number from which absolute value is requested
     * @return abs(x)
     */
    fun abs(x: Long): Long {
        val l = x ushr 63
        // l is one if x negative zero else
        // ~l+1 is zero if x is positive, -1 if x is negative
        // x^(~l+1) is x is x is positive, ~x if x is negative
        // add around
        return (x xor l.inv() + 1) + l
    }

    /**
     * Absolute value.
     * @param x number from which absolute value is requested
     * @return abs(x)
     */
    fun abs(x: Float): Float {
        return java.lang.Float.intBitsToFloat(MASK_NON_SIGN_INT and java.lang.Float.floatToRawIntBits(x))
    }

    /**
     * Absolute value.
     * @param x number from which absolute value is requested
     * @return abs(x)
     */
    fun abs(x: Double): Double {
        return java.lang.Double.longBitsToDouble(MASK_NON_SIGN_LONG and java.lang.Double.doubleToRawLongBits(x))
    }

    /**
     * Compute least significant bit (Unit in Last Position) for a number.
     * @param x number from which ulp is requested
     * @return ulp(x)
     */
    fun ulp(x: Double): Double {
        return if (java.lang.Double.isInfinite(x)) {
            Double.POSITIVE_INFINITY
        } else abs(x - java.lang.Double.longBitsToDouble(java.lang.Double.doubleToRawLongBits(x) xor 1))
    }

    /**
     * Compute least significant bit (Unit in Last Position) for a number.
     * @param x number from which ulp is requested
     * @return ulp(x)
     */
    fun ulp(x: Float): Float {
        return if (java.lang.Float.isInfinite(x)) {
            Float.POSITIVE_INFINITY
        } else abs(x - java.lang.Float.intBitsToFloat(java.lang.Float.floatToIntBits(x) xor 1))
    }

    /**
     * Multiply a double number by a power of 2.
     * @param d number to multiply
     * @param n power of 2
     * @return d  2<sup>n</sup>
     */
    fun scalb(d: Double, n: Int): Double {

        // first simple and fast handling when 2^n can be represented using normal numbers
        if (n > -1023 && n < 1024) {
            return d * java.lang.Double.longBitsToDouble((n + 1023).toLong() shl 52)
        }

        // handle special cases
        if (java.lang.Double.isNaN(d) || java.lang.Double.isInfinite(d) || d == 0.0) {
            return d
        }
        if (n < -2098) {
            return if (d > 0) 0.0 else -0.0
        }
        if (n > 2097) {
            return if (d > 0) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
        }

        // decompose d
        val bits: Long = java.lang.Double.doubleToRawLongBits(d)
        val sign = bits and (-0x8000000000000000L).toLong()
        val exponent = (bits ushr 52).toInt() and 0x7ff
        var mantissa = bits and 0x000fffffffffffffL

        // compute scaled exponent
        var scaledExponent = exponent + n
        return if (n < 0) {
            // we are really in the case n <= -1023
            if (scaledExponent > 0) {
                // both the input and the result are normal numbers, we only adjust the exponent
                java.lang.Double.longBitsToDouble(sign or (scaledExponent.toLong() shl 52) or mantissa)
            } else if (scaledExponent > -53) {
                // the input is a normal number and the result is a subnormal number

                // recover the hidden mantissa bit
                mantissa = mantissa or (1L shl 52)

                // scales down complete mantissa, hence losing least significant bits
                val mostSignificantLostBit = mantissa and (1L shl -scaledExponent)
                mantissa = mantissa ushr 1 - scaledExponent
                if (mostSignificantLostBit != 0L) {
                    // we need to add 1 bit to round up the result
                    mantissa++
                }
                java.lang.Double.longBitsToDouble(sign or mantissa)
            } else {
                // no need to compute the mantissa, the number scales down to 0
                if (sign == 0L) 0.0 else -0.0
            }
        } else {
            // we are really in the case n >= 1024
            if (exponent == 0) {

                // the input number is subnormal, normalize it
                while (mantissa ushr 52 != 1L) {
                    mantissa = mantissa shl 1
                    --scaledExponent
                }
                ++scaledExponent
                mantissa = mantissa and 0x000fffffffffffffL
                if (scaledExponent < 2047) {
                    java.lang.Double.longBitsToDouble(sign or (scaledExponent.toLong() shl 52) or mantissa)
                } else {
                    if (sign == 0L) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
                }
            } else if (scaledExponent < 2047) {
                java.lang.Double.longBitsToDouble(sign or (scaledExponent.toLong() shl 52) or mantissa)
            } else {
                if (sign == 0L) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
            }
        }
    }

    /**
     * Multiply a float number by a power of 2.
     * @param f number to multiply
     * @param n power of 2
     * @return f  2<sup>n</sup>
     */
    fun scalb(f: Float, n: Int): Float {

        // first simple and fast handling when 2^n can be represented using normal numbers
        if (n > -127 && n < 128) {
            return f * java.lang.Float.intBitsToFloat(n + 127 shl 23)
        }

        // handle special cases
        if (java.lang.Float.isNaN(f) || java.lang.Float.isInfinite(f) || f == 0f) {
            return f
        }
        if (n < -277) {
            return if (f > 0) 0.0f else -0.0f
        }
        if (n > 276) {
            return if (f > 0) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY
        }

        // decompose f
        val bits: Int = java.lang.Float.floatToIntBits(f)
        val sign = bits and -0x80000000
        val exponent = bits ushr 23 and 0xff
        var mantissa = bits and 0x007fffff

        // compute scaled exponent
        var scaledExponent = exponent + n
        return if (n < 0) {
            // we are really in the case n <= -127
            if (scaledExponent > 0) {
                // both the input and the result are normal numbers, we only adjust the exponent
                java.lang.Float.intBitsToFloat(sign or (scaledExponent shl 23) or mantissa)
            } else if (scaledExponent > -24) {
                // the input is a normal number and the result is a subnormal number

                // recover the hidden mantissa bit
                mantissa = mantissa or (1 shl 23)

                // scales down complete mantissa, hence losing least significant bits
                val mostSignificantLostBit = mantissa and (1 shl -scaledExponent)
                mantissa = mantissa ushr 1 - scaledExponent
                if (mostSignificantLostBit != 0) {
                    // we need to add 1 bit to round up the result
                    mantissa++
                }
                java.lang.Float.intBitsToFloat(sign or mantissa)
            } else {
                // no need to compute the mantissa, the number scales down to 0
                if (sign == 0) 0.0f else -0.0f
            }
        } else {
            // we are really in the case n >= 128
            if (exponent == 0) {

                // the input number is subnormal, normalize it
                while (mantissa ushr 23 != 1) {
                    mantissa = mantissa shl 1
                    --scaledExponent
                }
                ++scaledExponent
                mantissa = mantissa and 0x007fffff
                if (scaledExponent < 255) {
                    java.lang.Float.intBitsToFloat(sign or (scaledExponent shl 23) or mantissa)
                } else {
                    if (sign == 0) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY
                }
            } else if (scaledExponent < 255) {
                java.lang.Float.intBitsToFloat(sign or (scaledExponent shl 23) or mantissa)
            } else {
                if (sign == 0) Float.POSITIVE_INFINITY else Float.NEGATIVE_INFINITY
            }
        }
    }

    /**
     * Get the next machine representable number after a number, moving
     * in the direction of another number.
     *
     *
     * The ordering is as follows (increasing):
     *
     *  * -INFINITY
     *  * -MAX_VALUE
     *  * -MIN_VALUE
     *  * -0.0
     *  * +0.0
     *  * +MIN_VALUE
     *  * +MAX_VALUE
     *  * +INFINITY
     *  *
     *
     *
     * If arguments compare equal, then the second argument is returned.
     *
     *
     * If `direction` is greater than `d`,
     * the smallest machine representable number strictly greater than
     * `d` is returned; if less, then the largest representable number
     * strictly less than `d` is returned.
     *
     *
     * If `d` is infinite and direction does not
     * bring it back to finite numbers, it is returned unchanged.
     *
     * @param d base number
     * @param direction (the only important thing is whether
     * `direction` is greater or smaller than `d`)
     * @return the next machine representable number in the specified direction
     */
    fun nextAfter(d: Double, direction: Double): Double {

        // handling of some important special cases
        if (java.lang.Double.isNaN(d) || java.lang.Double.isNaN(direction)) {
            return Double.NaN
        } else if (d == direction) {
            return direction
        } else if (java.lang.Double.isInfinite(d)) {
            return if (d < 0) -Double.MAX_VALUE else Double.MAX_VALUE
        } else if (d == 0.0) {
            return if (direction < 0) -Double.MIN_VALUE else Double.MIN_VALUE
        }
        // special cases MAX_VALUE to infinity and  MIN_VALUE to 0
        // are handled just as normal numbers
        // can use raw bits since already dealt with infinity and NaN
        val bits: Long = java.lang.Double.doubleToRawLongBits(d)
        val sign = bits and (-0x8000000000000000L).toLong()
        return if ((direction < d) xor (sign == 0L)) {
            java.lang.Double.longBitsToDouble(sign or (bits and 0x7fffffffffffffffL) + 1)
        } else {
            java.lang.Double.longBitsToDouble(sign or (bits and 0x7fffffffffffffffL) - 1)
        }
    }

    /**
     * Get the next machine representable number after a number, moving
     * in the direction of another number.
     *
     *
     * The ordering is as follows (increasing):
     *
     *  * -INFINITY
     *  * -MAX_VALUE
     *  * -MIN_VALUE
     *  * -0.0
     *  * +0.0
     *  * +MIN_VALUE
     *  * +MAX_VALUE
     *  * +INFINITY
     *  *
     *
     *
     * If arguments compare equal, then the second argument is returned.
     *
     *
     * If `direction` is greater than `f`,
     * the smallest machine representable number strictly greater than
     * `f` is returned; if less, then the largest representable number
     * strictly less than `f` is returned.
     *
     *
     * If `f` is infinite and direction does not
     * bring it back to finite numbers, it is returned unchanged.
     *
     * @param f base number
     * @param direction (the only important thing is whether
     * `direction` is greater or smaller than `f`)
     * @return the next machine representable number in the specified direction
     */
    fun nextAfter(f: Float, direction: Double): Float {

        // handling of some important special cases
        if (java.lang.Double.isNaN(f.toDouble()) || java.lang.Double.isNaN(direction)) {
            return Float.NaN
        } else if (f.toDouble() == direction) {
            return direction.toFloat()
        } else if (java.lang.Float.isInfinite(f)) {
            return if (f < 0f) -Float.MAX_VALUE else Float.MAX_VALUE
        } else if (f == 0f) {
            return if (direction < 0) -Float.MIN_VALUE else Float.MIN_VALUE
        }
        // special cases MAX_VALUE to infinity and  MIN_VALUE to 0
        // are handled just as normal numbers
        val bits: Int = java.lang.Float.floatToIntBits(f)
        val sign = bits and -0x80000000
        return if ((direction < f) xor (sign == 0)) {
            java.lang.Float.intBitsToFloat(sign or (bits and 0x7fffffff) + 1)
        } else {
            java.lang.Float.intBitsToFloat(sign or (bits and 0x7fffffff) - 1)
        }
    }

    /** Get the largest whole number smaller than x.
     * @param x number from which floor is requested
     * @return a double number f such that f is an integer f <= x < f + 1.0
     */
    fun floor(x: Double): Double {
        var y: Long
        if (x != x) { // NaN
            return x
        }
        if (x >= TWO_POWER_52 || x <= -TWO_POWER_52) {
            return x
        }
        y = x.toLong()
        if (x < 0 && y.toDouble() != x) {
            y--
        }
        return if (y == 0L) {
            x * y
        } else y.toDouble()
    }

    /** Get the smallest whole number larger than x.
     * @param x number from which ceil is requested
     * @return a double number c such that c is an integer c - 1.0 < x <= c
     */
    fun ceil(x: Double): Double {
        var y: Double
        if (x != x) { // NaN
            return x
        }
        y = floor(x)
        if (y == x) {
            return y
        }
        y += 1.0
        return if (y == 0.0) {
            x * y
        } else y
    }

    /** Get the whole number that is the nearest to x, or the even one if x is exactly half way between two integers.
     * @param x number from which nearest whole number is requested
     * @return a double number r such that r is an integer r - 0.5 <= x <= r + 0.5
     */
    fun rint(x: Double): Double {
        val y = floor(x)
        val d = x - y
        if (d > 0.5) {
            return if (y == -1.0) {
                -0.0 // Preserve sign of operand
            } else y + 1.0
        }
        if (d < 0.5) {
            return y
        }

        /* half way, round to even */
        val z = y.toLong()
        return if (z and 1 == 0L) y else y + 1.0
    }

    /** Get the closest long to x.
     * @param x number from which closest long is requested
     * @return closest long to x
     */
    fun round(x: Double): Long {
        return floor(x + 0.5).toLong()
    }

    /** Get the closest int to x.
     * @param x number from which closest int is requested
     * @return closest int to x
     */
    fun round(x: Float): Int {
        return floor(x + 0.5f.toDouble()).toInt()
    }

    /** Compute the minimum of two values
     * @param a first value
     * @param b second value
     * @return a if a is lesser or equal to b, b otherwise
     */
    fun min(a: Int, b: Int): Int {
        return if (a <= b) a else b
    }

    /** Compute the minimum of two values
     * @param a first value
     * @param b second value
     * @return a if a is lesser or equal to b, b otherwise
     */
    fun min(a: Long, b: Long): Long {
        return if (a <= b) a else b
    }

    /** Compute the minimum of two values
     * @param a first value
     * @param b second value
     * @return a if a is lesser or equal to b, b otherwise
     */
    fun min(a: Float, b: Float): Float {
        if (a > b) {
            return b
        }
        if (a < b) {
            return a
        }
        /* if either arg is NaN, return NaN */if (a != b) {
            return Float.NaN
        }
        /* min(+0.0,-0.0) == -0.0 */
        /* 0x80000000 == Float.floatToRawIntBits(-0.0d) */
        val bits: Int = java.lang.Float.floatToRawIntBits(a)
        return if (bits == -0x80000000) {
            a
        } else b
    }

    /** Compute the minimum of two values
     * @param a first value
     * @param b second value
     * @return a if a is lesser or equal to b, b otherwise
     */
    fun min(a: Double, b: Double): Double {
        if (a > b) {
            return b
        }
        if (a < b) {
            return a
        }
        /* if either arg is NaN, return NaN */if (a != b) {
            return Double.NaN
        }
        /* min(+0.0,-0.0) == -0.0 */
        /* 0x8000000000000000L == Double.doubleToRawLongBits(-0.0d) */
        val bits: Long = java.lang.Double.doubleToRawLongBits(a)
        return if (bits == -0x8000000000000000L) {
            a
        } else b
    }

    /** Compute the maximum of two values
     * @param a first value
     * @param b second value
     * @return b if a is lesser or equal to b, a otherwise
     */
    fun max(a: Int, b: Int): Int {
        return if (a <= b) b else a
    }

    /** Compute the maximum of two values
     * @param a first value
     * @param b second value
     * @return b if a is lesser or equal to b, a otherwise
     */
    fun max(a: Long, b: Long): Long {
        return if (a <= b) b else a
    }

    /** Compute the maximum of two values
     * @param a first value
     * @param b second value
     * @return b if a is lesser or equal to b, a otherwise
     */
    fun max(a: Float, b: Float): Float {
        if (a > b) {
            return a
        }
        if (a < b) {
            return b
        }
        /* if either arg is NaN, return NaN */if (a != b) {
            return Float.NaN
        }
        /* min(+0.0,-0.0) == -0.0 */
        /* 0x80000000 == Float.floatToRawIntBits(-0.0d) */
        val bits: Int = java.lang.Float.floatToRawIntBits(a)
        return if (bits == -0x80000000) {
            b
        } else a
    }

    /** Compute the maximum of two values
     * @param a first value
     * @param b second value
     * @return b if a is lesser or equal to b, a otherwise
     */
    fun max(a: Double, b: Double): Double {
        if (a > b) {
            return a
        }
        if (a < b) {
            return b
        }
        /* if either arg is NaN, return NaN */if (a != b) {
            return Double.NaN
        }
        /* min(+0.0,-0.0) == -0.0 */
        /* 0x8000000000000000L == Double.doubleToRawLongBits(-0.0d) */
        val bits: Long = java.lang.Double.doubleToRawLongBits(a)
        return if (bits == -0x8000000000000000L) {
            b
        } else a
    }

    /**
     * Returns the hypotenuse of a triangle with sides `x` and `y`
     * - sqrt(*x*<sup>2</sup>&nbsp;+*y*<sup>2</sup>)<br></br>
     * avoiding intermediate overflow or underflow.
     *
     *
     *  *  If either argument is infinite, then the result is positive infinity.
     *  *  else, if either argument is NaN then the result is NaN.
     *
     *
     * @param x a value
     * @param y a value
     * @return sqrt(*x*<sup>2</sup>&nbsp;+*y*<sup>2</sup>)
     */
    fun hypot(x: Double, y: Double): Double {
        return if (java.lang.Double.isInfinite(x) || java.lang.Double.isInfinite(y)) {
            Double.POSITIVE_INFINITY
        } else if (java.lang.Double.isNaN(x) || java.lang.Double.isNaN(y)) {
            Double.NaN
        } else {
            val expX = getExponent(x)
            val expY = getExponent(y)
            if (expX > expY + 27) {
                // y is neglectible with respect to x
                abs(x)
            } else if (expY > expX + 27) {
                // x is neglectible with respect to y
                abs(y)
            } else {

                // find an intermediate scale to avoid both overflow and underflow
                val middleExp = (expX + expY) / 2

                // scale parameters without losing precision
                val scaledX = scalb(x, -middleExp)
                val scaledY = scalb(y, -middleExp)

                // compute scaled hypotenuse
                val scaledH = sqrt(scaledX * scaledX + scaledY * scaledY)

                // remove scaling
                scalb(scaledH, middleExp)
            }
        }
    }

    /**
     * Computes the remainder as prescribed by the IEEE 754 standard.
     * The remainder value is mathematically equal to `x - y*n`
     * where `n` is the mathematical integer closest to the exact mathematical value
     * of the quotient `x/y`.
     * If two mathematical integers are equally close to `x/y` then
     * `n` is the integer that is even.
     *
     *
     *
     *  * If either operand is NaN, the result is NaN.
     *  * If the result is not NaN, the sign of the result equals the sign of the dividend.
     *  * If the dividend is an infinity, or the divisor is a zero, or both, the result is NaN.
     *  * If the dividend is finite and the divisor is an infinity, the result equals the dividend.
     *  * If the dividend is a zero and the divisor is finite, the result equals the dividend.
     *
     *
     * **Note:** this implementation currently delegates to [StrictMath.IEEEremainder]
     * @param dividend the number to be divided
     * @param divisor the number by which to divide
     * @return the remainder, rounded
     */
    fun IEEEremainder(dividend: Double, divisor: Double): Double {
        return java.lang.StrictMath.IEEEremainder(dividend, divisor) // TODO provide our own implementation
    }

    /** Convert a long to interger, detecting overflows
     * @param n number to convert to int
     * @return integer with same valie as n if no overflows occur
     * @exception MathArithmeticException if n cannot fit into an int
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun toIntExact(n: Long): Int {
        if (n < Int.MIN_VALUE || n > Int.MAX_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW)
        }
        return n.toInt()
    }

    /** Increment a number, detecting overflows.
     * @param n number to increment
     * @return n+1 if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun incrementExact(n: Int): Int {
        if (n == Int.MAX_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, n, 1)
        }
        return n + 1
    }

    /** Increment a number, detecting overflows.
     * @param n number to increment
     * @return n+1 if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun incrementExact(n: Long): Long {
        if (n == Long.MAX_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, n, 1)
        }
        return n + 1
    }

    /** Decrement a number, detecting overflows.
     * @param n number to decrement
     * @return n-1 if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun decrementExact(n: Int): Int {
        if (n == Int.MIN_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, n, 1)
        }
        return n - 1
    }

    /** Decrement a number, detecting overflows.
     * @param n number to decrement
     * @return n-1 if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun decrementExact(n: Long): Long {
        if (n == Long.MIN_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, n, 1)
        }
        return n - 1
    }

    /** Add two numbers, detecting overflows.
     * @param a first number to add
     * @param b second number to add
     * @return a+b if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun addExact(a: Int, b: Int): Int {

        // compute sum
        val sum = a + b

        // check for overflow
        if (a xor b >= 0 && sum xor b < 0) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, a, b)
        }
        return sum
    }

    /** Add two numbers, detecting overflows.
     * @param a first number to add
     * @param b second number to add
     * @return a+b if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun addExact(a: Long, b: Long): Long {

        // compute sum
        val sum = a + b

        // check for overflow
        if (a xor b >= 0 && sum xor b < 0) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, a, b)
        }
        return sum
    }

    /** Subtract two numbers, detecting overflows.
     * @param a first number
     * @param b second number to subtract from a
     * @return a-b if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    fun subtractExact(a: Int, b: Int): Int {

        // compute subtraction
        val sub = a - b

        // check for overflow
        if (a xor b < 0 && sub xor b >= 0) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, a, b)
        }
        return sub
    }

    /** Subtract two numbers, detecting overflows.
     * @param a first number
     * @param b second number to subtract from a
     * @return a-b if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    fun subtractExact(a: Long, b: Long): Long {

        // compute subtraction
        val sub = a - b

        // check for overflow
        if (a xor b < 0 && sub xor b >= 0) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, a, b)
        }
        return sub
    }

    /** Multiply two numbers, detecting overflows.
     * @param a first number to multiply
     * @param b second number to multiply
     * @return a*b if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    fun multiplyExact(a: Int, b: Int): Int {
        if (b > 0 && (a > Int.MAX_VALUE / b || a < Int.MIN_VALUE / b) ||
            b < -1 && (a > Int.MIN_VALUE / b || a < Int.MAX_VALUE / b) ||
            b == -1 && a == Int.MIN_VALUE
        ) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_MULTIPLICATION, a, b)
        }
        return a * b
    }

    /** Multiply two numbers, detecting overflows.
     * @param a first number to multiply
     * @param b second number to multiply
     * @return a*b if no overflows occur
     * @exception MathArithmeticException if an overflow occurs
     * @since 3.4
     */
    fun multiplyExact(a: Long, b: Long): Long {
        if (b > 0L && (a > Long.MAX_VALUE / b || a < Long.MIN_VALUE / b) ||
            b < -1L && (a > Long.MIN_VALUE / b || a < Long.MAX_VALUE / b) ||
            b == -1L && a == Long.MIN_VALUE
        ) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_MULTIPLICATION, a, b)
        }
        return a * b
    }

    /** Finds q such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0.
     *
     *
     * This methods returns the same value as integer division when
     * a and b are same signs, but returns a different value when
     * they are opposite (i.e. q is negative).
     *
     * @param a dividend
     * @param b divisor
     * @return q such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0
     * @exception MathArithmeticException if b == 0
     * @see .floorMod
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun floorDiv(a: Int, b: Int): Int {
        if (b == 0) {
            throw MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR)
        }
        val m = a % b
        return if (a xor b >= 0 || m == 0) {
            // a an b have same sign, or division is exact
            a / b
        } else {
            // a and b have opposite signs and division is not exact
            a / b - 1
        }
    }

    /** Finds q such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0.
     *
     *
     * This methods returns the same value as integer division when
     * a and b are same signs, but returns a different value when
     * they are opposite (i.e. q is negative).
     *
     * @param a dividend
     * @param b divisor
     * @return q such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0
     * @exception MathArithmeticException if b == 0
     * @see .floorMod
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun floorDiv(a: Long, b: Long): Long {
        if (b == 0L) {
            throw MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR)
        }
        val m = a % b
        return if (a xor b >= 0L || m == 0L) {
            // a an b have same sign, or division is exact
            a / b
        } else {
            // a and b have opposite signs and division is not exact
            a / b - 1L
        }
    }

    /** Finds r such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0.
     *
     *
     * This methods returns the same value as integer modulo when
     * a and b are same signs, but returns a different value when
     * they are opposite (i.e. q is negative).
     *
     * @param a dividend
     * @param b divisor
     * @return r such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0
     * @exception MathArithmeticException if b == 0
     * @see .floorDiv
     * @since 3.4
     */
    @Throws(MathArithmeticException::class)
    fun floorMod(a: Int, b: Int): Int {
        if (b == 0) {
            throw MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR)
        }
        val m = a % b
        return if (a xor b >= 0 || m == 0) {
            // a an b have same sign, or division is exact
            m
        } else {
            // a and b have opposite signs and division is not exact
            b + m
        }
    }

    /** Finds r such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0.
     *
     *
     * This methods returns the same value as integer modulo when
     * a and b are same signs, but returns a different value when
     * they are opposite (i.e. q is negative).
     *
     * @param a dividend
     * @param b divisor
     * @return r such that a = q b + r with 0 <= r < b if b > 0 and b < r <= 0 if b < 0
     * @exception MathArithmeticException if b == 0
     * @see .floorDiv
     * @since 3.4
     */
    fun floorMod(a: Long, b: Long): Long {
        if (b == 0L) {
            throw MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR)
        }
        val m = a % b
        return if (a xor b >= 0L || m == 0L) {
            // a an b have same sign, or division is exact
            m
        } else {
            // a and b have opposite signs and division is not exact
            b + m
        }
    }

    /**
     * Returns the first argument with the sign of the second argument.
     * A NaN `sign` argument is treated as positive.
     *
     * @param magnitude the value to return
     * @param sign the sign for the returned value
     * @return the magnitude with the same sign as the `sign` argument
     */
    fun copySign(magnitude: Double, sign: Double): Double {
        // The highest order bit is going to be zero if the
        // highest order bit of m and s is the same and one otherwise.
        // So (m^s) will be positive if both m and s have the same sign
        // and negative otherwise.
        val m: Long = java.lang.Double.doubleToRawLongBits(magnitude) // don't care about NaN
        val s: Long = java.lang.Double.doubleToRawLongBits(sign)
        return if (m xor s >= 0) {
            magnitude
        } else -magnitude
        // flip sign
    }

    /**
     * Returns the first argument with the sign of the second argument.
     * A NaN `sign` argument is treated as positive.
     *
     * @param magnitude the value to return
     * @param sign the sign for the returned value
     * @return the magnitude with the same sign as the `sign` argument
     */
    fun copySign(magnitude: Float, sign: Float): Float {
        // The highest order bit is going to be zero if the
        // highest order bit of m and s is the same and one otherwise.
        // So (m^s) will be positive if both m and s have the same sign
        // and negative otherwise.
        val m: Int = java.lang.Float.floatToRawIntBits(magnitude)
        val s: Int = java.lang.Float.floatToRawIntBits(sign)
        return if (m xor s >= 0) {
            magnitude
        } else -magnitude
        // flip sign
    }

    /**
     * Return the exponent of a double number, removing the bias.
     *
     *
     * For double numbers of the form 2<sup>x</sup>, the unbiased
     * exponent is exactly x.
     *
     * @param d number from which exponent is requested
     * @return exponent for d in IEEE754 representation, without bias
     */
    fun getExponent(d: Double): Int {
        // NaN and Infinite will return 1024 anywho so can use raw bits
        return (java.lang.Double.doubleToRawLongBits(d) ushr 52 and 0x7ff) as Int - 1023
    }

    /**
     * Return the exponent of a float number, removing the bias.
     *
     *
     * For float numbers of the form 2<sup>x</sup>, the unbiased
     * exponent is exactly x.
     *
     * @param f number from which exponent is requested
     * @return exponent for d in IEEE754 representation, without bias
     */
    fun getExponent(f: Float): Int {
        // NaN and Infinite will return the same exponent anywho so can use raw bits
        return (java.lang.Float.floatToRawIntBits(f) ushr 23 and 0xff) - 127
    }

    /**
     * Print out contents of arrays, and check the length.
     *
     * used to generate the preset arrays originally.
     * @param a unused
     */
    @JvmStatic
    fun main(a: Array<String>) {
        val out: PrintStream = java.lang.System.out
        FastMathCalc.printarray(out, "EXP_INT_TABLE_A", EXP_INT_TABLE_LEN, ExpIntTable.EXP_INT_TABLE_A)
        FastMathCalc.printarray(out, "EXP_INT_TABLE_B", EXP_INT_TABLE_LEN, ExpIntTable.EXP_INT_TABLE_B)
        FastMathCalc.printarray(out, "EXP_FRAC_TABLE_A", EXP_FRAC_TABLE_LEN, ExpFracTable.EXP_FRAC_TABLE_A)
        FastMathCalc.printarray(out, "EXP_FRAC_TABLE_B", EXP_FRAC_TABLE_LEN, ExpFracTable.EXP_FRAC_TABLE_B)
        FastMathCalc.printarray(out, "LN_MANT", LN_MANT_LEN, lnMant.LN_MANT)
        FastMathCalc.printarray(out, "SINE_TABLE_A", SINE_TABLE_LEN, SINE_TABLE_A)
        FastMathCalc.printarray(out, "SINE_TABLE_B", SINE_TABLE_LEN, SINE_TABLE_B)
        FastMathCalc.printarray(out, "COSINE_TABLE_A", SINE_TABLE_LEN, COSINE_TABLE_A)
        FastMathCalc.printarray(out, "COSINE_TABLE_B", SINE_TABLE_LEN, COSINE_TABLE_B)
        FastMathCalc.printarray(out, "TANGENT_TABLE_A", SINE_TABLE_LEN, TANGENT_TABLE_A)
        FastMathCalc.printarray(out, "TANGENT_TABLE_B", SINE_TABLE_LEN, TANGENT_TABLE_B)
    }

    /** Class operator on double numbers split into one 26 bits number and one 27 bits number.  */
    private class Split {
        /** Full number.  */
        val full: Double

        /** High order bits.  */
        private val high: Double

        /** Low order bits.  */
        private val low: Double

        /** Simple constructor.
         * @param x number to split
         */
        internal constructor(x: Double) {
            full = x
            high = java.lang.Double.longBitsToDouble(java.lang.Double.doubleToRawLongBits(x) and (-1L shl 27))
            low = x - high
        }

        /** Simple constructor.
         * @param high high order bits
         * @param low low order bits
         */
        internal constructor(
            high: Double,
            low: Double
        ) : this(
            if (high == 0.0) if (low == 0.0 && java.lang.Double.doubleToRawLongBits(high) == Long.MIN_VALUE /* negative zero */) -0.0 else low else high + low,
            high,
            low
        ) {
        }

        /** Simple constructor.
         * @param full full number
         * @param high high order bits
         * @param low low order bits
         */
        internal constructor(full: Double, high: Double, low: Double) {
            this.full = full
            this.high = high
            this.low = low
        }

        /** Multiply the instance by another one.
         * @param b other instance to multiply by
         * @return product
         */
        fun multiply(b: Split): Split {
            // beware the following expressions must NOT be simplified, they rely on floating point arithmetic properties
            val mulBasic = Split(full * b.full)
            val mulError =
                low * b.low - (mulBasic.full - high * b.high - low * b.high - high * b.low)
            return Split(mulBasic.high, mulBasic.low + mulError)
        }

        /** Compute the reciprocal of the instance.
         * @return reciprocal of the instance
         */
        fun reciprocal(): Split {
            val approximateInv = 1.0 / full
            val splitInv = Split(approximateInv)

            // if 1.0/d were computed perfectly, remultiplying it by d should give 1.0
            // we want to estimate the error so we can fix the low order bits of approximateInvLow
            // beware the following expressions must NOT be simplified, they rely on floating point arithmetic properties
            val product = multiply(splitInv)
            val error = product.high - 1 + product.low

            // better accuracy estimate of reciprocal
            return if (java.lang.Double.isNaN(error)) splitInv else Split(splitInv.high, splitInv.low - error / full)
        }

        /** Computes this^e.
         * @param e exponent (beware, here it MUST be > 0; the only exclusion is Long.MIN_VALUE)
         * @return d^e, split in high and low bits
         * @since 3.6
         */
        fun pow(e: Long): Split {

            // prepare result
            var result = Split(1)

            // d^(2p)
            var d2p = Split(full, high, low)
            var p = e
            while (p != 0L) {
                if (p and 0x1 != 0L) {
                    // accurate multiplication result = result * d^(2p) using Veltkamp TwoProduct algorithm
                    result = result.multiply(d2p)
                }

                // accurate squaring d^(2(p+1)) = d^(2p) * d^(2p) using Veltkamp TwoProduct algorithm
                d2p = d2p.multiply(d2p)
                p = p ushr 1
            }
            return if (java.lang.Double.isNaN(result.full)) {
                if (java.lang.Double.isNaN(full)) {
                    NAN
                } else {
                    // some intermediate numbers exceeded capacity,
                    // and the low order bits became NaN (because infinity - infinity = NaN)
                    if (abs(full) < 1) {
                        Split(copySign(0.0, full), 0.0)
                    } else if (full < 0 && e and 0x1 == 1L) {
                        NEGATIVE_INFINITY
                    } else {
                        POSITIVE_INFINITY
                    }
                }
            } else {
                result
            }
        }

        companion object {
            /** Split version of NaN.  */
            val NAN = Split(Double.NaN, 0)

            /** Split version of positive infinity.  */
            val POSITIVE_INFINITY = Split(Double.POSITIVE_INFINITY, 0)

            /** Split version of negative infinity.  */
            val NEGATIVE_INFINITY = Split(Double.NEGATIVE_INFINITY, 0)
        }
    }

    /** Enclose large data table in nested static class so it's only loaded on first access.  */
    private object ExpIntTable {
        /** Exponential evaluated at integer values,
         * exp(x) =  expIntTableA[x + EXP_INT_TABLE_MAX_INDEX] + expIntTableB[x+EXP_INT_TABLE_MAX_INDEX].
         */
        val EXP_INT_TABLE_A: DoubleArray

        /** Exponential evaluated at integer values,
         * exp(x) =  expIntTableA[x + EXP_INT_TABLE_MAX_INDEX] + expIntTableB[x+EXP_INT_TABLE_MAX_INDEX]
         */
        val EXP_INT_TABLE_B: DoubleArray

        init {
            if (RECOMPUTE_TABLES_AT_RUNTIME) {
                EXP_INT_TABLE_A = DoubleArray(EXP_INT_TABLE_LEN)
                EXP_INT_TABLE_B = DoubleArray(EXP_INT_TABLE_LEN)
                val tmp = DoubleArray(2)
                val recip = DoubleArray(2)

                // Populate expIntTable
                for (i in 0 until EXP_INT_TABLE_MAX_INDEX) {
                    FastMathCalc.expint(i, tmp)
                    EXP_INT_TABLE_A[i + EXP_INT_TABLE_MAX_INDEX] = tmp[0]
                    EXP_INT_TABLE_B[i + EXP_INT_TABLE_MAX_INDEX] = tmp[1]
                    if (i != 0) {
                        // Negative integer powers
                        FastMathCalc.splitReciprocal(tmp, recip)
                        EXP_INT_TABLE_A[EXP_INT_TABLE_MAX_INDEX - i] = recip[0]
                        EXP_INT_TABLE_B[EXP_INT_TABLE_MAX_INDEX - i] = recip[1]
                    }
                }
            } else {
                EXP_INT_TABLE_A = FastMathLiteralArrays.loadExpIntA()
                EXP_INT_TABLE_B = FastMathLiteralArrays.loadExpIntB()
            }
        }
    }

    /** Enclose large data table in nested static class so it's only loaded on first access.  */
    private object ExpFracTable {
        /** Exponential over the range of 0 - 1 in increments of 2^-10
         * exp(x/1024) =  expFracTableA[x] + expFracTableB[x].
         * 1024 = 2^10
         */
        val EXP_FRAC_TABLE_A: DoubleArray

        /** Exponential over the range of 0 - 1 in increments of 2^-10
         * exp(x/1024) =  expFracTableA[x] + expFracTableB[x].
         */
        val EXP_FRAC_TABLE_B: DoubleArray

        init {
            if (RECOMPUTE_TABLES_AT_RUNTIME) {
                EXP_FRAC_TABLE_A = DoubleArray(EXP_FRAC_TABLE_LEN)
                EXP_FRAC_TABLE_B = DoubleArray(EXP_FRAC_TABLE_LEN)
                val tmp = DoubleArray(2)

                // Populate expFracTable
                val factor = 1.0 / (EXP_FRAC_TABLE_LEN - 1)
                for (i in EXP_FRAC_TABLE_A.indices) {
                    FastMathCalc.slowexp(i * factor, tmp)
                    EXP_FRAC_TABLE_A[i] = tmp[0]
                    EXP_FRAC_TABLE_B[i] = tmp[1]
                }
            } else {
                EXP_FRAC_TABLE_A = FastMathLiteralArrays.loadExpFracA()
                EXP_FRAC_TABLE_B = FastMathLiteralArrays.loadExpFracB()
            }
        }
    }

    /** Enclose large data table in nested static class so it's only loaded on first access.  */
    private object lnMant {
        /** Extended precision logarithm table over the range 1 - 2 in increments of 2^-10.  */
        val LN_MANT: Array<DoubleArray?>

        init {
            if (RECOMPUTE_TABLES_AT_RUNTIME) {
                LN_MANT = arrayOfNulls(LN_MANT_LEN)

                // Populate lnMant table
                for (i in LN_MANT.indices) {
                    val d: Double =
                        java.lang.Double.longBitsToDouble(i.toLong() shl 42 or 0x3ff0000000000000L)
                    LN_MANT[i] = FastMathCalc.slowLog(d)
                }
            } else {
                LN_MANT = FastMathLiteralArrays.loadLnMant()
            }
        }
    }

    /** Enclose the Cody/Waite reduction (used in "sin", "cos" and "tan").  */
    private class CodyWaite internal constructor(xa: Double) {
        /**
         * @return k
         */
        /** k  */
        val k: Int

        /**
         * @return remA
         */
        /** remA  */
        val remA: Double

        /**
         * @return remB
         */
        /** remB  */
        val remB: Double

        /**
         * @param xa Argument.
         */
        init {
            // Estimate k.
            //k = (int)(xa / 1.5707963267948966);
            var k = (xa * 0.6366197723675814).toInt()

            // Compute remainder.
            var remA: Double
            var remB: Double
            while (true) {
                var a = -k * 1.570796251296997
                remA = xa + a
                remB = -(remA - xa - a)
                a = -k * 7.549789948768648E-8
                var b = remA
                remA = a + b
                remB += -(remA - b - a)
                a = -k * 6.123233995736766E-17
                b = remA
                remA = a + b
                remB += -(remA - b - a)
                if (remA > 0) {
                    break
                }

                // Remainder is negative, so decrement k and try again.
                // This should only happen if the input is very close
                // to an even multiple of pi/2.
                --k
            }
            this.k = k
            this.remA = remA
            this.remB = remB
        }
    }
}
