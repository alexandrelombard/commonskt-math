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
package com.github.alexandrelombard.commonskt.math3.utils

import kotlin.reflect.KClass

import com.github.alexandrelombard.commonskt.math3.Field
import com.github.alexandrelombard.commonskt.math3.FieldElement

/**
 * Representation of real numbers with arbitrary precision field.
 *
 *
 * This class is a singleton.
 *
 * @see BigReal
 *
 * @since 2.0
 */
@ExperimentalStdlibApi
class BigRealField
/** Private constructor for the singleton.
 */
private constructor() : Field<BigReal> {
    /** {@inheritDoc}  */
    override val one: BigReal
        get() = BigReal.ONE

    /** {@inheritDoc}  */
    override val zero: BigReal
        get() = BigReal.ZERO

    /** {@inheritDoc}  */
    override val runtimeClass: KClass<out FieldElement<BigReal>>
        get() = BigReal::class
    // CHECKSTYLE: stop HideUtilityClassConstructor
    /** Holder for the instance.
     *
     * We use here the Initialization On Demand Holder Idiom.
     */
    private object LazyHolder {
        /** Cached field instance.  */
        val INSTANCE = BigRealField()
    }
    // CHECKSTYLE: resume HideUtilityClassConstructor
    /** Handle deserialization of the singleton.
     * @return the singleton instance
     */
    private fun readResolve(): Any {
        // return the singleton instance
        return LazyHolder.INSTANCE
    }

    companion object {
        /** Serializable version identifier  */
        private const val serialVersionUID = 4756431066541037559L

        /** Get the unique instance.
         * @return the unique instance
         */
        val instance: BigRealField
            get() = LazyHolder.INSTANCE
    }
}
