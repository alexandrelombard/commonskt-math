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
package com.github.alexandrelombard.commonskt.math3

import kotlin.reflect.KClass

/**
 * Interface representing a [field](http://mathworld.wolfram.com/Field.html).
 *
 *
 * Classes implementing this interface will often be singletons.
 *
 * @param <T> the type of the field elements
 * @see FieldElement
 *
 * @since 2.0
</T> */
interface Field<T> {
    /** Get the additive identity of the field.
     *
     *
     * The additive identity is the element e<sub>0</sub> of the field such that
     * for all elements a of the field, the equalities a + e<sub>0</sub> =
     * e<sub>0</sub> + a = a hold.
     *
     * @return additive identity of the field
     */
    val zero: T

    /** Get the multiplicative identity of the field.
     *
     *
     * The multiplicative identity is the element e<sub>1</sub> of the field such that
     * for all elements a of the field, the equalities a  e<sub>1</sub> =
     * e<sub>1</sub>  a = a hold.
     *
     * @return multiplicative identity of the field
     */
    val one: T

    /**
     * Returns the runtime class of the FieldElement.
     *
     * @return The `Class` object that represents the runtime
     * class of this object.
     */
    val runtimeClass: KClass<out FieldElement<T>>
}
