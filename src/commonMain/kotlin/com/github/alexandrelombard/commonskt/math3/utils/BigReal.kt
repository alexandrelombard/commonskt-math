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
import com.github.alexandrelombard.commonskt.math3.FieldElement
import com.github.alexandrelombard.commonskt.math3.exception.MathArithmeticException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import org.apache.commons.math3.Field
import org.apache.commons.math3.FieldElement
import org.apache.commons.math3.exception.MathArithmeticException
import org.apache.commons.math3.exception.util.LocalizedFormats


/**
 * Arbitrary precision decimal number.
 *
 *
 * This class is a simple wrapper around the standard `BigDecimal`
 * in order to implement the [FieldElement] interface.
 *
 * @since 2.0
 */
class BigReal : FieldElement<BigReal?>, Comparable<BigReal?>, java.io.Serializable {
    /** Underlying BigDecimal.  */
    private val d: java.math.BigDecimal

    /** Rounding mode for divisions.  */
    private var roundingMode: java.math.RoundingMode = java.math.RoundingMode.HALF_UP

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
    constructor(`val`: java.math.BigDecimal) {
        d = `val`
    }

    /** Build an instance from a BigInteger.
     * @param val value of the instance
     */
    constructor(`val`: java.math.BigInteger?) {
        d = java.math.BigDecimal(`val`)
    }

    /** Build an instance from an unscaled BigInteger.
     * @param unscaledVal unscaled value
     * @param scale scale to use
     */
    constructor(unscaledVal: java.math.BigInteger?, scale: Int) {
        d = java.math.BigDecimal(unscaledVal, scale)
    }

    /** Build an instance from an unscaled BigInteger.
     * @param unscaledVal unscaled value
     * @param scale scale to use
     * @param mc to used
     */
    constructor(unscaledVal: java.math.BigInteger?, scale: Int, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(unscaledVal, scale, mc)
    }

    /** Build an instance from a BigInteger.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: java.math.BigInteger?, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`val`, mc)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     */
    constructor(`in`: CharArray?) {
        d = java.math.BigDecimal(`in`)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param offset offset of the first character to analyze
     * @param len length of the array slice to analyze
     */
    constructor(`in`: CharArray?, offset: Int, len: Int) {
        d = java.math.BigDecimal(`in`, offset, len)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param offset offset of the first character to analyze
     * @param len length of the array slice to analyze
     * @param mc context to use
     */
    constructor(`in`: CharArray?, offset: Int, len: Int, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`in`, offset, len, mc)
    }

    /** Build an instance from a characters representation.
     * @param in character representation of the value
     * @param mc context to use
     */
    constructor(`in`: CharArray?, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`in`, mc)
    }

    /** Build an instance from a double.
     * @param val value of the instance
     */
    constructor(`val`: Double) {
        d = java.math.BigDecimal(`val`)
    }

    /** Build an instance from a double.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: Double, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`val`, mc)
    }

    /** Build an instance from an int.
     * @param val value of the instance
     */
    constructor(`val`: Int) {
        d = java.math.BigDecimal(`val`)
    }

    /** Build an instance from an int.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: Int, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`val`, mc)
    }

    /** Build an instance from a long.
     * @param val value of the instance
     */
    constructor(`val`: Long) {
        d = java.math.BigDecimal(`val`)
    }

    /** Build an instance from a long.
     * @param val value of the instance
     * @param mc context to use
     */
    constructor(`val`: Long, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`val`, mc)
    }

    /** Build an instance from a String representation.
     * @param val character representation of the value
     */
    constructor(`val`: String?) {
        d = java.math.BigDecimal(`val`)
    }

    /** Build an instance from a String representation.
     * @param val character representation of the value
     * @param mc context to use
     */
    constructor(`val`: String?, mc: java.math.MathContext?) {
        d = java.math.BigDecimal(`val`, mc)
    }

    /***
     * Gets the rounding mode for division operations
     * The default is `RoundingMode.HALF_UP`
     * @return the rounding mode.
     * @since 2.1
     */
    fun getRoundingMode(): java.math.RoundingMode {
        return roundingMode
    }

    /***
     * Sets the rounding mode for decimal divisions.
     * @param roundingMode rounding mode for decimal divisions
     * @since 2.1
     */
    fun setRoundingMode(roundingMode: java.math.RoundingMode) {
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
    @Throws(MathArithmeticException::class)
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
    @Throws(MathArithmeticException::class)
    override fun reciprocal(): BigReal {
        return try {
            BigReal(java.math.BigDecimal.ONE.divide(d, scale, roundingMode))
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
        return BigReal(d.multiply(java.math.BigDecimal(n)))
    }

    /** {@inheritDoc}  */
    override operator fun compareTo(a: BigReal): Int {
        return d.compareTo(a.d)
    }

    /** Get the double value corresponding to the instance.
     * @return double value corresponding to the instance
     */
    fun doubleValue(): Double {
        return d.doubleValue()
    }

    /** Get the BigDecimal value corresponding to the instance.
     * @return BigDecimal value corresponding to the instance
     */
    fun bigDecimalValue(): java.math.BigDecimal {
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
        get() = BigRealField.getInstance()

    companion object {
        /** A big real representing 0.  */
        val ZERO: BigReal = BigReal(java.math.BigDecimal.ZERO)

        /** A big real representing 1.  */
        val ONE: BigReal = BigReal(java.math.BigDecimal.ONE)

        /** Serializable version identifier.  */
        private const val serialVersionUID = 4984534880991310382L
    }
}
