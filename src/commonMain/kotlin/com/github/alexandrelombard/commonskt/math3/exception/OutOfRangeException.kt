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
package org.apache.commons.math3.exception

import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalNumberException
import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats

/**
 * Exception to be thrown when some argument is out of range.
 *
 * @since 2.2
 */
class OutOfRangeException
/**
 * Construct an exception from the mismatched dimensions with a
 * specific context information.
 *
 * @param specific Context information.
 * @param wrong Requested value.
 * @param lo Lower bound.
 * @param hi Higher bound.
 */(
    specific: Localizable,
    wrong: Number,
    /** Lower bound.  */
    val lo: Number,
    /** Higher bound.  */
    val hi: Number
) : MathIllegalNumberException(specific, wrong, lo, hi) {
    /**
     * @return the lower bound.
     */

    /**
     * @return the higher bound.
     */

    /**
     * Construct an exception from the mismatched dimensions.
     *
     * @param wrong Requested value.
     * @param lo Lower bound.
     * @param hi Higher bound.
     */
    constructor(
        wrong: Number,
        lo: Number,
        hi: Number
    ) : this(LocalizedFormats.OUT_OF_RANGE_SIMPLE, wrong, lo, hi)

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = 111601815794403609L
    }

}