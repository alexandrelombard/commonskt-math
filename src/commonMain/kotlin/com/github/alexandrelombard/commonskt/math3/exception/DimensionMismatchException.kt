package com.github.alexandrelombard.commonskt.math3.exception


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
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable


/**
 * Exception to be thrown when two dimensions differ.
 *
 * @since 2.2
 */
class DimensionMismatchException
/**
 * Construct an exception from the mismatched dimensions.
 *
 * @param specific Specific context information pattern.
 * @param wrong Wrong dimension.
 * @param expected Expected dimension.
 */(
    specific: Localizable?,
    wrong: Int,
    /** Correct dimension.  */
    val dimension: Int
) : MathIllegalNumberException(specific, wrong, dimension) {
    /**
     * @return the expected dimension.
     */

    /**
     * Construct an exception from the mismatched dimensions.
     *
     * @param wrong Wrong dimension.
     * @param expected Expected dimension.
     */
    constructor(
        wrong: Int,
        expected: Int
    ) : this(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, wrong, expected) {
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = -8415396756375798143L
    }

}
