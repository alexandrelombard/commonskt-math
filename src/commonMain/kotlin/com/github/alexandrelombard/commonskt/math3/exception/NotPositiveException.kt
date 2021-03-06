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


/**
 * Exception to be thrown when the argument is negative.
 *
 * @since 2.2
 */
class NotPositiveException : NumberIsTooSmallException {
    /**
     * Construct the exception.
     *
     * @param value Argument.
     */
    constructor(value: Number) : super(value, INTEGER_ZERO, true) {}

    /**
     * Construct the exception with a specific context.
     *
     * @param specific Specific context where the error occurred.
     * @param value Argument.
     */
    constructor(
        specific: Localizable,
        value: Number
    ) : super(specific, value, INTEGER_ZERO, true) {
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = -2250556892093726375L
    }
}
