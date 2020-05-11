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

import com.github.alexandrelombard.commonskt.math3.exception.DimensionMismatchException
import com.github.alexandrelombard.commonskt.math3.exception.ZeroException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.abs
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.sqrt
import com.github.alexandrelombard.commonskt.math3.utils.MathUtils.hash
import com.github.alexandrelombard.commonskt.math3.utils.Precision
import com.github.alexandrelombard.commonskt.math3.utils.Precision.equals

/**
 * This class implements [
 * quaternions](http://mathworld.wolfram.com/Quaternion.html) (Hamilton's hypercomplex numbers).
 * <br></br>
 * Instance of this class are guaranteed to be immutable.
 *
 * @since 3.1
 */
class Quaternion  {
    /**
     * Gets the first component of the quaternion (scalar part).
     *
     * @return the scalar part.
     */
    /** First component (scalar part).  */
    val q0: Double

    /**
     * Gets the second component of the quaternion (first component
     * of the vector part).
     *
     * @return the first component of the vector part.
     */
    /** Second component (first vector part).  */
    val q1: Double

    /**
     * Gets the third component of the quaternion (second component
     * of the vector part).
     *
     * @return the second component of the vector part.
     */
    /** Third component (second vector part).  */
    val q2: Double

    /**
     * Gets the fourth component of the quaternion (third component
     * of the vector part).
     *
     * @return the third component of the vector part.
     */
    /** Fourth component (third vector part).  */
    val q3: Double

    /**
     * Builds a quaternion from its components.
     *
     * @param a Scalar component.
     * @param b First vector component.
     * @param c Second vector component.
     * @param d Third vector component.
     */
    constructor(
        a: Double,
        b: Double,
        c: Double,
        d: Double
    ) {
        q0 = a
        q1 = b
        q2 = c
        q3 = d
    }

    /**
     * Builds a quaternion from scalar and vector parts.
     *
     * @param scalar Scalar part of the quaternion.
     * @param v Components of the vector part of the quaternion.
     *
     * @throws DimensionMismatchException if the array length is not 3.
     */
    constructor(
        scalar: Double,
        v: DoubleArray
    ) {
        if (v.size != 3) {
            throw DimensionMismatchException(v.size, 3)
        }
        q0 = scalar
        q1 = v.get(0)
        q2 = v.get(1)
        q3 = v.get(2)
    }

    /**
     * Builds a pure quaternion from a vector (assuming that the scalar
     * part is zero).
     *
     * @param v Components of the vector part of the pure quaternion.
     */
    constructor(v: DoubleArray) : this(0.0, v) {}

    /**
     * Returns the conjugate quaternion of the instance.
     *
     * @return the conjugate quaternion
     */
    val conjugate: Quaternion
        get() = Quaternion(q0, -q1, -q2, -q3)

    /**
     * Returns the Hamilton product of the instance by a quaternion.
     *
     * @param q Quaternion.
     * @return the product of this instance with `q`, in that order.
     */
    fun multiply(q: Quaternion): Quaternion {
        return multiply(this, q)
    }

    /**
     * Computes the sum of the instance and another quaternion.
     *
     * @param q Quaternion.
     * @return the sum of this instance and `q`
     */
    fun add(q: Quaternion): Quaternion {
        return add(this, q)
    }

    /**
     * Subtracts a quaternion from the instance.
     *
     * @param q Quaternion.
     * @return the difference between this instance and `q`.
     */
    fun subtract(q: Quaternion): Quaternion {
        return subtract(this, q)
    }

    /**
     * Computes the dot-product of the instance by a quaternion.
     *
     * @param q Quaternion.
     * @return the dot product of this instance and `q`.
     */
    fun dotProduct(q: Quaternion): Double {
        return dotProduct(this, q)
    }

    /**
     * Computes the norm of the quaternion.
     *
     * @return the norm.
     */
    val norm: Double
        get() = sqrt(
            (q0 * q0) + (
                    q1 * q1) + (
                    q2 * q2) + (
                    q3 * q3)
        )

    /**
     * Computes the normalized quaternion (the versor of the instance).
     * The norm of the quaternion must not be zero.
     *
     * @return a normalized quaternion.
     * @throws ZeroException if the norm of the quaternion is zero.
     */
    fun normalize(): Quaternion {
        val norm: Double = norm
        if (norm < Precision.SAFE_MIN) {
            throw ZeroException(LocalizedFormats.NORM, norm)
        }
        return Quaternion(
            q0 / norm,
            q1 / norm,
            q2 / norm,
            q3 / norm
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is Quaternion) {
            val q: Quaternion = other
            return (q0 == q.q0) && (
                    q1 == q.q1) && (
                    q2 == q.q2) && (
                    q3 == q.q3)
        }
        return false
    }

    /**
     * {@inheritDoc}
     */
    override fun hashCode(): Int {
        // "Effective Java" (second edition, p. 47).
        var result: Int = 17
        for (comp: Double in doubleArrayOf(q0, q1, q2, q3)) {
            val c: Int = hash(comp)
            result = 31 * result + c
        }
        return result
    }

    /**
     * Checks whether this instance is equal to another quaternion
     * within a given tolerance.
     *
     * @param q Quaternion with which to compare the current quaternion.
     * @param eps Tolerance.
     * @return `true` if the each of the components are equal
     * within the allowed absolute error.
     */
    fun equals(
        q: Quaternion,
        eps: Double
    ): Boolean {
        return (equals(q0, q.q0, eps) &&
                equals(q1, q.q1, eps) &&
                equals(q2, q.q2, eps) &&
                equals(q3, q.q3, eps))
    }

    /**
     * Checks whether the instance is a unit quaternion within a given
     * tolerance.
     *
     * @param eps Tolerance (absolute error).
     * @return `true` if the norm is 1 within the given tolerance,
     * `false` otherwise
     */
    fun isUnitQuaternion(eps: Double): Boolean {
        return equals(norm, 1.0, eps)
    }

    /**
     * Checks whether the instance is a pure quaternion within a given
     * tolerance.
     *
     * @param eps Tolerance (absolute error).
     * @return `true` if the scalar part of the quaternion is zero.
     */
    fun isPureQuaternion(eps: Double): Boolean {
        return abs(q0) <= eps
    }// The quaternion of rotation (normalized quaternion) q and -q
    // are equivalent (i.e. represent the same rotation).

    /**
     * Returns the polar form of the quaternion.
     *
     * @return the unit quaternion with positive scalar part.
     */
    val positivePolarForm: Quaternion
        get() {
            if (q0 < 0) {
                val unitQ: Quaternion = normalize()
                // The quaternion of rotation (normalized quaternion) q and -q
                // are equivalent (i.e. represent the same rotation).
                return Quaternion(
                    -unitQ.q0,
                    -unitQ.q1,
                    -unitQ.q2,
                    -unitQ.q3
                )
            } else {
                return normalize()
            }
        }

    /**
     * Returns the inverse of this instance.
     * The norm of the quaternion must not be zero.
     *
     * @return the inverse.
     * @throws ZeroException if the norm (squared) of the quaternion is zero.
     */
    val inverse: Quaternion
        get() {
            val squareNorm: Double = (q0 * q0) + (q1 * q1) + (q2 * q2) + (q3 * q3)
            if (squareNorm < Precision.SAFE_MIN) {
                throw ZeroException(LocalizedFormats.NORM, squareNorm)
            }
            return Quaternion(
                q0 / squareNorm,
                -q1 / squareNorm,
                -q2 / squareNorm,
                -q3 / squareNorm
            )
        }

    /**
     * Gets the scalar part of the quaternion.
     *
     * @return the scalar part.
     * @see .getQ0
     */
    val scalarPart: Double
        get() {
            return q0
        }

    /**
     * Gets the three components of the vector part of the quaternion.
     *
     * @return the vector part.
     * @see .getQ1
     * @see .getQ2
     * @see .getQ3
     */
    val vectorPart: DoubleArray
        get() {
            return doubleArrayOf(q1, q2, q3)
        }

    /**
     * Multiplies the instance by a scalar.
     *
     * @param alpha Scalar factor.
     * @return a scaled quaternion.
     */
    fun multiply(alpha: Double): Quaternion {
        return Quaternion(
            alpha * q0,
            alpha * q1,
            alpha * q2,
            alpha * q3
        )
    }

    /**
     * {@inheritDoc}
     */
    override fun toString(): String {
        val sp = " "
        val s = StringBuilder()
        s.append("[")
            .append(q0).append(sp)
            .append(q1).append(sp)
            .append(q2).append(sp)
            .append(q3)
            .append("]")
        return s.toString()
    }

    companion object {
        /** Identity quaternion.  */
        val IDENTITY: Quaternion = Quaternion(1.0, 0.0, 0.0, 0.0)

        /** Zero quaternion.  */
        val ZERO: Quaternion = Quaternion(0.0, 0.0, 0.0, 0.0)

        /** i  */
        val I: Quaternion = Quaternion(0.0, 1.0, 0.0, 0.0)

        /** j  */
        val J: Quaternion = Quaternion(0.0, 0.0, 1.0, 0.0)

        /** k  */
        val K: Quaternion = Quaternion(0.0, 0.0, 0.0, 1.0)

        /** Serializable version identifier.  */
        private val serialVersionUID: Long = 20092012L

        /**
         * Returns the Hamilton product of two quaternions.
         *
         * @param q1 First quaternion.
         * @param q2 Second quaternion.
         * @return the product `q1` and `q2`, in that order.
         */
        fun multiply(q1: Quaternion, q2: Quaternion): Quaternion {
            // Components of the first quaternion.
            val q1a: Double = q1.q0
            val q1b: Double = q1.q1
            val q1c: Double = q1.q2
            val q1d: Double = q1.q3

            // Components of the second quaternion.
            val q2a: Double = q2.q0
            val q2b: Double = q2.q1
            val q2c: Double = q2.q2
            val q2d: Double = q2.q3

            // Components of the product.
            val w: Double = (q1a * q2a) - (q1b * q2b) - (q1c * q2c) - (q1d * q2d)
            val x: Double = (q1a * q2b) + (q1b * q2a) + (q1c * q2d) - q1d * q2c
            val y: Double = (q1a * q2c - q1b * q2d) + (q1c * q2a) + (q1d * q2b)
            val z: Double = q1a * q2d + q1b * q2c - q1c * q2b + q1d * q2a
            return Quaternion(w, x, y, z)
        }

        /**
         * Computes the sum of two quaternions.
         *
         * @param q1 Quaternion.
         * @param q2 Quaternion.
         * @return the sum of `q1` and `q2`.
         */
        fun add(
            q1: Quaternion,
            q2: Quaternion
        ): Quaternion {
            return Quaternion(
                q1.q0 + q2.q0,
                q1.q1 + q2.q1,
                q1.q2 + q2.q2,
                q1.q3 + q2.q3
            )
        }

        /**
         * Subtracts two quaternions.
         *
         * @param q1 First Quaternion.
         * @param q2 Second quaternion.
         * @return the difference between `q1` and `q2`.
         */
        fun subtract(
            q1: Quaternion,
            q2: Quaternion
        ): Quaternion {
            return Quaternion(
                q1.q0 - q2.q0,
                q1.q1 - q2.q1,
                q1.q2 - q2.q2,
                q1.q3 - q2.q3
            )
        }

        /**
         * Computes the dot-product of two quaternions.
         *
         * @param q1 Quaternion.
         * @param q2 Quaternion.
         * @return the dot product of `q1` and `q2`.
         */
        fun dotProduct(
            q1: Quaternion,
            q2: Quaternion
        ): Double {
            return (q1.q0 * q2.q0) + (
                    q1.q1 * q2.q1) + (
                    q1.q2 * q2.q2) + (
                    q1.q3 * q2.q3)
        }
    }
}
