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
package com.github.alexandrelombard.commonskt.math3.exception

import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable

/**
 * Exception to be thrown when two sets of dimensions differ.
 *
 * @since 3.0
 */
class MultiDimensionMismatchException(
    specific: Localizable,
    wrong: Array<Int>,
    expected: Array<Int>
) : MathIllegalArgumentException(specific, wrong, expected) {
    /** Wrong dimensions.  */
    private val wrong: Array<Int>

    /** Correct dimensions.  */
    private val expected: Array<Int>

    /**
     * Construct an exception from the mismatched dimensions.
     *
     * @param wrong Wrong dimensions.
     * @param expected Expected dimensions.
     */
    constructor(
        wrong: Array<Int>,
        expected: Array<Int>
    ) : this(LocalizedFormats.DIMENSIONS_MISMATCH, wrong, expected) {
    }

    /**
     * @return an array containing the wrong dimensions.
     */
    val wrongDimensions: Array<Int>
        get() = wrong.copyOf()

    /**
     * @return an array containing the expected dimensions.
     */
    val expectedDimensions: Array<Int>
        get() = expected.copyOf()

    /**
     * @param index Dimension index.
     * @return the wrong dimension stored at `index`.
     */
    fun getWrongDimension(index: Int): Int {
        return wrong[index]
    }

    /**
     * @param index Dimension index.
     * @return the expected dimension stored at `index`.
     */
    fun getExpectedDimension(index: Int): Int {
        return expected[index]
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = -8415396756375798143L
    }

    /**
     * Construct an exception from the mismatched dimensions.
     *
     * @param specific Message pattern providing the specific context of
     * the error.
     * @param wrong Wrong dimensions.
     * @param expected Expected dimensions.
     */
    init {
        this.wrong = wrong.copyOf()
        this.expected = expected.copyOf()
    }
}
