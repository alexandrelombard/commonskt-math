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

import com.github.alexandrelombard.commonskt.math.BigInteger
import com.github.alexandrelombard.commonskt.math3.exception.MathArithmeticException
import com.github.alexandrelombard.commonskt.math3.exception.NotPositiveException
import com.github.alexandrelombard.commonskt.math3.exception.NumberIsTooLargeException
import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.min

/**
 * Some useful, arithmetics related, additions to the built-in functions in
 * [Math].
 *
 */
object ArithmeticUtils {
    /**
     * Add two integers, checking for overflow.
     *
     * @param x an addend
     * @param y an addend
     * @return the sum `x+y`
     * @throws MathArithmeticException if the result can not be represented
     * as an `int`.
     * @since 1.1
     */
    fun addAndCheck(x: Int, y: Int): Int {
        val s = x.toLong() + y.toLong()
        if (s < Int.MIN_VALUE || s > Int.MAX_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, x, y)
        }
        return s.toInt()
    }

    /**
     * Add two long integers, checking for overflow.
     *
     * @param a an addend
     * @param b an addend
     * @return the sum `a+b`
     * @throws MathArithmeticException if the result can not be represented as an long
     * @since 1.2
     */
    fun addAndCheck(a: Long, b: Long): Long {
        return addAndCheck(a, b, LocalizedFormats.OVERFLOW_IN_ADDITION)
    }

    /**
     * Returns an exact representation of the [ Binomial
 * Coefficient](http://mathworld.wolfram.com/BinomialCoefficient.html), "`n choose k`", the number of
     * `k`-element subsets that can be selected from an
     * `n`-element set.
     *
     *
     * <Strong>Preconditions</Strong>:
     *
     *  *  `0 <= k <= n ` (otherwise
     * `IllegalArgumentException` is thrown)
     *  *  The result is small enough to fit into a `long`. The
     * largest value of `n` for which all coefficients are
     * `< Long.MAX_VALUE` is 66. If the computed value exceeds
     * `Long.MAX_VALUE` an `ArithMeticException` is
     * thrown.
     *
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return `n choose k`
     * @throws NotPositiveException if `n < 0`.
     * @throws NumberIsTooLargeException if `k > n`.
     * @throws MathArithmeticException if the result is too large to be
     * represented by a long integer.
     */
    @Deprecated("use {@link CombinatoricsUtils#binomialCoefficient(int, int)}")
    fun binomialCoefficient(n: Int, k: Int): Long {
        return CombinatoricsUtils.binomialCoefficient(n, k)
    }

    /**
     * Returns a `double` representation of the [ Binomial Coefficient](http://mathworld.wolfram.com/BinomialCoefficient.html), "`n choose k`", the number of
     * `k`-element subsets that can be selected from an
     * `n`-element set.
     *
     *
     * <Strong>Preconditions</Strong>:
     *
     *  *  `0 <= k <= n ` (otherwise
     * `IllegalArgumentException` is thrown)
     *  *  The result is small enough to fit into a `double`. The
     * largest value of `n` for which all coefficients are <
     * Double.MAX_VALUE is 1029. If the computed value exceeds Double.MAX_VALUE,
     * Double.POSITIVE_INFINITY is returned
     *
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return `n choose k`
     * @throws NotPositiveException if `n < 0`.
     * @throws NumberIsTooLargeException if `k > n`.
     * @throws MathArithmeticException if the result is too large to be
     * represented by a long integer.
     */
    @Deprecated("use {@link CombinatoricsUtils#binomialCoefficientDouble(int, int)}")
    fun binomialCoefficientDouble(n: Int, k: Int): Double {
        return CombinatoricsUtils.binomialCoefficientDouble(n, k)
    }

    /**
     * Returns the natural `log` of the [ Binomial Coefficient](http://mathworld.wolfram.com/BinomialCoefficient.html), "`n choose k`", the number of
     * `k`-element subsets that can be selected from an
     * `n`-element set.
     *
     *
     * <Strong>Preconditions</Strong>:
     *
     *  *  `0 <= k <= n ` (otherwise
     * `IllegalArgumentException` is thrown)
     *
     *
     * @param n the size of the set
     * @param k the size of the subsets to be counted
     * @return `n choose k`
     * @throws NotPositiveException if `n < 0`.
     * @throws NumberIsTooLargeException if `k > n`.
     * @throws MathArithmeticException if the result is too large to be
     * represented by a long integer.
     */
    @Deprecated("use {@link CombinatoricsUtils#binomialCoefficientLog(int, int)}")
    fun binomialCoefficientLog(n: Int, k: Int): Double {
        return CombinatoricsUtils.binomialCoefficientLog(n, k)
    }

    /**
     * Returns n!. Shorthand for `n` [ Factorial](http://mathworld.wolfram.com/Factorial.html), the
     * product of the numbers `1,...,n`.
     *
     *
     * <Strong>Preconditions</Strong>:
     *
     *  *  `n >= 0` (otherwise
     * `IllegalArgumentException` is thrown)
     *  *  The result is small enough to fit into a `long`. The
     * largest value of `n` for which `n!` <
     * Long.MAX_VALUE} is 20. If the computed value exceeds `Long.MAX_VALUE`
     * an `ArithMeticException ` is thrown.
     *
     *
     *
     * @param n argument
     * @return `n!`
     * @throws MathArithmeticException if the result is too large to be represented
     * by a `long`.
     * @throws NotPositiveException if `n < 0`.
     * @throws MathArithmeticException if `n > 20`: The factorial value is too
     * large to fit in a `long`.
     */
    @Deprecated("use {@link CombinatoricsUtils#factorial(int)}")
    fun factorial(n: Int): Long {
        return CombinatoricsUtils.factorial(n)
    }

    /**
     * Compute n!, the [factorial](http://mathworld.wolfram.com/Factorial.html) of `n` (the product of the numbers 1 to n), as a
     * `double`.
     * The result should be small enough to fit into a `double`: The
     * largest `n` for which `n! < Double.MAX_VALUE` is 170.
     * If the computed value exceeds `Double.MAX_VALUE`,
     * `Double.POSITIVE_INFINITY` is returned.
     *
     * @param n Argument.
     * @return `n!`
     * @throws NotPositiveException if `n < 0`.
     */
    @Deprecated("use {@link CombinatoricsUtils#factorialDouble(int)}")
    fun factorialDouble(n: Int): Double {
        return CombinatoricsUtils.factorialDouble(n)
    }

    /**
     * Compute the natural logarithm of the factorial of `n`.
     *
     * @param n Argument.
     * @return `n!`
     * @throws NotPositiveException if `n < 0`.
     */
    @Deprecated("use {@link CombinatoricsUtils#factorialLog(int)}")
    fun factorialLog(n: Int): Double {
        return CombinatoricsUtils.factorialLog(n)
    }

    /**
     * Computes the greatest common divisor of the absolute value of two
     * numbers, using a modified version of the "binary gcd" method.
     * See Knuth 4.5.2 algorithm B.
     * The algorithm is due to Josef Stein (1961).
     * <br></br>
     * Special cases:
     *
     *  * The invocations
     * `gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)`,
     * `gcd(Integer.MIN_VALUE, 0)` and
     * `gcd(0, Integer.MIN_VALUE)` throw an
     * `ArithmeticException`, because the result would be 2^31, which
     * is too large for an int value.
     *  * The result of `gcd(x, x)`, `gcd(0, x)` and
     * `gcd(x, 0)` is the absolute value of `x`, except
     * for the special cases above.
     *  * The invocation `gcd(0, 0)` is the only one which returns
     * `0`.
     *
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor (never negative).
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative `int` value.
     * @since 1.1
     */
    @ExperimentalStdlibApi
    fun gcd(p: Int, q: Int): Int {
        var a = p
        var b = q
        if (a == 0 ||
            b == 0
        ) {
            if (a == Int.MIN_VALUE ||
                b == Int.MIN_VALUE
            ) {
                throw MathArithmeticException(
                    LocalizedFormats.GCD_OVERFLOW_32_BITS,
                    p, q
                )
            }
            return abs(a + b)
        }
        var al = a.toLong()
        var bl = b.toLong()
        var useLong = false
        if (a < 0) {
            if (Int.MIN_VALUE == a) {
                useLong = true
            } else {
                a = -a
            }
            al = -al
        }
        if (b < 0) {
            if (Int.MIN_VALUE == b) {
                useLong = true
            } else {
                b = -b
            }
            bl = -bl
        }
        if (useLong) {
            if (al == bl) {
                throw MathArithmeticException(
                    LocalizedFormats.GCD_OVERFLOW_32_BITS,
                    p, q
                )
            }
            var blbu = bl
            bl = al
            al = blbu % al
            if (al == 0L) {
                if (bl > Int.MAX_VALUE) {
                    throw MathArithmeticException(
                        LocalizedFormats.GCD_OVERFLOW_32_BITS,
                        p, q
                    )
                }
                return bl.toInt()
            }
            blbu = bl

            // Now "al" and "bl" fit in an "int".
            b = al.toInt()
            a = (blbu % al).toInt()
        }
        return gcdPositive(a, b)
    }

    /**
     * Computes the greatest common divisor of two *positive* numbers
     * (this precondition is *not* checked and the result is undefined
     * if not fulfilled) using the "binary gcd" method which avoids division
     * and modulo operations.
     * See Knuth 4.5.2 algorithm B.
     * The algorithm is due to Josef Stein (1961).
     * <br></br>
     * Special cases:
     *
     *  * The result of `gcd(x, x)`, `gcd(0, x)` and
     * `gcd(x, 0)` is the value of `x`.
     *  * The invocation `gcd(0, 0)` is the only one which returns
     * `0`.
     *
     *
     * @param a Positive number.
     * @param b Positive number.
     * @return the greatest common divisor.
     */
    @ExperimentalStdlibApi
    private fun gcdPositive(a: Int, b: Int): Int {
        var a = a
        var b = b
        if (a == 0) {
            return b
        } else if (b == 0) {
            return a
        }

        // Make "a" and "b" odd, keeping track of common power of 2.
        val aTwos: Int = a.countTrailingZeroBits()
        a = a shr aTwos
        val bTwos: Int = b.countTrailingZeroBits()
        b = b shr bTwos
        val shift = min(aTwos, bTwos)

        // "a" and "b" are positive.
        // If a > b then "gdc(a, b)" is equal to "gcd(a - b, b)".
        // If a < b then "gcd(a, b)" is equal to "gcd(b - a, a)".
        // Hence, in the successive iterations:
        //  "a" becomes the absolute difference of the current values,
        //  "b" becomes the minimum of the current values.
        while (a != b) {
            val delta = a - b
            b = min(a, b)
            a = abs(delta)

            // Remove any power of 2 in "a" ("b" is guaranteed to be odd).
            a = a shr a.countTrailingZeroBits()
        }

        // Recover the common power of 2.
        return a shl shift
    }

    /**
     *
     *
     * Gets the greatest common divisor of the absolute value of two numbers,
     * using the "binary gcd" method which avoids division and modulo
     * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
     * Stein (1961).
     *
     * Special cases:
     *
     *  * The invocations
     * `gcd(Long.MIN_VALUE, Long.MIN_VALUE)`,
     * `gcd(Long.MIN_VALUE, 0L)` and
     * `gcd(0L, Long.MIN_VALUE)` throw an
     * `ArithmeticException`, because the result would be 2^63, which
     * is too large for a long value.
     *  * The result of `gcd(x, x)`, `gcd(0L, x)` and
     * `gcd(x, 0L)` is the absolute value of `x`, except
     * for the special cases above.
     *  * The invocation `gcd(0L, 0L)` is the only one which returns
     * `0L`.
     *
     *
     * @param p Number.
     * @param q Number.
     * @return the greatest common divisor, never negative.
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative `long` value.
     * @since 2.1
     */
    fun gcd(p: Long, q: Long): Long {
        var u = p
        var v = q
        if (u == 0L || v == 0L) {
            if (u == Long.MIN_VALUE || v == Long.MIN_VALUE) {
                throw MathArithmeticException(
                    LocalizedFormats.GCD_OVERFLOW_64_BITS,
                    p, q
                )
            }
            return abs(u) + abs(v)
        }
        // keep u and v negative, as negative integers range down to
        // -2^63, while positive numbers can only be as large as 2^63-1
        // (i.e. we can't necessarily negate a negative number without
        // overflow)
        /* assert u!=0 && v!=0; */if (u > 0) {
            u = -u
        } // make u negative
        if (v > 0) {
            v = -v
        } // make v negative
        // B1. [Find power of 2]
        var k = 0
        while (u and 1 == 0L && v and 1 == 0L && k < 63) { // while u and v are
            // both even...
            u /= 2
            v /= 2
            k++ // cast out twos.
        }
        if (k == 63) {
            throw MathArithmeticException(
                LocalizedFormats.GCD_OVERFLOW_64_BITS,
                p, q
            )
        }
        // B2. Initialize: u and v have been divided by 2^k and at least
        // one is odd.
        var t = if (u and 1 == 1L) v else -(u / 2) /* B3 */
        // t negative: u was odd, v may be even (t replaces v)
        // t positive: u was even, v is odd (t replaces u)
        do {
            /* assert u<0 && v<0; */
            // B4/B3: cast out twos from t.
            while (t and 1 == 0L) { // while t is even..
                t /= 2 // cast out twos
            }
            // B5 [reset max(u,v)]
            if (t > 0) {
                u = -t
            } else {
                v = t
            }
            // B6/B3. at this point both u and v should be odd.
            t = (v - u) / 2
            // |u| larger: t positive (replace u)
            // |v| larger: t negative (replace v)
        } while (t != 0L)
        return -u * (1L shl k) // gcd is u*2^k
    }

    /**
     *
     *
     * Returns the least common multiple of the absolute value of two numbers,
     * using the formula `lcm(a,b) = (a / gcd(a,b)) * b`.
     *
     * Special cases:
     *
     *  * The invocations `lcm(Integer.MIN_VALUE, n)` and
     * `lcm(n, Integer.MIN_VALUE)`, where `abs(n)` is a
     * power of 2, throw an `ArithmeticException`, because the result
     * would be 2^31, which is too large for an int value.
     *  * The result of `lcm(0, x)` and `lcm(x, 0)` is
     * `0` for any `x`.
     *
     *
     * @param a Number.
     * @param b Number.
     * @return the least common multiple, never negative.
     * @throws MathArithmeticException if the result cannot be represented as
     * a non-negative `int` value.
     * @since 1.1
     */
    @ExperimentalStdlibApi
    fun lcm(a: Int, b: Int): Int {
        if (a == 0 || b == 0) {
            return 0
        }
        val lcm = abs(mulAndCheck(a / gcd(a, b), b))
        if (lcm == Int.MIN_VALUE) {
            throw MathArithmeticException(
                LocalizedFormats.LCM_OVERFLOW_32_BITS,
                a, b
            )
        }
        return lcm
    }

    /**
     *
     *
     * Returns the least common multiple of the absolute value of two numbers,
     * using the formula `lcm(a,b) = (a / gcd(a,b)) * b`.
     *
     * Special cases:
     *
     *  * The invocations `lcm(Long.MIN_VALUE, n)` and
     * `lcm(n, Long.MIN_VALUE)`, where `abs(n)` is a
     * power of 2, throw an `ArithmeticException`, because the result
     * would be 2^63, which is too large for an int value.
     *  * The result of `lcm(0L, x)` and `lcm(x, 0L)` is
     * `0L` for any `x`.
     *
     *
     * @param a Number.
     * @param b Number.
     * @return the least common multiple, never negative.
     * @throws MathArithmeticException if the result cannot be represented
     * as a non-negative `long` value.
     * @since 2.1
     */
    fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) {
            return 0
        }
        val lcm = abs(mulAndCheck(a / gcd(a, b), b))
        if (lcm == Long.MIN_VALUE) {
            throw MathArithmeticException(
                LocalizedFormats.LCM_OVERFLOW_64_BITS,
                a, b
            )
        }
        return lcm
    }

    /**
     * Multiply two integers, checking for overflow.
     *
     * @param x Factor.
     * @param y Factor.
     * @return the product `x * y`.
     * @throws MathArithmeticException if the result can not be
     * represented as an `int`.
     * @since 1.1
     */
    fun mulAndCheck(x: Int, y: Int): Int {
        val m = x.toLong() * y.toLong()
        if (m < Int.MIN_VALUE || m > Int.MAX_VALUE) {
            throw MathArithmeticException()
        }
        return m.toInt()
    }

    /**
     * Multiply two long integers, checking for overflow.
     *
     * @param a Factor.
     * @param b Factor.
     * @return the product `a * b`.
     * @throws MathArithmeticException if the result can not be represented
     * as a `long`.
     * @since 1.2
     */
    fun mulAndCheck(a: Long, b: Long): Long {
        val ret: Long
        ret = if (a > b) {
            // use symmetry to reduce boundary cases
            mulAndCheck(b, a)
        } else {
            if (a < 0) {
                if (b < 0) {
                    // check for positive overflow with negative a, negative b
                    if (a >= Long.MAX_VALUE / b) {
                        a * b
                    } else {
                        throw MathArithmeticException()
                    }
                } else if (b > 0) {
                    // check for negative overflow with negative a, positive b
                    if (Long.MIN_VALUE / b <= a) {
                        a * b
                    } else {
                        throw MathArithmeticException()
                    }
                } else {
                    // assert b == 0
                    0
                }
            } else if (a > 0) {
                // assert a > 0
                // assert b > 0

                // check for positive overflow with positive a, positive b
                if (a <= Long.MAX_VALUE / b) {
                    a * b
                } else {
                    throw MathArithmeticException()
                }
            } else {
                // assert a == 0
                0
            }
        }
        return ret
    }

    /**
     * Subtract two integers, checking for overflow.
     *
     * @param x Minuend.
     * @param y Subtrahend.
     * @return the difference `x - y`.
     * @throws MathArithmeticException if the result can not be represented
     * as an `int`.
     * @since 1.1
     */
    fun subAndCheck(x: Int, y: Int): Int {
        val s = x.toLong() - y.toLong()
        if (s < Int.MIN_VALUE || s > Int.MAX_VALUE) {
            throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, x, y)
        }
        return s.toInt()
    }

    /**
     * Subtract two long integers, checking for overflow.
     *
     * @param a Value.
     * @param b Value.
     * @return the difference `a - b`.
     * @throws MathArithmeticException if the result can not be represented as a
     * `long`.
     * @since 1.2
     */
    fun subAndCheck(a: Long, b: Long): Long {
        val ret: Long
        ret = if (b == Long.MIN_VALUE) {
            if (a < 0) {
                a - b
            } else {
                throw MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, a, -b)
            }
        } else {
            // use additive inverse
            addAndCheck(a, -b, LocalizedFormats.OVERFLOW_IN_ADDITION)
        }
        return ret
    }

    /**
     * Raise an int to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return \( k^e \)
     * @throws NotPositiveException if `e < 0`.
     * @throws MathArithmeticException if the result would overflow.
     */
    fun pow(
        k: Int,
        e: Int
    ): Int {
        if (e < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        return try {
            var exp = e
            var result = 1
            var k2p = k
            while (true) {
                if (exp and 0x1 != 0) {
                    result = mulAndCheck(result, k2p)
                }
                exp = exp shr 1
                if (exp == 0) {
                    break
                }
                k2p = mulAndCheck(k2p, k2p)
            }
            result
        } catch (mae: MathArithmeticException) {
            // Add context information.
            mae.context.addMessage(LocalizedFormats.OVERFLOW)
            mae.context.addMessage(LocalizedFormats.BASE, k)
            mae.context.addMessage(LocalizedFormats.EXPONENT, e)
            throw mae
        }
    }

    /**
     * Raise an int to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if `e < 0`.
     */
    @Deprecated("As of 3.3. Please use {@link #pow(int,int)} instead.")
    fun pow(k: Int, e: Long): Int {
        var e = e
        if (e < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        var result = 1
        var k2p = k
        while (e != 0L) {
            if (e and 0x1 != 0L) {
                result *= k2p
            }
            k2p *= k2p
            e = e shr 1
        }
        return result
    }

    /**
     * Raise a long to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return \( k^e \)
     * @throws NotPositiveException if `e < 0`.
     * @throws MathArithmeticException if the result would overflow.
     */
    fun pow(
        k: Long,
        e: Int
    ): Long {
        if (e < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        return try {
            var exp = e
            var result: Long = 1
            var k2p = k
            while (true) {
                if (exp and 0x1 != 0) {
                    result = mulAndCheck(result, k2p)
                }
                exp = exp shr 1
                if (exp == 0) {
                    break
                }
                k2p = mulAndCheck(k2p, k2p)
            }
            result
        } catch (mae: MathArithmeticException) {
            // Add context information.
            mae.context.addMessage(LocalizedFormats.OVERFLOW)
            mae.context.addMessage(LocalizedFormats.BASE, k)
            mae.context.addMessage(LocalizedFormats.EXPONENT, e)
            throw mae
        }
    }

    /**
     * Raise a long to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if `e < 0`.
     */
    @Deprecated("As of 3.3. Please use {@link #pow(long,int)} instead.")
    fun pow(k: Long, e: Long): Long {
        var e = e
        if (e < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        var result = 1L
        var k2p = k
        while (e != 0L) {
            if (e and 0x1 != 0L) {
                result *= k2p
            }
            k2p *= k2p
            e = e shr 1
        }
        return result
    }

    /**
     * Raise a BigInteger to an int power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if `e < 0`.
     */
    @ExperimentalStdlibApi
    fun pow(k: BigInteger, e: Int): BigInteger {
        if (e < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        return k.pow(e)
    }

    /**
     * Raise a BigInteger to a long power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if `e < 0`.
     */
    @ExperimentalStdlibApi
    fun pow(k: BigInteger, e: Long): BigInteger {
        var e = e
        if (e < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        var result: BigInteger = BigInteger.ONE
        var k2p: BigInteger = k
        while (e != 0L) {
            if (e and 0x1 != 0L) {
                result = result.multiply(k2p)
            }
            k2p = k2p.multiply(k2p)
            e = e shr 1
        }
        return result
    }

    /**
     * Raise a BigInteger to a BigInteger power.
     *
     * @param k Number to raise.
     * @param e Exponent (must be positive or zero).
     * @return k<sup>e</sup>
     * @throws NotPositiveException if `e < 0`.
     */
    @ExperimentalStdlibApi
    fun pow(k: BigInteger, e: BigInteger): BigInteger {
        var e: BigInteger = e
        if (e.compareTo(BigInteger.ZERO) < 0) {
            throw NotPositiveException(LocalizedFormats.EXPONENT, e)
        }
        var result: BigInteger = BigInteger.ONE
        var k2p: BigInteger = k
        while (BigInteger.ZERO != e) {
            if (e.testBit(0)) {
                result = result.multiply(k2p)
            }
            k2p = k2p.multiply(k2p)
            e = e.shiftRight(1)
        }
        return result
    }

    /**
     * Returns the [
 * Stirling number of the second kind](http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html), "`S(n,k)`", the number of
     * ways of partitioning an `n`-element set into `k` non-empty
     * subsets.
     *
     *
     * The preconditions are `0 <= k <= n ` (otherwise
     * `NotPositiveException` is thrown)
     *
     * @param n the size of the set
     * @param k the number of non-empty subsets
     * @return `S(n,k)`
     * @throws NotPositiveException if `k < 0`.
     * @throws NumberIsTooLargeException if `k > n`.
     * @throws MathArithmeticException if some overflow happens, typically for n exceeding 25 and
     * k between 20 and n-2 (S(n,n-1) is handled specifically and does not overflow)
     * @since 3.1
     */
    @Deprecated("use {@link CombinatoricsUtils#stirlingS2(int, int)}")
    fun stirlingS2(n: Int, k: Int): Long {
        return CombinatoricsUtils.stirlingS2(n, k)
    }

    /**
     * Add two long integers, checking for overflow.
     *
     * @param a Addend.
     * @param b Addend.
     * @param pattern Pattern to use for any thrown exception.
     * @return the sum `a + b`.
     * @throws MathArithmeticException if the result cannot be represented
     * as a `long`.
     * @since 1.2
     */
    private fun addAndCheck(a: Long, b: Long, pattern: Localizable): Long {
        val result = a + b
        if ((a xor b) < 0 || ((a xor result).toInt() < 0)) {
            throw MathArithmeticException(pattern, a, b)
        }
        return result
    }

    /**
     * Returns true if the argument is a power of two.
     *
     * @param n the number to test
     * @return true if the argument is a power of two
     */
    fun isPowerOfTwo(n: Long): Boolean {
        return n > 0 && n and n - 1 == 0L
    }
}
