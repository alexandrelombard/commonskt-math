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

import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.exception.util.Localizable


/**
 * Exception triggered when something that shouldn't happen does happen.
 *
 * @since 2.2
 */
class MathInternalError : MathIllegalStateException {

    /**
     * Simple constructor.
     */
    constructor() {
        getContext().addMessage(LocalizedFormats.INTERNAL_ERROR, REPORT_URL)
    }

    /**
     * Simple constructor.
     * @param cause root cause
     */
    constructor(cause: Throwable) : super(
        cause,
        LocalizedFormats.INTERNAL_ERROR,
        REPORT_URL
    )

    /**
     * Constructor accepting a localized message.
     *
     * @param pattern Message pattern explaining the cause of the error.
     * @param args Arguments.
     */
    constructor(pattern: Localizable, vararg args: Any) : super(pattern, args)

    companion object {
        /** Serializable version Id.  */
        private const val serialVersionUID = -6276776513966934846L

        /** URL for reporting problems.  */
        private const val REPORT_URL = "https://issues.apache.org/jira/browse/MATH"
    }
}
