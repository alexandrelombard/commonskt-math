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

import com.github.alexandrelombard.commonskt.math.BigDecimal
import com.github.alexandrelombard.commonskt.math.BigInteger
import com.github.alexandrelombard.commonskt.math.MathContext
import com.github.alexandrelombard.commonskt.math.RoundingMode
import com.github.alexandrelombard.commonskt.math3.Field
import com.github.alexandrelombard.commonskt.math3.FieldElement
import com.github.alexandrelombard.commonskt.math3.exception.MathArithmeticException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats

/**
 * Arbitrary precision decimal number.
 *
 *
 * This class is a simple wrapper around the standard `BigDecimal`
 * in order to implement the [FieldElement] interface.
 *
 * @since 2.0
 */
@ExperimentalStdlibApi
class BigReal : FieldElement<BigReal>, Comparable<BigReal> {
    /** Underlying BigDecimal.  */
    private val d: BigDecimal

    /** Rounding mode for divisions.  */
    private var roundingMode: RoundingMode = RoundingMode.HALF_UP

    /***
     * Sets the scale for division operations.
     * The default is 64
     * @return the scale
     * @since 2.1
     */
    /***
     * Sets the scale for division operations.
     * @param scale scale for division operations
     * @since 2.1
     */
    /*** BigDecimal scale  */
    var scale = 64

    /** Build an instance from a BigDecimal.
     * @param val value of the instance
     */
    constructor(`val`: BigDecimal) {
        d = `val`
    }

    /** Build an instance from a BigInteger.
     * @param val value of the instance
     */
    constructor(`val`: BigInteger) {
        d = BigDecimal(`val`)
    }

    /** Build an instance from an unscaled BigInteger.
     * @param unscaledVal unscaled value
     * @param scale scale to use
     */
    constructor(unscaledVal: BigInteger, scale: Int) {
        d = BigDecimal(unscaledVal, scale)
    }

    /** Build an instance from an unscaled BigInteger.
     * @param unscaledVal unscaled value
     * @param scale scale to use
     * @param mc to used
     */
    constructor(unscaledVal: BigInteger, scale: Int, mc: MathContext) {
        d = BigDecimal(unscaledVal, scale, mc)
    }

    /** Build an instance from a BigInteger.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: BigInteger, mc: MathContext) {
        d = BigDecimal(`val`, mc)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     */
    constructor(`in`: CharArray) {
        d = BigDecimal(`in`)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param offset offset of the first character to analyze
     * @param len length of the array slice to analyze
     */
    constructor(`in`: CharArray, offset: Int, len: Int) {
        d = BigDecimal(`in`, offset, len)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param offset offset of the first character to analyze
     * @param len length of the array slice to analyze
     * @param mc context to use
     */
    constructor(`in`: CharArray, offset: Int, len: Int, mc: MathContext) {
        d = BigDecimal(`in`, offset, len, mc)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param mc context to use
     */
    constructor(`in`: CharArray, mc: MathContext) {
        d = BigDecimal(`in`, mc)
    }

    /** Build an instance from a double.
     * @param val value of the instance
     */
    constructor(`val`: Double) {
        d = BigDecimal(`val`)
    }

    /** Build an instance from a double.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: Double, mc: MathContext) {
        d = BigDecimal(`val`, mc)
    }

    /** Build an instance from an int.
     * @param val value of the instance
     */
    constructor(`val`: Int) {
        d = BigDecimal(`val`)
    }

    /** Build an instance from an int.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: Int, mc: MathContext) {
        d = BigDecimal(`val`, mc)
    }

    /** Build an instance from a long.
     * @param val value of the instance
     */
    constructor(`val`: Long) {
        d = BigDecimal(`val`)
    }

    /** Build an instance from a long.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: Long, mc: MathContext) {
        d = BigDecimal(`val`, mc)
    }

    /** Build an instance from a String representation.
     * @param val character representation of the value
     */
    constructor(`val`: String) {
        d = BigDecimal(`val`)
    }

    /** Build an instance from a String representation.
     * @param val character representation of the value
     * @param mc context to use
     */
    constructor(`val`: String, mc: MathContext) {
        d = BigDecimal(`val`, mc)
    }

    /***
     * Gets the rounding mode for division operations
     * The default is `RoundingMode.HALF_UP`
     * @return the rounding mode.
     * @since 2.1
     */
    fun getRoundingMode(): RoundingMode {
        return roundingMode
    }

    /***
     * Sets the rounding mode for decimal divisions.
     * @param roundingMode rounding mode for decimal divisions
     * @since 2.1
     */
    fun setRoundingMode(roundingMode: RoundingMode) {
        this.roundingMode = roundingMode
    }

    /** {@inheritDoc}  */
    override fun add(a: BigReal): BigReal {
        return BigReal(d.add(a.d))
    }

    /** {@inheritDoc}  */
    override fun subtract(a: BigReal): BigReal {
        return BigReal(d.subtract(a.d))
    }

    /** {@inheritDoc}  */
    override fun negate(): BigReal {
        return BigReal(d.negate())
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathArithmeticException if `a` is zero
     */
    override fun divide(a: BigReal): BigReal {
        return try {
            BigReal(d.divide(a.d, scale, roundingMode))
        } catch (e: ArithmeticException) {
            // Division by zero has occurred
            throw MathArithmeticException(LocalizedFormats.ZERO_NOT_ALLOWED)
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathArithmeticException if `this` is zero
     */
    override fun reciprocal(): BigReal {
        return try {
            BigReal(BigDecimal.ONE.divide(d, scale, roundingMode))
        } catch (e: ArithmeticException) {
            // Division by zero has occurred
            throw MathArithmeticException(LocalizedFormats.ZERO_NOT_ALLOWED)
        }
    }

    /** {@inheritDoc}  */
    override fun multiply(a: BigReal): BigReal {
        return BigReal(d.multiply(a.d))
    }

    /** {@inheritDoc}  */
    override fun multiply(n: Int): BigReal {
        return BigReal(d.multiply(BigDecimal(n)))
    }

    /** {@inheritDoc}  */
    override operator fun compareTo(a: BigReal): Int {
        return d.compareTo(a.d)
    }

    /** Get the double value corresponding to the instance.
     * @return double value corresponding to the instance
     */
    fun doubleValue(): Double {
        return d.toDouble()
    }

    /** Get the BigDecimal value corresponding to the instance.
     * @return BigDecimal value corresponding to the instance
     */
    fun bigDecimalValue(): BigDecimal {
        return d
    }

    /** {@inheritDoc}  */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other is BigReal) {
            d == other.d
        } else false
    }

    /** {@inheritDoc}  */
    override fun hashCode(): Int {
        return d.hashCode()
    }

    /** {@inheritDoc}  */
    override val field: Field<BigReal>
        get() = BigRealField.instance

    companion object {
        /** A big real representing 0.  */
        val ZERO: BigReal = BigReal(BigDecimal.ZERO)

        /** A big real representing 1.  */
        val ONE: BigReal = BigReal(BigDecimal.ONE)

        /** Serializable version identifier.  */
        private const val serialVersionUID = 4984534880991310382L
    }
}
