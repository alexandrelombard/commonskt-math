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

import com.github.alexandrelombard.commonskt.math3.exception.util.ExceptionContext
import com.github.alexandrelombard.commonskt.math3.exception.util.ExceptionContextProvider
import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable

/**
 * As of release 4.0, all exceptions thrown by the Commons Math code (except
 * [NullArgumentException]) inherit from this class.
 * In most cases, this class should not be instantiated directly: it should
 * serve as a base class for implementing exception classes that describe a
 * specific "problem".
 *
 * @since 3.1
 */
class MathRuntimeException(
    pattern: Localizable,
    vararg args: Any
) : RuntimeException(), ExceptionContextProvider {
    /** {@inheritDoc}  */
    /** Context.  */
    override val context: ExceptionContext = ExceptionContext(this)

    /** {@inheritDoc}  */
    override val message: String
        get() = context.getMessage()

    /** {@inheritDoc}  */
    val localizedMessage: String
        get() = context.getLocalizedMessage()

    /**
     * @param pattern Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    init {
        context.addMessage(pattern, args)
    }

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = 20120926L
    }
}
