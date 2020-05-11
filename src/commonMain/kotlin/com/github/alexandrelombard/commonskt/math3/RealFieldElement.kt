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
package com.github.alexandrelombard.commonskt.math3

/**
 * Interface representing a [real](http://mathworld.wolfram.com/RealNumber.html)
 * [field](http://mathworld.wolfram.com/Field.html).
 * @param <T> the type of the field elements
 * @see FieldElement
 *
 * @since 3.2
</T> */
interface RealFieldElement<T> : FieldElement<T> {
    /** Get the real value of the number.
     * @return real value
     */
    val real: Double

    /** '+' operator.
     * @param a right hand side parameter of the operator
     * @return this+a
     */
    fun add(a: Double): T

    /** '-' operator.
     * @param a right hand side parameter of the operator
     * @return this-a
     */
    fun subtract(a: Double): T

    /** '' operator.
     * @param a right hand side parameter of the operator
     * @return thisa
     */
    fun multiply(a: Double): T

    /** '' operator.
     * @param a right hand side parameter of the operator
     * @return thisa
     */
    fun divide(a: Double): T

    /** IEEE remainder operator.
     * @param a right hand side parameter of the operator
     * @return this - n  a where n is the closest integer to this/a
     * (the even integer is chosen for n if this/a is halfway between two integers)
     */
    fun remainder(a: Double): T

    /** IEEE remainder operator.
     * @param a right hand side parameter of the operator
     * @return this - n  a where n is the closest integer to this/a
     * (the even integer is chosen for n if this/a is halfway between two integers)
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    fun remainder(a: T): T

    /** absolute value.
     * @return abs(this)
     */
    fun abs(): T

    /** Get the smallest whole number larger than instance.
     * @return ceil(this)
     */
    fun ceil(): T

    /** Get the largest whole number smaller than instance.
     * @return floor(this)
     */
    fun floor(): T

    /** Get the whole number that is the nearest to the instance, or the even one if x is exactly half way between two integers.
     * @return a double number r such that r is an integer r - 0.5  this  r + 0.5
     */
    fun rint(): T

    /** Get the closest long to instance value.
     * @return closest long to [.getReal]
     */
    fun round(): Long

    /** Compute the signum of the instance.
     * The signum is -1 for negative numbers, +1 for positive numbers and 0 otherwise
     * @return -1.0, -0.0, +0.0, +1.0 or NaN depending on sign of a
     */
    fun signum(): T

    /**
     * Returns the instance with the sign of the argument.
     * A NaN `sign` argument is treated as positive.
     *
     * @param sign the sign for the returned value
     * @return the instance with the same sign as the `sign` argument
     */
    fun copySign(sign: T): T

    /**
     * Returns the instance with the sign of the argument.
     * A NaN `sign` argument is treated as positive.
     *
     * @param sign the sign for the returned value
     * @return the instance with the same sign as the `sign` argument
     */
    fun copySign(sign: Double): T

    /**
     * Multiply the instance by a power of 2.
     * @param n power of 2
     * @return this  2<sup>n</sup>
     */
    fun scalb(n: Int): T

    /**
     * Returns the hypotenuse of a triangle with sides `this` and `y`
     * - sqrt(*this*<sup>2</sup>&nbsp;+*y*<sup>2</sup>)
     * avoiding intermediate overflow or underflow.
     *
     *
     *  *  If either argument is infinite, then the result is positive infinity.
     *  *  else, if either argument is NaN then the result is NaN.
     *
     *
     * @param y a value
     * @return sqrt(*this*<sup>2</sup>&nbsp;+*y*<sup>2</sup>)
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    fun hypot(y: T): T

    /** {@inheritDoc}  */
    override fun reciprocal(): T

    /** Square root.
     * @return square root of the instance
     */
    fun sqrt(): T

    /** Cubic root.
     * @return cubic root of the instance
     */
    fun cbrt(): T

    /** N<sup>th</sup> root.
     * @param n order of the root
     * @return n<sup>th</sup> root of the instance
     */
    fun rootN(n: Int): T

    /** Power operation.
     * @param p power to apply
     * @return this<sup>p</sup>
     */
    fun pow(p: Double): T

    /** Integer power operation.
     * @param n power to apply
     * @return this<sup>n</sup>
     */
    fun pow(n: Int): T

    /** Power operation.
     * @param e exponent
     * @return this<sup>e</sup>
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    fun pow(e: T): T

    /** Exponential.
     * @return exponential of the instance
     */
    fun exp(): T

    /** Exponential minus 1.
     * @return exponential minus one of the instance
     */
    fun expm1(): T

    /** Natural logarithm.
     * @return logarithm of the instance
     */
    fun log(): T

    /** Shifted natural logarithm.
     * @return logarithm of one plus the instance
     */
    fun log1p(): T
    //    TODO: add this method in 4.0, as it is not possible to do it in 3.2
    //          due to incompatibility of the return type in the Dfp class
    //    /** Base 10 logarithm.
    //     * @return base 10 logarithm of the instance
    //     */
    //    T log10();
    /** Cosine operation.
     * @return cos(this)
     */
    fun cos(): T

    /** Sine operation.
     * @return sin(this)
     */
    fun sin(): T

    /** Tangent operation.
     * @return tan(this)
     */
    fun tan(): T

    /** Arc cosine operation.
     * @return acos(this)
     */
    fun acos(): T

    /** Arc sine operation.
     * @return asin(this)
     */
    fun asin(): T

    /** Arc tangent operation.
     * @return atan(this)
     */
    fun atan(): T

    /** Two arguments arc tangent operation.
     * @param x second argument of the arc tangent
     * @return atan2(this, x)
     * @exception DimensionMismatchException if number of free parameters or orders are inconsistent
     */
    fun atan2(x: T): T

    /** Hyperbolic cosine operation.
     * @return cosh(this)
     */
    fun cosh(): T

    /** Hyperbolic sine operation.
     * @return sinh(this)
     */
    fun sinh(): T

    /** Hyperbolic tangent operation.
     * @return tanh(this)
     */
    fun tanh(): T

    /** Inverse hyperbolic cosine operation.
     * @return acosh(this)
     */
    fun acosh(): T

    /** Inverse hyperbolic sine operation.
     * @return asin(this)
     */
    fun asinh(): T

    /** Inverse hyperbolic  tangent operation.
     * @return atanh(this)
     */
    fun atanh(): T

    /**
     * Compute a linear combination.
     * @param a Factors.
     * @param b Factors.
     * @return `<sub>i</sub> a<sub>i</sub> b<sub>i</sub>`.
     * @throws DimensionMismatchException if arrays dimensions don't match
     * @since 3.2
     */
    fun linearCombination(a: Array<T>?, b: Array<T>?): T

    /**
     * Compute a linear combination.
     * @param a Factors.
     * @param b Factors.
     * @return `<sub>i</sub> a<sub>i</sub> b<sub>i</sub>`.
     * @throws DimensionMismatchException if arrays dimensions don't match
     * @since 3.2
     */
    fun linearCombination(a: DoubleArray?, b: Array<T>?): T

    /**
     * Compute a linear combination.
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub>
     * @see .linearCombination
     * @see .linearCombination
     * @since 3.2
     */
    fun linearCombination(a1: T, b1: T, a2: T, b2: T): T

    /**
     * Compute a linear combination.
     * @param a1 first factor of the first term
     * @param b1 second factor of the first term
     * @param a2 first factor of the second term
     * @param b2 second factor of the second term
     * @return a<sub>1</sub>b<sub>1</sub> +
     * a<sub>2</sub>b<sub>2</sub>
     * @see .linearCombination
     * @see .linearCombination
     * @since 3.2
     */
    fun linearCombination(a1: Double, b1: T, a2: Double, b2: T): T

    /**
     * Compute a linear combination.
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
     * @since 3.2
     */
    fun linearCombination(a1: T, b1: T, a2: T, b2: T, a3: T, b3: T): T

    /**
     * Compute a linear combination.
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
     * @since 3.2
     */
    fun linearCombination(a1: Double, b1: T, a2: Double, b2: T, a3: Double, b3: T): T

    /**
     * Compute a linear combination.
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
     * @since 3.2
     */
    fun linearCombination(a1: T, b1: T, a2: T, b2: T, a3: T, b3: T, a4: T, b4: T): T

    /**
     * Compute a linear combination.
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
     * @since 3.2
     */
    fun linearCombination(
        a1: Double,
        b1: T,
        a2: Double,
        b2: T,
        a3: Double,
        b3: T,
        a4: Double,
        b4: T
    ): T
}
