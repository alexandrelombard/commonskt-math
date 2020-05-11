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
 * Base class for all exceptions that signal that the process
 * throwing the exception is in a state that does not comply with
 * the set of states that it is designed to be in.
 *
 * @since 2.2
 */
open class MathIllegalStateException: IllegalStateException, ExceptionContextProvider {

    /** Context. */
    final override val context: ExceptionContext = ExceptionContext(this)

    /**
     * Simple constructor.
     *
     * @param pattern Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    constructor(pattern: Localizable = LocalizedFormats.ILLEGAL_STATE, vararg args: Any) {
        context.addMessage(pattern, args)
    }

    /**
     * Simple constructor.
     *
     * @param cause Root cause.
     * @param pattern Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    constructor(cause: Throwable, pattern: Localizable, vararg args: Any): super(cause) {
        context.addMessage(pattern, args)
    }

    /** {@inheritDoc} */
    fun getContext(): ExceptionContext {
        return context
    }

    /** {@inheritDoc} */
    override val message: String?
        get() = this.context.getMessage()

    val localizedMessage: String?
        get() = this.context.getLocalizedMessage()

    companion object {
        /** Serializable version Id. */
        private val serialVersionUID = -6024911025449780478L
    }
}
