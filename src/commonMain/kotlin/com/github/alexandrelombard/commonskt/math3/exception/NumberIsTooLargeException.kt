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

import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats

/**
 * Exception to be thrown when a number is too large.
 *
 * @since 2.2
 */
class NumberIsTooLargeException
/**
 * Construct the exception with a specific context.
 *
 * @param specific Specific context pattern.
 * @param wrong Value that is larger than the maximum.
 * @param max Maximum.
 * @param boundIsAllowed if true the maximum is included in the allowed range.
 */(
    specific: Localizable,
    wrong: Number,
    /**
     * Higher bound.
     */
    val max: Number?,
    /**
     * Whether the maximum is included in the allowed range.
     */
    val boundIsAllowed: Boolean
) :
    MathIllegalNumberException(specific, wrong, max) {
    /**
     * @return the maximum.
     */

    /**
     * @return `true` if the maximum is included in the allowed range.
     */

    /**
     * Construct the exception.
     *
     * @param wrong Value that is larger than the maximum.
     * @param max Maximum.
     * @param boundIsAllowed if true the maximum is included in the allowed range.
     */
    constructor(
        wrong: Number,
        max: Number,
        boundIsAllowed: Boolean
    ) : this(
        if (boundIsAllowed) LocalizedFormats.NUMBER_TOO_LARGE else LocalizedFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED,
        wrong, max, boundIsAllowed
    ) {
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = 4330003017885151975L
    }

}
