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
import com.github.alexandrelombard.commonskt.math3.util.MathArrays

/**
 * Exception to be thrown when the a sequence of values is not monotonically
 * increasing or decreasing.
 *
 * @since 2.2 (name changed to "NonMonotonicSequenceException" in 3.0)
 */
class NonMonotonicSequenceException constructor(
    wrong: Number,
    previous: Number,
    index: Int,
    direction: MathArrays.OrderDirection = MathArrays.OrderDirection.INCREASING,
    strict: Boolean = true
) :
    MathIllegalNumberException(
        if (direction === MathArrays.OrderDirection.INCREASING) if (strict) LocalizedFormats.NOT_STRICTLY_INCREASING_SEQUENCE else LocalizedFormats.NOT_INCREASING_SEQUENCE else if (strict) LocalizedFormats.NOT_STRICTLY_DECREASING_SEQUENCE else LocalizedFormats.NOT_DECREASING_SEQUENCE,
        wrong, previous, index, index - 1
    ) {

    /**
     * Direction (positive for increasing, negative for decreasing).
     */
    private val direction: MathArrays.OrderDirection

    /**
     * @return `true` is the sequence should be strictly monotonic.
     */
    /**
     * Whether the sequence must be strictly increasing or decreasing.
     */
    val strict: Boolean

    /**
     * Get the index of the wrong value.
     *
     * @return the current index.
     */
    /**
     * Index of the wrong value.
     */
    val index: Int

    /**
     * @return the previous value.
     */
    /**
     * Previous value.
     */
    val previous: Number

    /**
     * @return the order direction.
     */
    fun getDirection(): MathArrays.OrderDirection {
        return direction
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = 3596849179428944575L
    }
    /**
     * Construct the exception.
     *
     * @param wrong Value that did not match the requirements.
     * @param previous Previous value in the sequence.
     * @param index Index of the value that did not match the requirements.
     * @param direction Strictly positive for a sequence required to be
     * increasing, negative (or zero) for a decreasing sequence.
     * @param strict Whether the sequence must be strictly increasing or
     * decreasing.
     */
    /**
     * Construct the exception.
     * This constructor uses default values assuming that the sequence should
     * have been strictly increasing.
     *
     * @param wrong Value that did not match the requirements.
     * @param previous Previous value in the sequence.
     * @param index Index of the value that did not match the requirements.
     */
    init {
        this.direction = direction
        this.strict = strict
        this.index = index
        this.previous = previous
    }
}
