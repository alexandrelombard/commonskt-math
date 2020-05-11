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
import com.github.alexandrelombard.commonskt.math3.exception.util.ExceptionContext
import com.github.alexandrelombard.commonskt.math3.exception.util.ExceptionContextProvider

/**
 * Base class for all unsupported features.
 * It is used for all the exceptions that have the semantics of the standard
 * {@link UnsupportedOperationException}, but must also provide a localized
 * message.
 *
 * @since 2.2
 */
class MathUnsupportedOperationException
/**
 * @param pattern Message pattern providing the specific context of
 * the error.
 * @param args Arguments.
 */(pattern: Localizable = LocalizedFormats.UNSUPPORTED_OPERATION, vararg args: Any) :
    UnsupportedOperationException(), ExceptionContextProvider {

    /** Context. */
    override val context: ExceptionContext = ExceptionContext(this)

    init {
        context.addMessage(pattern, args)
    }

    override val message: String?
        get() = context.getMessage()

    val localizedMessage: String
        get() = context.getLocalizedMessage()

    companion object {
        /** Serializable version Id. */
        private val serialVersionUID = -6024911025449780478L
    }
}
