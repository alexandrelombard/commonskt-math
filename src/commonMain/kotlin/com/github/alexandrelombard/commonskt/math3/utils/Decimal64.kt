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
import com.github.alexandrelombard.commonskt.math3.exception.DimensionMismatchException
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.IEEEremainder
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.acos
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.acosh
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.asin
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.asinh
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.atan
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.atan2
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.atanh
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.cbrt
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.ceil
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.copySign
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.cos
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.cosh
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.exp
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.expm1
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.floor
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.hypot
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.log
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.log10
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.log1p
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.pow
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.rint
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.round
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.scalb
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.signum
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.sin
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.sinh
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.sqrt
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.tan
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.tanh
import com.github.alexandrelombard.commonskt.math3.utils.MathArrays.linearCombination
import org.apache.commons.math3.Field
import org.apache.commons.math3.RealFieldElement
import org.apache.commons.math3.exception.DimensionMismatchException


/**
 * This class wraps a `double` value in an object. It is similar to the
 * standard class [Double], while also implementing the
 * [RealFieldElement] interface.
 *
 * @since 3.1
 */
class Decimal64
/**
 * Creates a new instance of this class.
 *
 * @param x the primitive `double` value of the object to be created
 */(
    /** The primitive `double` value of this object.  */
    override val real: Double
) : Number(), RealFieldElement<Decimal64?>,
    Comparable<Decimal64?> {
    companion object {
        /** The constant value of `0d` as a `Decimal64`.  */
        var ZERO: Decimal64? = null

        /** The constant value of `1d` as a `Decimal64`.  */
        var ONE: Decimal64? = null

        /**
         * The constant value of [Double.NEGATIVE_INFINITY] as a
         * `Decimal64`.
         */
        var NEGATIVE_INFINITY: Decimal64? = null

        /**
         * The constant value of [Double.POSITIVE_INFINITY] as a
         * `Decimal64`.
         */
        var POSITIVE_INFINITY: Decimal64? = null

        /** The constant value of [Double.NaN] as a `Decimal64`.  */
        var NAN: Decimal64? = null

        /**  */
        private const val serialVersionUID = 20120227L

        init {
            ZERO = Decimal64(0.0)
            ONE = Decimal64(1.0)
            NEGATIVE_INFINITY = Decimal64(Double.NEGATIVE_INFINITY)
            POSITIVE_INFINITY = Decimal64(Double.POSITIVE_INFINITY)
            NAN = Decimal64(Double.NaN)
        }
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    /*
     * Methods from the FieldElement interface.
     */
    /** {@inheritDoc}  */
    override val field: Field<Decimal64>
        get() = Decimal64Field.getInstance()

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.add(a).equals(new Decimal64(this.doubleValue()
     * + a.doubleValue()))`.
     */
    override fun add(a: Decimal64): Decimal64 {
        return Decimal64(real + a.real)
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.subtract(a).equals(new Decimal64(this.doubleValue()
     * - a.doubleValue()))`.
     */
    override fun subtract(a: Decimal64): Decimal64 {
        return Decimal64(real - a.real)
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.negate().equals(new Decimal64(-this.doubleValue()))`.
     */
    override fun negate(): Decimal64 {
        return Decimal64(-real)
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.multiply(a).equals(new Decimal64(this.doubleValue()
     * * a.doubleValue()))`.
     */
    override fun multiply(a: Decimal64): Decimal64 {
        return Decimal64(real * a.real)
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.multiply(n).equals(new Decimal64(n * this.doubleValue()))`.
     */
    override fun multiply(n: Int): Decimal64 {
        return Decimal64(n * real)
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.divide(a).equals(new Decimal64(this.doubleValue()
     * / a.doubleValue()))`.
     *
     */
    override fun divide(a: Decimal64): Decimal64 {
        return Decimal64(real / a.real)
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation strictly enforces
     * `this.reciprocal().equals(new Decimal64(1.0
     * / this.doubleValue()))`.
     */
    override fun reciprocal(): Decimal64 {
        return Decimal64(1.0 / real)
    }
    /*
     * Methods from the Number abstract class
     */
    /**
     * {@inheritDoc}
     *
     * The current implementation performs casting to a `byte`.
     */
    override fun byteValue(): Byte {
        return real.toByte()
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation performs casting to a `short`.
     */
    override fun shortValue(): Short {
        return real.toShort()
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation performs casting to a `int`.
     */
    override fun intValue(): Int {
        return real.toInt()
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation performs casting to a `long`.
     */
    override fun longValue(): Long {
        return real.toLong()
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation performs casting to a `float`.
     */
    override fun floatValue(): Float {
        return real.toFloat()
    }

    /** {@inheritDoc}  */
    override fun doubleValue(): Double {
        return real
    }
    /*
     * Methods from the Comparable interface.
     */
    /**
     * {@inheritDoc}
     *
     * The current implementation returns the same value as
     * <center> `new Double(this.doubleValue()).compareTo(new
     * Double(o.doubleValue()))` </center>
     *
     * @see Double.compareTo
     */
    override operator fun compareTo(o: Decimal64): Int {
        return java.lang.Double.compare(real, o.real)
    }
    /*
     * Methods from the Object abstract class.
     */
    /** {@inheritDoc}  */
    override fun equals(obj: Any?): Boolean {
        if (obj is Decimal64) {
            return java.lang.Double.doubleToLongBits(real) == java.lang.Double
                .doubleToLongBits(obj.real)
        }
        return false
    }

    /**
     * {@inheritDoc}
     *
     * The current implementation returns the same value as
     * `new Double(this.doubleValue()).hashCode()`
     *
     * @see Double.hashCode
     */
    override fun hashCode(): Int {
        val v: Long = java.lang.Double.doubleToLongBits(real)
        return (v xor (v ushr 32)).toInt()
    }

    /**
     * {@inheritDoc}
     *
     * The returned `String` is equal to
     * `Double.toString(this.doubleValue())`
     *
     * @see Double.toString
     */
    override fun toString(): String {
        return java.lang.Double.toString(real)
    }
    /*
     * Methods inspired by the Double class.
     */
    /**
     * Returns `true` if `this` double precision number is infinite
     * ([Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY]).
     *
     * @return `true` if `this` number is infinite
     */
    val isInfinite: Boolean
        get() = java.lang.Double.isInfinite(real)

    /**
     * Returns `true` if `this` double precision number is
     * Not-a-Number (`NaN`), false otherwise.
     *
     * @return `true` if `this` is `NaN`
     */
    val isNaN: Boolean
        get() = java.lang.Double.isNaN(real)

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun add(a: Double): Decimal64 {
        return Decimal64(real + a)
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun subtract(a: Double): Decimal64 {
        return Decimal64(real - a)
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun multiply(a: Double): Decimal64 {
        return Decimal64(real * a)
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun divide(a: Double): Decimal64 {
        return Decimal64(real / a)
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun remainder(a: Double): Decimal64 {
        return Decimal64(IEEEremainder(real, a))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun remainder(a: Decimal64): Decimal64 {
        return Decimal64(IEEEremainder(real, a.real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun abs(): Decimal64 {
        return Decimal64(abs(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun ceil(): Decimal64 {
        return Decimal64(ceil(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun floor(): Decimal64 {
        return Decimal64(floor(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun rint(): Decimal64 {
        return Decimal64(rint(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun round(): Long {
        return round(real)
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun signum(): Decimal64 {
        return Decimal64(signum(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun copySign(sign: Decimal64): Decimal64 {
        return Decimal64(copySign(real, sign.real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun copySign(sign: Double): Decimal64 {
        return Decimal64(copySign(real, sign))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun scalb(n: Int): Decimal64 {
        return Decimal64(scalb(real, n))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun hypot(y: Decimal64): Decimal64 {
        return Decimal64(hypot(real, y.real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun sqrt(): Decimal64 {
        return Decimal64(sqrt(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun cbrt(): Decimal64 {
        return Decimal64(cbrt(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun rootN(n: Int): Decimal64 {
        return if (real < 0) {
            Decimal64(-pow(-real, 1.0 / n))
        } else {
            Decimal64(pow(real, 1.0 / n))
        }
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun pow(p: Double): Decimal64 {
        return Decimal64(pow(real, p))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun pow(n: Int): Decimal64 {
        return Decimal64(pow(real, n))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun pow(e: Decimal64): Decimal64 {
        return Decimal64(pow(real, e.real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun exp(): Decimal64 {
        return Decimal64(exp(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun expm1(): Decimal64 {
        return Decimal64(expm1(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun log(): Decimal64 {
        return Decimal64(log(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun log1p(): Decimal64 {
        return Decimal64(log1p(real))
    }

    /** Base 10 logarithm.
     * @return base 10 logarithm of the instance
     * @since 3.2
     */
    fun log10(): Decimal64 {
        return Decimal64(log10(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun cos(): Decimal64 {
        return Decimal64(cos(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun sin(): Decimal64 {
        return Decimal64(sin(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun tan(): Decimal64 {
        return Decimal64(tan(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun acos(): Decimal64 {
        return Decimal64(acos(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun asin(): Decimal64 {
        return Decimal64(asin(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun atan(): Decimal64 {
        return Decimal64(atan(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun atan2(x: Decimal64): Decimal64 {
        return Decimal64(atan2(real, x.real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun cosh(): Decimal64 {
        return Decimal64(cosh(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun sinh(): Decimal64 {
        return Decimal64(sinh(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun tanh(): Decimal64 {
        return Decimal64(tanh(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun acosh(): Decimal64 {
        return Decimal64(acosh(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun asinh(): Decimal64 {
        return Decimal64(asinh(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun atanh(): Decimal64 {
        return Decimal64(atanh(real))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    @Throws(DimensionMismatchException::class)
    override fun linearCombination(a: Array<Decimal64>?, b: Array<Decimal64>?): Decimal64 {
        if (a!!.size != b!!.size) {
            throw DimensionMismatchException(a.size, b.size)
        }
        val aDouble = DoubleArray(a.size)
        val bDouble = DoubleArray(b.size)
        for (i in a.indices) {
            aDouble[i] = a[i].real
            bDouble[i] = b[i].real
        }
        return Decimal64(linearCombination(aDouble, bDouble))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    @Throws(DimensionMismatchException::class)
    override fun linearCombination(a: DoubleArray?, b: Array<Decimal64>?): Decimal64 {
        if (a!!.size != b!!.size) {
            throw DimensionMismatchException(a.size, b.size)
        }
        val bDouble = DoubleArray(b.size)
        for (i in a.indices) {
            bDouble[i] = b[i].real
        }
        return Decimal64(linearCombination(a, bDouble))
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun linearCombination(
        a1: Decimal64, b1: Decimal64,
        a2: Decimal64, b2: Decimal64
    ): Decimal64 {
        return Decimal64(
            linearCombination(
                a1.real, b1.real,
                a2.real, b2.real
            )
        )
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun linearCombination(
        a1: Double, b1: Decimal64,
        a2: Double, b2: Decimal64
    ): Decimal64 {
        return Decimal64(
            linearCombination(
                a1, b1.real,
                a2, b2.real
            )
        )
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun linearCombination(
        a1: Decimal64, b1: Decimal64,
        a2: Decimal64, b2: Decimal64,
        a3: Decimal64, b3: Decimal64
    ): Decimal64 {
        return Decimal64(
            linearCombination(
                a1.real, b1.real,
                a2.real, b2.real,
                a3.real, b3.real
            )
        )
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun linearCombination(
        a1: Double, b1: Decimal64,
        a2: Double, b2: Decimal64,
        a3: Double, b3: Decimal64
    ): Decimal64 {
        return Decimal64(
            linearCombination(
                a1, b1.real,
                a2, b2.real,
                a3, b3.real
            )
        )
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun linearCombination(
        a1: Decimal64, b1: Decimal64,
        a2: Decimal64, b2: Decimal64,
        a3: Decimal64, b3: Decimal64,
        a4: Decimal64, b4: Decimal64
    ): Decimal64 {
        return Decimal64(
            linearCombination(
                a1.real, b1.real,
                a2.real, b2.real,
                a3.real, b3.real,
                a4.real, b4.real
            )
        )
    }

    /** {@inheritDoc}
     * @since 3.2
     */
    override fun linearCombination(
        a1: Double, b1: Decimal64,
        a2: Double, b2: Decimal64,
        a3: Double, b3: Decimal64,
        a4: Double, b4: Decimal64
    ): Decimal64 {
        return Decimal64(
            linearCombination(
                a1, b1.real,
                a2, b2.real,
                a3, b3.real,
                a4, b4.real
            )
        )
    }

}
