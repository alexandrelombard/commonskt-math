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

import com.github.alexandrelombard.commonskt.math3.exception.MathArithmeticException
import com.github.alexandrelombard.commonskt.math3.exception.NotPositiveException
import com.github.alexandrelombard.commonskt.math3.exception.NumberIsTooLargeException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats

/**
 * Combinatorial utilities.
 *
 * @since 3.3
 */
object CombinatoricsUtils {
    /** All long-representable factorials  */
    val FACTORIALS: LongArray = longArrayOf(
        1L, 1L, 2L,
        6L, 24L, 120L,
        720L, 5040L, 40320L,
        362880L, 3628800L, 39916800L,
        479001600L, 6227020800L, 87178291200L,
        1307674368000L, 20922789888000L, 355687428096000L,
        6402373705728000L, 121645100408832000L, 2432902008176640000L
    )

    /** Stirling numbers of the second kind.  */
    val STIRLING_S2: AtomicReference<Array<LongArray?>?>? =
        AtomicReference<Array<LongArray?>?>(null)

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
     * `MathIllegalArgumentException` is thrown)
     *  *  The result is small enough to fit into a `long`. The
     * largest value of `n` for which all coefficients are
     * `< Long.MAX_VALUE` is 66. If the computed value exceeds
     * `Long.MAX_VALUE` a `MathArithMeticException` is
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
    @ExperimentalStdlibApi
    fun binomialCoefficient(n: Int, k: Int): Long {
        checkBinomial(n, k)
        if (n == k || k == 0) {
            return 1
        }
        if (k == 1 || k == n - 1) {
            return n.toLong()
        }
        // Use symmetry for large k
        if (k > n / 2) {
            return binomialCoefficient(n, n - k)
        }

        // We use the formula
        // (n choose k) = n! / (n-k)! / k!
        // (n choose k) == ((n-k+1)*...*n) / (1*...*k)
        // which could be written
        // (n choose k) == (n-1 choose k-1) * n / k
        var result: Long = 1
        if (n <= 61) {
            // For n <= 61, the naive implementation cannot overflow.
            var i = n - k + 1
            for (j in 1..k) {
                result = result * i / j
                i++
            }
        } else if (n <= 66) {
            // For n > 61 but n <= 66, the result cannot overflow,
            // but we must take care not to overflow intermediate values.
            var i = n - k + 1
            for (j in 1..k) {
                // We know that (result * i) is divisible by j,
                // but (result * i) may overflow, so we split j:
                // Filter out the gcd, d, so j/d and i/d are integer.
                // result is divisible by (j/d) because (j/d)
                // is relative prime to (i/d) and is a divisor of
                // result * (i/d).
                val d: Long = ArithmeticUtils.gcd(i, j).toLong()
                result = result / (j / d) * (i / d)
                i++
            }
        } else {
            // For n > 66, a result overflow might occur, so we check
            // the multiplication, taking care to not overflow
            // unnecessary.
            var i = n - k + 1
            for (j in 1..k) {
                val d: Long = ArithmeticUtils.gcd(i, j).toLong()
                result = ArithmeticUtils.mulAndCheck(result / (j / d), i / d)
                i++
            }
        }
        return result
    }

    /**
     * Returns a `double` representation of the [ Binomial
 * Coefficient](http://mathworld.wolfram.com/BinomialCoefficient.html), "`n choose k`", the number of
     * `k`-element subsets that can be selected from an
     * `n`-element set.
     *
     *
     * <Strong>Preconditions</Strong>:
     *
     *  *  `0 <= k <= n ` (otherwise
     * `IllegalArgumentException` is thrown)
     *  *  The result is small enough to fit into a `double`. The
     * largest value of `n` for which all coefficients are less than
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
    @ExperimentalStdlibApi
    fun binomialCoefficientDouble(n: Int, k: Int): Double {
        checkBinomial(n, k)
        if (n == k || k == 0) {
            return 1.0
        }
        if (k == 1 || k == n - 1) {
            return n.toDouble()
        }
        if (k > n / 2) {
            return binomialCoefficientDouble(n, n - k)
        }
        if (n < 67) {
            return binomialCoefficient(n, k).toDouble()
        }
        var result = 1.0
        for (i in 1..k) {
            result *= (n - k + i).toDouble() / i.toDouble()
        }
        return FastMath.floor(result + 0.5)
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
     * `MathIllegalArgumentException` is thrown)
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
    @ExperimentalStdlibApi
    fun binomialCoefficientLog(n: Int, k: Int): Double {
        checkBinomial(n, k)
        if (n == k || k == 0) {
            return 0.0
        }
        if (k == 1 || k == n - 1) {
            return FastMath.log(n.toDouble())
        }

        /*
         * For values small enough to do exact integer computation,
         * return the log of the exact value
         */if (n < 67) {
            return FastMath.log(binomialCoefficient(n, k).toDouble())
        }

        /*
         * Return the log of binomialCoefficientDouble for values that will not
         * overflow binomialCoefficientDouble
         */if (n < 1030) {
            return FastMath.log(binomialCoefficientDouble(n, k))
        }
        if (k > n / 2) {
            return binomialCoefficientLog(n, n - k)
        }

        /*
         * Sum logs for values that could overflow
         */
        var logSum = 0.0

        // n!/(n-k)!
        for (i in n - k + 1..n) {
            logSum += FastMath.log(i.toDouble())
        }

        // divide by k!
        for (i in 2..k) {
            logSum -= FastMath.log(i.toDouble())
        }
        return logSum
    }

    /**
     * Returns n!. Shorthand for `n` [ Factorial](http://mathworld.wolfram.com/Factorial.html), the
     * product of the numbers `1,...,n`.
     *
     *
     * <Strong>Preconditions</Strong>:
     *
     *  *  `n >= 0` (otherwise
     * `MathIllegalArgumentException` is thrown)
     *  *  The result is small enough to fit into a `long`. The
     * largest value of `n` for which `n!` does not exceed
     * Long.MAX_VALUE} is 20. If the computed value exceeds `Long.MAX_VALUE`
     * an `MathArithMeticException ` is thrown.
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
    fun factorial(n: Int): Long {
        if (n < 0) {
            throw NotPositiveException(
                LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,
                n
            )
        }
        if (n > 20) {
            throw MathArithmeticException()
        }
        return FACTORIALS!![n]
    }

    /**
     * Compute n!, the[
 * factorial](http://mathworld.wolfram.com/Factorial.html) of `n` (the product of the numbers 1 to n), as a
     * `double`.
     * The result should be small enough to fit into a `double`: The
     * largest `n` for which `n!` does not exceed
     * `Double.MAX_VALUE` is 170. If the computed value exceeds
     * `Double.MAX_VALUE`, `Double.POSITIVE_INFINITY` is returned.
     *
     * @param n Argument.
     * @return `n!`
     * @throws NotPositiveException if `n < 0`.
     */
    fun factorialDouble(n: Int): Double {
        if (n < 0) {
            throw NotPositiveException(
                LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,
                n
            )
        }
        return if (n < 21) {
            FACTORIALS!![n].toDouble()
        } else FastMath.floor(FastMath.exp(factorialLog(n)) + 0.5)
    }

    /**
     * Compute the natural logarithm of the factorial of `n`.
     *
     * @param n Argument.
     * @return `n!`
     * @throws NotPositiveException if `n < 0`.
     */
    fun factorialLog(n: Int): Double {
        if (n < 0) {
            throw NotPositiveException(
                LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,
                n
            )
        }
        if (n < 21) {
            return FastMath.log(FACTORIALS[n].toDouble())
        }
        var logSum = 0.0
        for (i in 2..n) {
            logSum += FastMath.log(i.toDouble())
        }
        return logSum
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
    @ExperimentalStdlibApi
    fun stirlingS2(n: Int, k: Int): Long {
        if (k < 0) {
            throw NotPositiveException(k)
        }
        if (k > n) {
            throw NumberIsTooLargeException(k, n, true)
        }
        val stirlingS2: Array<LongArray?> = STIRLING_S2.get()
        return if (n < stirlingS2.size) {
            // the number is in the small cache
            stirlingS2[n]!![k]
        } else {
            // use explicit formula to compute the number without caching it
            if (k == 0) {
                0
            } else if (k == 1 || k == n) {
                1
            } else if (k == 2) {
                (1L shl n - 1) - 1L
            } else if (k == n - 1) {
                binomialCoefficient(n, 2)
            } else {
                // definition formula: note that this may trigger some overflow
                var sum: Long = 0
                var sign = if (k and 0x1 == 0) 1 else -1.toLong()
                for (j in 1..k) {
                    sign = -sign
                    sum += sign * binomialCoefficient(k, j) * ArithmeticUtils.pow(j, n)
                    if (sum < 0) {
                        // there was an overflow somewhere
                        throw MathArithmeticException(
                            LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN,
                            n, 0, stirlingS2.size - 1
                        )
                    }
                }
                sum / factorial(k)
            }
        }
    }

    /**
     * Returns an iterator whose range is the k-element subsets of {0, ..., n - 1}
     * represented as `int[]` arrays.
     *
     *
     * The arrays returned by the iterator are sorted in descending order and
     * they are visited in lexicographic order with significance from right to
     * left. For example, combinationsIterator(4, 2) returns an Iterator that
     * will generate the following sequence of arrays on successive calls to
     * `next()`:
     *
     *
     * `[0, 1], [0, 2], [1, 2], [0, 3], [1, 3], [2, 3]`
     *
     *
     * If `k == 0` an Iterator containing an empty array is returned and
     * if `k == n` an Iterator containing [0, ..., n -1] is returned.
     *
     * @param n Size of the set from which subsets are selected.
     * @param k Size of the subsets to be enumerated.
     * @return an [iterator][Iterator] over the k-sets in n.
     * @throws NotPositiveException if `n < 0`.
     * @throws NumberIsTooLargeException if `k > n`.
     */
    fun combinationsIterator(n: Int, k: Int): Iterator<IntArray> {
        return Combinations(n, k).iterator()
    }

    /**
     * Check binomial preconditions.
     *
     * @param n Size of the set.
     * @param k Size of the subsets to be counted.
     * @throws NotPositiveException if `n < 0`.
     * @throws NumberIsTooLargeException if `k > n`.
     */
    fun checkBinomial(
        n: Int,
        k: Int
    ) {
        if (n < k) {
            throw NumberIsTooLargeException(
                LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER,
                k, n, true
            )
        }
        if (n < 0) {
            throw NotPositiveException(LocalizedFormats.BINOMIAL_NEGATIVE_PARAMETER, n)
        }
    }
}
