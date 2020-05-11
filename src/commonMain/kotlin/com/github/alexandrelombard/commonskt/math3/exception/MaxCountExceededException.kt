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
 * Exception to be thrown when some counter maximum value is exceeded.
 *
 * @since 3.0
 */
class MaxCountExceededException(
    specific: Localizable,
    max: Number,
    vararg args: Any
) : MathIllegalStateException() {

    /**
     * Maximum number of evaluations.
     */
    val max: Number

    /**
     * Construct the exception.
     *
     * @param max Maximum.
     */
    constructor(max: Number) : this(LocalizedFormats.MAX_COUNT_EXCEEDED, max)

    /**
     * Construct the exception with a specific context.
     *
     * @param specific Specific context pattern.
     * @param max Maximum.
     * @param args Additional arguments.
     */
    init {
        getContext().addMessage(specific, max, args)
        this.max = max
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = 4330003017885151975L
    }
}
