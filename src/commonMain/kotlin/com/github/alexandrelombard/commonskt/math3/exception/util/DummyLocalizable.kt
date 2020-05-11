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
package com.github.alexandrelombard.commonskt.math3.exception.util

/**
 * Dummy implementation of the [Localizable] interface, without localization.
 *
 * @since 2.2
 */
class DummyLocalizable
/** Simple constructor.
 * @param source source text
 */(
    /** Source string.  */
    override val sourceString: String
) :
    Localizable {
    /** {@inheritDoc}  */

    /** {@inheritDoc}  */
    override fun getLocalizedString(locale: Locale): String {
        return sourceString
    }

    /** {@inheritDoc}  */
    override fun toString(): String {
        return sourceString
    }

    companion object {
        /** Serializable version identifier.  */
        private const val serialVersionUID = 8843275624471387299L
    }
}
