package com.github.alexandrelombard.commonskt.math3.utils
import com.github.alexandrelombard.commonskt.math3.exception.DimensionMismatchException


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
import org.apache.commons.math3.exception.DimensionMismatchException


/** Class used to compute the classical functions tables.
 * @since 3.0
 */
internal object FastMathCalc {
    /**
     * 0x40000000 - used to split a double into two parts, both with the low order bits cleared.
     * Equivalent to 2^30.
     */
    private const val HEX_40000000 = 0x40000000L // 1073741824L

    /** Factorial table, for Taylor series expansions. 0!, 1!, 2!, ... 19!  */
    private val FACT = doubleArrayOf(
        +1.0,  // 0
        +1.0,  // 1
        +2.0,  // 2
        +6.0,  // 3
        +24.0,  // 4
        +120.0,  // 5
        +720.0,  // 6
        +5040.0,  // 7
        +40320.0,  // 8
        +362880.0,  // 9
        +3628800.0,  // 10
        +39916800.0,  // 11
        +479001600.0,  // 12
        +6227020800.0,  // 13
        +87178291200.0,  // 14
        +1307674368000.0,  // 15
        +20922789888000.0,  // 16
        +355687428096000.0,  // 17
        +6402373705728000.0,  // 18
        +121645100408832000.0
    )

    /** Coefficients for slowLog.  */
    private val LN_SPLIT_COEF = arrayOf(
        doubleArrayOf(2.0, 0.0),
        doubleArrayOf(0.6666666269302368, 3.9736429850260626E-8),
        doubleArrayOf(0.3999999761581421, 2.3841857910019882E-8),
        doubleArrayOf(0.2857142686843872, 1.7029898543501842E-8),
        doubleArrayOf(0.2222222089767456, 1.3245471311735498E-8),
        doubleArrayOf(0.1818181574344635, 2.4384203044354907E-8),
        doubleArrayOf(0.1538461446762085, 9.140260083262505E-9),
        doubleArrayOf(0.13333332538604736, 9.220590270857665E-9),
        doubleArrayOf(0.11764700710773468, 1.2393345855018391E-8),
        doubleArrayOf(0.10526403784751892, 8.251545029714408E-9),
        doubleArrayOf(0.0952233225107193, 1.2675934823758863E-8),
        doubleArrayOf(0.08713622391223907, 1.1430250008909141E-8),
        doubleArrayOf(0.07842259109020233, 2.404307984052299E-9),
        doubleArrayOf(0.08371849358081818, 1.176342548272881E-8),
        doubleArrayOf(0.030589580535888672, 1.2958646899018938E-9),
        doubleArrayOf(0.14982303977012634, 1.225743062930824E-8)
    )

    /** Table start declaration.  */
    private const val TABLE_START_DECL = "    {"

    /** Table end declaration.  */
    private const val TABLE_END_DECL = "    };"

    /** Build the sine and cosine tables.
     * @param SINE_TABLE_A table of the most significant part of the sines
     * @param SINE_TABLE_B table of the least significant part of the sines
     * @param COSINE_TABLE_A table of the most significant part of the cosines
     * @param COSINE_TABLE_B table of the most significant part of the cosines
     * @param SINE_TABLE_LEN length of the tables
     * @param TANGENT_TABLE_A table of the most significant part of the tangents
     * @param TANGENT_TABLE_B table of the most significant part of the tangents
     */
    private fun buildSinCosTables(
        SINE_TABLE_A: DoubleArray, SINE_TABLE_B: DoubleArray,
        COSINE_TABLE_A: DoubleArray, COSINE_TABLE_B: DoubleArray,
        SINE_TABLE_LEN: Int, TANGENT_TABLE_A: DoubleArray, TANGENT_TABLE_B: DoubleArray
    ) {
        val result = DoubleArray(2)

        /* Use taylor series for 0 <= x <= 6/8 */for (i in 0..6) {
            val x = i / 8.0
            slowSin(x, result)
            SINE_TABLE_A[i] = result[0]
            SINE_TABLE_B[i] = result[1]
            slowCos(x, result)
            COSINE_TABLE_A[i] = result[0]
            COSINE_TABLE_B[i] = result[1]
        }

        /* Use angle addition formula to complete table to 13/8, just beyond pi/2 */for (i in 7 until SINE_TABLE_LEN) {
            val xs = DoubleArray(2)
            val ys = DoubleArray(2)
            val `as` = DoubleArray(2)
            val bs = DoubleArray(2)
            val temps = DoubleArray(2)
            if (i and 1 == 0) {
                // Even, use double angle
                xs[0] = SINE_TABLE_A[i / 2]
                xs[1] = SINE_TABLE_B[i / 2]
                ys[0] = COSINE_TABLE_A[i / 2]
                ys[1] = COSINE_TABLE_B[i / 2]

                /* compute sine */splitMult(xs, ys, result)
                SINE_TABLE_A[i] = result[0] * 2.0
                SINE_TABLE_B[i] = result[1] * 2.0

                /* Compute cosine */splitMult(ys, ys, `as`)
                splitMult(xs, xs, temps)
                temps[0] = -temps[0]
                temps[1] = -temps[1]
                splitAdd(`as`, temps, result)
                COSINE_TABLE_A[i] = result[0]
                COSINE_TABLE_B[i] = result[1]
            } else {
                xs[0] = SINE_TABLE_A[i / 2]
                xs[1] = SINE_TABLE_B[i / 2]
                ys[0] = COSINE_TABLE_A[i / 2]
                ys[1] = COSINE_TABLE_B[i / 2]
                `as`[0] = SINE_TABLE_A[i / 2 + 1]
                `as`[1] = SINE_TABLE_B[i / 2 + 1]
                bs[0] = COSINE_TABLE_A[i / 2 + 1]
                bs[1] = COSINE_TABLE_B[i / 2 + 1]

                /* compute sine */splitMult(xs, bs, temps)
                splitMult(ys, `as`, result)
                splitAdd(result, temps, result)
                SINE_TABLE_A[i] = result[0]
                SINE_TABLE_B[i] = result[1]

                /* Compute cosine */splitMult(ys, bs, result)
                splitMult(xs, `as`, temps)
                temps[0] = -temps[0]
                temps[1] = -temps[1]
                splitAdd(result, temps, result)
                COSINE_TABLE_A[i] = result[0]
                COSINE_TABLE_B[i] = result[1]
            }
        }

        /* Compute tangent = sine/cosine */for (i in 0 until SINE_TABLE_LEN) {
            val xs = DoubleArray(2)
            val ys = DoubleArray(2)
            val `as` = DoubleArray(2)
            `as`[0] = COSINE_TABLE_A[i]
            `as`[1] = COSINE_TABLE_B[i]
            splitReciprocal(`as`, ys)
            xs[0] = SINE_TABLE_A[i]
            xs[1] = SINE_TABLE_B[i]
            splitMult(xs, ys, `as`)
            TANGENT_TABLE_A[i] = `as`[0]
            TANGENT_TABLE_B[i] = `as`[1]
        }
    }

    /**
     * For x between 0 and pi/4 compute cosine using Talor series
     * cos(x) = 1 - x^2/2! + x^4/4! ...
     * @param x number from which cosine is requested
     * @param result placeholder where to put the result in extended precision
     * (may be null)
     * @return cos(x)
     */
    fun slowCos(x: Double, result: DoubleArray?): Double {
        val xs = DoubleArray(2)
        val ys = DoubleArray(2)
        val facts = DoubleArray(2)
        val `as` = DoubleArray(2)
        split(x, xs)
        ys[1] = 0.0
        ys[0] = ys[1]
        for (i in FACT.indices.reversed()) {
            splitMult(xs, ys, `as`)
            ys[0] = `as`[0]
            ys[1] = `as`[1]
            if (i and 1 != 0) { // skip odd entries
                continue
            }
            split(FACT[i], `as`)
            splitReciprocal(`as`, facts)
            if (i and 2 != 0) { // alternate terms are negative
                facts[0] = -facts[0]
                facts[1] = -facts[1]
            }
            splitAdd(ys, facts, `as`)
            ys[0] = `as`[0]
            ys[1] = `as`[1]
        }
        if (result != null) {
            result[0] = ys[0]
            result[1] = ys[1]
        }
        return ys[0] + ys[1]
    }

    /**
     * For x between 0 and pi/4 compute sine using Taylor expansion:
     * sin(x) = x - x^3/3! + x^5/5! - x^7/7! ...
     * @param x number from which sine is requested
     * @param result placeholder where to put the result in extended precision
     * (may be null)
     * @return sin(x)
     */
    fun slowSin(x: Double, result: DoubleArray?): Double {
        val xs = DoubleArray(2)
        val ys = DoubleArray(2)
        val facts = DoubleArray(2)
        val `as` = DoubleArray(2)
        split(x, xs)
        ys[1] = 0.0
        ys[0] = ys[1]
        for (i in FACT.indices.reversed()) {
            splitMult(xs, ys, `as`)
            ys[0] = `as`[0]
            ys[1] = `as`[1]
            if (i and 1 == 0) { // Ignore even numbers
                continue
            }
            split(FACT[i], `as`)
            splitReciprocal(`as`, facts)
            if (i and 2 != 0) { // alternate terms are negative
                facts[0] = -facts[0]
                facts[1] = -facts[1]
            }
            splitAdd(ys, facts, `as`)
            ys[0] = `as`[0]
            ys[1] = `as`[1]
        }
        if (result != null) {
            result[0] = ys[0]
            result[1] = ys[1]
        }
        return ys[0] + ys[1]
    }

    /**
     * For x between 0 and 1, returns exp(x), uses extended precision
     * @param x argument of exponential
     * @param result placeholder where to place exp(x) split in two terms
     * for extra precision (i.e. exp(x) = result[0] + result[1]
     * @return exp(x)
     */
    fun slowexp(x: Double, result: DoubleArray?): Double {
        val xs = DoubleArray(2)
        val ys = DoubleArray(2)
        val facts = DoubleArray(2)
        val `as` = DoubleArray(2)
        split(x, xs)
        ys[1] = 0.0
        ys[0] = ys[1]
        for (i in FACT.indices.reversed()) {
            splitMult(xs, ys, `as`)
            ys[0] = `as`[0]
            ys[1] = `as`[1]
            split(FACT[i], `as`)
            splitReciprocal(`as`, facts)
            splitAdd(ys, facts, `as`)
            ys[0] = `as`[0]
            ys[1] = `as`[1]
        }
        if (result != null) {
            result[0] = ys[0]
            result[1] = ys[1]
        }
        return ys[0] + ys[1]
    }

    /** Compute split[0], split[1] such that their sum is equal to d,
     * and split[0] has its 30 least significant bits as zero.
     * @param d number to split
     * @param split placeholder where to place the result
     */
    private fun split(d: Double, split: DoubleArray) {
        if (d < 8e298 && d > -8e298) {
            val a = d * HEX_40000000
            split[0] = d + a - a
            split[1] = d - split[0]
        } else {
            val a = d * 9.31322574615478515625E-10
            split[0] = (d + a - d) * HEX_40000000
            split[1] = d - split[0]
        }
    }

    /** Recompute a split.
     * @param a input/out array containing the split, changed
     * on output
     */
    private fun resplit(a: DoubleArray) {
        val c = a[0] + a[1]
        val d = -(c - a[0] - a[1])
        if (c < 8e298 && c > -8e298) { // MAGIC NUMBER
            val z = c * HEX_40000000
            a[0] = c + z - z
            a[1] = c - a[0] + d
        } else {
            val z = c * 9.31322574615478515625E-10
            a[0] = (c + z - c) * HEX_40000000
            a[1] = c - a[0] + d
        }
    }

    /** Multiply two numbers in split form.
     * @param a first term of multiplication
     * @param b second term of multiplication
     * @param ans placeholder where to put the result
     */
    private fun splitMult(a: DoubleArray, b: DoubleArray, ans: DoubleArray) {
        ans[0] = a[0] * b[0]
        ans[1] = a[0] * b[1] + a[1] * b[0] + a[1] * b[1]

        /* Resplit */resplit(ans)
    }

    /** Add two numbers in split form.
     * @param a first term of addition
     * @param b second term of addition
     * @param ans placeholder where to put the result
     */
    private fun splitAdd(a: DoubleArray, b: DoubleArray, ans: DoubleArray) {
        ans[0] = a[0] + b[0]
        ans[1] = a[1] + b[1]
        resplit(ans)
    }

    /** Compute the reciprocal of in.  Use the following algorithm.
     * in = c + d.
     * want to find x + y such that x+y = 1/(c+d) and x is much
     * larger than y and x has several zero bits on the right.
     *
     * Set b = 1/(2^22),  a = 1 - b.  Thus (a+b) = 1.
     * Use following identity to compute (a+b)/(c+d)
     *
     * (a+b)/(c+d)  =   a/c   +    (bc - ad) / (c^2 + cd)
     * set x = a/c  and y = (bc - ad) / (c^2 + cd)
     * This will be close to the right answer, but there will be
     * some rounding in the calculation of X.  So by carefully
     * computing 1 - (c+d)(x+y) we can compute an error and
     * add that back in.   This is done carefully so that terms
     * of similar size are subtracted first.
     * @param in initial number, in split form
     * @param result placeholder where to put the result
     */
    fun splitReciprocal(`in`: DoubleArray, result: DoubleArray) {
        val b = 1.0 / 4194304.0
        val a = 1.0 - b
        if (`in`[0] == 0.0) {
            `in`[0] = `in`[1]
            `in`[1] = 0.0
        }
        result[0] = a / `in`[0]
        result[1] = (b * `in`[0] - a * `in`[1]) / (`in`[0] * `in`[0] + `in`[0] * `in`[1])
        if (result[1] != result[1]) { // can happen if result[1] is NAN
            result[1] = 0.0
        }

        /* Resplit */resplit(result)
        for (i in 0..1) {
            /* this may be overkill, probably once is enough */
            var err = 1.0 - result[0] * `in`[0] - result[0] * `in`[1] - result[1] * `in`[0] - result[1] * `in`[1]
            /*err = 1.0 - err; */err *= result[0] + result[1]
            /*printf("err = %16e\n", err); */result[1] += err
        }
    }

    /** Compute (a[0] + a[1]) * (b[0] + b[1]) in extended precision.
     * @param a first term of the multiplication
     * @param b second term of the multiplication
     * @param result placeholder where to put the result
     */
    private fun quadMult(a: DoubleArray, b: DoubleArray, result: DoubleArray) {
        val xs = DoubleArray(2)
        val ys = DoubleArray(2)
        val zs = DoubleArray(2)

        /* a[0] * b[0] */split(a[0], xs)
        split(b[0], ys)
        splitMult(xs, ys, zs)
        result[0] = zs[0]
        result[1] = zs[1]

        /* a[0] * b[1] */split(b[1], ys)
        splitMult(xs, ys, zs)
        var tmp = result[0] + zs[0]
        result[1] -= tmp - result[0] - zs[0]
        result[0] = tmp
        tmp = result[0] + zs[1]
        result[1] -= tmp - result[0] - zs[1]
        result[0] = tmp

        /* a[1] * b[0] */split(a[1], xs)
        split(b[0], ys)
        splitMult(xs, ys, zs)
        tmp = result[0] + zs[0]
        result[1] -= tmp - result[0] - zs[0]
        result[0] = tmp
        tmp = result[0] + zs[1]
        result[1] -= tmp - result[0] - zs[1]
        result[0] = tmp

        /* a[1] * b[0] */split(a[1], xs)
        split(b[1], ys)
        splitMult(xs, ys, zs)
        tmp = result[0] + zs[0]
        result[1] -= tmp - result[0] - zs[0]
        result[0] = tmp
        tmp = result[0] + zs[1]
        result[1] -= tmp - result[0] - zs[1]
        result[0] = tmp
    }

    /** Compute exp(p) for a integer p in extended precision.
     * @param p integer whose exponential is requested
     * @param result placeholder where to put the result in extended precision
     * @return exp(p) in standard precision (equal to result[0] + result[1])
     */
    fun expint(p: Int, result: DoubleArray?): Double {
        //double x = M_E;
        var p = p
        val xs = DoubleArray(2)
        val `as` = DoubleArray(2)
        val ys = DoubleArray(2)
        //split(x, xs);
        //xs[1] = (double)(2.7182818284590452353602874713526625L - xs[0]);
        //xs[0] = 2.71827697753906250000;
        //xs[1] = 4.85091998273542816811e-06;
        //xs[0] = Double.longBitsToDouble(0x4005bf0800000000L);
        //xs[1] = Double.longBitsToDouble(0x3ed458a2bb4a9b00L);

        /* E */xs[0] = 2.718281828459045
        xs[1] = 1.4456468917292502E-16
        split(1.0, ys)
        while (p > 0) {
            if (p and 1 != 0) {
                quadMult(ys, xs, `as`)
                ys[0] = `as`[0]
                ys[1] = `as`[1]
            }
            quadMult(xs, xs, `as`)
            xs[0] = `as`[0]
            xs[1] = `as`[1]
            p = p shr 1
        }
        if (result != null) {
            result[0] = ys[0]
            result[1] = ys[1]
            resplit(result)
        }
        return ys[0] + ys[1]
    }

    /** xi in the range of [1, 2].
     * 3        5        7
     * x+1           /          x        x        x          \
     * ln ----- =   2 *  |  x  +   ----  +  ----  +  ---- + ...  |
     * 1-x           \          3        5        7          /
     *
     * So, compute a Remez approximation of the following function
     *
     * ln ((sqrt(x)+1)/(1-sqrt(x)))  /  x
     *
     * This will be an even function with only positive coefficents.
     * x is in the range [0 - 1/3].
     *
     * Transform xi for input to the above function by setting
     * x = (xi-1)/(xi+1).   Input to the polynomial is x^2, then
     * the result is multiplied by x.
     * @param xi number from which log is requested
     * @return log(xi)
     */
    fun slowLog(xi: Double): DoubleArray {
        val x = DoubleArray(2)
        val x2 = DoubleArray(2)
        val y = DoubleArray(2)
        val a = DoubleArray(2)
        split(xi, x)

        /* Set X = (x-1)/(x+1) */x[0] += 1.0
        resplit(x)
        splitReciprocal(x, a)
        x[0] -= 2.0
        resplit(x)
        splitMult(x, a, y)
        x[0] = y[0]
        x[1] = y[1]

        /* Square X -> X2*/splitMult(x, x, x2)


        //x[0] -= 1.0;
        //resplit(x);
        y[0] = LN_SPLIT_COEF[LN_SPLIT_COEF.size - 1][0]
        y[1] = LN_SPLIT_COEF[LN_SPLIT_COEF.size - 1][1]
        for (i in LN_SPLIT_COEF.size - 2 downTo 0) {
            splitMult(y, x2, a)
            y[0] = a[0]
            y[1] = a[1]
            splitAdd(y, LN_SPLIT_COEF[i], a)
            y[0] = a[0]
            y[1] = a[1]
        }
        splitMult(y, x, a)
        y[0] = a[0]
        y[1] = a[1]
        return y
    }

    /**
     * Print an array.
     * @param out text output stream where output should be printed
     * @param name array name
     * @param expectedLen expected length of the array
     * @param array2d array data
     */
    fun printarray(
        out: PrintStream,
        name: String?,
        expectedLen: Int,
        array2d: Array<DoubleArray>
    ) {
        out.println(name)
        checkLen(expectedLen, array2d.size)
        out.println("$TABLE_START_DECL ")
        var i = 0
        for (array in array2d) { // "double array[]" causes PMD parsing error
            out.print("        {")
            for (d in array) { // assume inner array has very few entries
                out.printf("%-25.25s", format(d)) // multiple entries per line
            }
            out.println("}, // " + i++)
        }
        out.println(TABLE_END_DECL)
    }

    /**
     * Print an array.
     * @param out text output stream where output should be printed
     * @param name array name
     * @param expectedLen expected length of the array
     * @param array array data
     */
    fun printarray(out: PrintStream, name: String, expectedLen: Int, array: DoubleArray) {
        out.println("$name=")
        checkLen(expectedLen, array.size)
        out.println(TABLE_START_DECL)
        for (d in array) {
            out.printf("        %s%n", format(d)) // one entry per line
        }
        out.println(TABLE_END_DECL)
    }

    /** Format a double.
     * @param d double number to format
     * @return formatted number
     */
    fun format(d: Double): String {
        return if (d != d) {
            "Double.NaN,"
        } else {
            (if (d >= 0) "+" else "") + java.lang.Double.toString(d) + "d,"
        }
    }

    /**
     * Check two lengths are equal.
     * @param expectedLen expected length
     * @param actual actual length
     * @exception DimensionMismatchException if the two lengths are not equal
     */
    @Throws(DimensionMismatchException::class)
    private fun checkLen(expectedLen: Int, actual: Int) {
        if (expectedLen != actual) {
            throw DimensionMismatchException(actual, expectedLen)
        }
    }
}
