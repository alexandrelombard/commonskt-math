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
 * All conditions checks that fail due to a {@code null} argument must throw
 * this exception.
 * This class is meant to signal a precondition violation ("null is an illegal
 * argument") and so does not extend the standard {@code NullPointerException}.
 * Propagation of {@code NullPointerException} from within Commons-Math is
 * construed to be a bug.
 *
 * @since 2.2
 */
@Deprecated("Kotlin null-safety nullifies the benefits of this exception")
class NullArgumentException
/**
 * @param pattern Message pattern providing the specific context of
 * the error.
 * @param arguments Values for replacing the placeholders in {@code pattern}.
 */(pattern: Localizable = LocalizedFormats.NULL_NOT_ALLOWED, vararg arguments: Any) :
    MathIllegalArgumentException(pattern, arguments) {

    companion object {
        /** Serializable version Id. */
        private const val serialVersionUID = -6024911025449780478L
    }
}
