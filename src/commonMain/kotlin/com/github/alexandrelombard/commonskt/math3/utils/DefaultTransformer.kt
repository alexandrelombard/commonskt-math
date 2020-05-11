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

import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalArgumentException
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats

/**
 * A Default NumberTransformer for java.lang.Numbers and Numeric Strings. This
 * provides some simple conversion capabilities to turn any java.lang.Number
 * into a primitive double or to turn a String representation of a Number into
 * a double.
 *
 */
class DefaultTransformer : NumberTransformer {
    /**
     * @param o  the object that gets transformed.
     * @return a double primitive representation of the Object o.
     * @throws NullArgumentException if Object `o` is `null`.
     * @throws MathIllegalArgumentException if Object `o`
     * cannot successfully be transformed
     * @see [Commons Collections Transformer](http://commons.apache.org/collections/api-release/org/apache/commons/collections/Transformer.html)
     */
    override fun transform(o: Any): Double {
        return if (o is Number) {
            o.toDouble()
        } else try {
            o.toString().toDouble()
        } catch (e: NumberFormatException) {
            throw MathIllegalArgumentException(
                LocalizedFormats.CANNOT_TRANSFORM_TO_DOUBLE,
                o.toString()
            )
        }
    }

    /** {@inheritDoc}  */
    override fun equals(other: Any?): Boolean {
        return if (this === other) {
            true
        } else other is DefaultTransformer
    }

    /** {@inheritDoc}  */
    override fun hashCode(): Int {
        // some arbitrary number ...
        return 401993047
    }

    companion object {
        /** Serializable version identifier  */
        private const val serialVersionUID = 4019938025047800455L
    }
}
