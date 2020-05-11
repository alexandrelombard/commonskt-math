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

import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalArgumentException

/**
 * This TansformerMap automates the transformation of mixed object types.
 * It provides a means to set NumberTransformers that will be selected
 * based on the Class of the object handed to the Maps
 * `double transform(Object o)` method.
 */
class TransformerMap : NumberTransformer {
    /**
     * A default Number Transformer for Numbers and numeric Strings.
     */
    private var defaultTransformer: NumberTransformer = DefaultTransformer()

    /**
     * The internal Map.
     */
    private val map: MutableMap<KClass<*>, NumberTransformer> = hashMapOf()

    /**
     * Tests if a Class is present in the TransformerMap.
     * @param key Class to check
     * @return true|false
     */
    fun containsClass(key: KClass<*>): Boolean {
        return map.containsKey(key)
    }

    /**
     * Tests if a NumberTransformer is present in the TransformerMap.
     * @param value NumberTransformer to check
     * @return true|false
     */
    fun containsTransformer(value: NumberTransformer): Boolean {
        return map.containsValue(value)
    }

    /**
     * Returns the Transformer that is mapped to a class
     * if mapping is not present, this returns null.
     * @param key The Class of the object
     * @return the mapped NumberTransformer or null.
     */
    fun getTransformer(key: KClass<*>): NumberTransformer? {
        return map.get(key)
    }

    /**
     * Sets a Class to Transformer Mapping in the Map. If
     * the Class is already present, this overwrites that
     * mapping.
     * @param key The Class
     * @param transformer The NumberTransformer
     * @return the replaced transformer if one is present
     */
    fun putTransformer(key: KClass<*>, transformer: NumberTransformer): NumberTransformer? {
        return map.put(key, transformer)
    }

    /**
     * Removes a Class to Transformer Mapping in the Map.
     * @param key The Class
     * @return the removed transformer if one is present or
     * null if none was present.
     */
    fun removeTransformer(key: KClass<*>): NumberTransformer? {
        return map.remove(key)
    }

    /**
     * Clears all the Class to Transformer mappings.
     */
    fun clear() {
        map.clear()
    }

    /**
     * Returns the Set of Classes used as keys in the map.
     * @return Set of Classes
     */
    fun classes(): MutableSet<KClass<*>> {
        return map.keys
    }

    /**
     * Returns the Set of NumberTransformers used as values
     * in the map.
     * @return Set of NumberTransformers
     */
    fun transformers(): MutableCollection<NumberTransformer> {
        return map.values
    }

    /**
     * Attempts to transform the Object against the map of
     * NumberTransformers. Otherwise it returns Double.NaN.
     *
     * @param o the Object to be transformed.
     * @return the double value of the Object.
     * @throws MathIllegalArgumentException if the Object can not be
     * transformed into a Double.
     * @see org.apache.commons.math3.util.NumberTransformer.transform
     */
    override fun transform(o: Any): Double {
        var value = Double.NaN
        if (o is Number || o is String) {
            value = defaultTransformer.transform(o)
        } else {
            val trans = getTransformer(o::class)
            if (trans != null) {
                value = trans.transform(o)
            }
        }
        return value
    }

    /** {@inheritDoc}  */
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other is TransformerMap) {
            val rhs = other as TransformerMap?
            if (defaultTransformer != rhs!!.defaultTransformer) {
                return false
            }
            if (map.size != rhs.map.size) {
                return false
            }
            for (entry in map.entries) {
                if (entry.value != rhs.map[entry.key]) {
                    return false
                }
            }
            return true
        }
        return false
    }

    /** {@inheritDoc}  */
    override fun hashCode(): Int {
        var hash = defaultTransformer.hashCode()
        for (t in map.values) {
            hash = hash * 31 + t.hashCode()
        }
        return hash
    }

    companion object {
        /** Serializable version identifier  */
        private const val serialVersionUID = 4605318041528645258L
    }
}
