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

import com.github.alexandrelombard.commonskt.math3.Field
import com.github.alexandrelombard.commonskt.math3.FieldElement
import com.github.alexandrelombard.commonskt.math3.utils.Decimal64
import kotlin.reflect.KClass

/**
 * The field of double precision floating-point numbers.
 *
 * @since 3.1
 * @see Decimal64
 */
class Decimal64Field
/** Default constructor.  */
private constructor() : Field<Decimal64> {
    /** {@inheritDoc}  */
    override val zero: Decimal64
        get() = Decimal64.ZERO

    /** {@inheritDoc}  */
    override val one: Decimal64
        get() = Decimal64.ONE

    /** {@inheritDoc}  */
    override val runtimeClass: KClass<out FieldElement<Decimal64>>
        get() = Decimal64::class

    companion object {
        /**
         * Returns the unique instance of this class.
         *
         * @return the unique instance of this class
         */
        /** The unique instance of this class.  */
        val instance = Decimal64Field()

    }
}