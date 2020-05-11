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
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

/**
 * Error thrown when a numerical computation can not be performed because the
 * numerical result failed to converge to a finite value.
 *
 * @since 2.2
 */
class ConvergenceException
/**
 * Construct the exception with a specific context and arguments.
 *
 * @param pattern Message pattern providing the specific context of
 * the error.
 * @param args Arguments.
 */(pattern: Localizable = LocalizedFormats.CONVERGENCE_FAILED, vararg args: Any) : MathIllegalStateException() {

    init {
        getContext().addMessage(pattern, args)
    }

    companion object {
        /** Serializable version Id. */
        private const val serialVersionUID = 4330003017885151975L
    }
}
