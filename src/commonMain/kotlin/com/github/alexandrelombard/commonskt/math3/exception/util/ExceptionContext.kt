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
 * Class that contains the actual implementation of the functionality mandated
 * by the {@link ExceptionContext} interface.
 * All Commons Math exceptions delegate the interface's methods to this class.
 *
 * @since 3.0
 */
class ExceptionContext
/** Simple constructor.
 * @param throwable the exception this context refers too
 */(
    /**
     * The throwable to which this context refers to.
     */
    val throwable: Throwable
) {

    /**
     * Various informations that enrich the informative message.
     */
    private val msgPatterns: MutableList<Localizable>
    /**
     * Various informations that enrich the informative message.
     * The arguments will replace the corresponding place-holders in
     * {@link #msgPatterns}.
     */
    private val msgArguments: MutableList<Array<Any>>
    /**
     * Arbitrary context information.
     */
    private val context: MutableMap<String, Any>

    init {
        msgPatterns    = arrayListOf()
        msgArguments   = arrayListOf()
        context        = hashMapOf()
    }

    /**
     * Adds a message.
     *
     * @param pattern Message pattern.
     * @param arguments Values for replacing the placeholders in the message
     * pattern.
     */
    fun addMessage(pattern: Localizable, vararg arguments: Any) {
        msgPatterns.add(pattern);
        msgArguments.add(ArgUtils.flatten(arguments));
    }

    /**
     * Sets the context (key, value) pair.
     * Keys are assumed to be unique within an instance. If the same key is
     * assigned a new value, the previous one will be lost.
     *
     * @param key Context key (not null).
     * @param value Context value.
     */
    fun setValue(key: String, value: Any) {
        context[key] = value
    }

    /**
     * Gets the value associated to the given context key.
     *
     * @param key Context key.
     * @return the context value or {@code null} if the key does not exist.
     */
    fun getValue(key: String): Any? {
        return context[key]
    }

    /**
     * Gets all the keys stored in the exception
     *
     * @return the set of keys.
     */
    fun getKeys(): Set<String> {
        return context.keys
    }

    /**
     * Gets the default message.
     *
     * @return the message.
     */
    fun getMessage(): String {
        return getMessage(Locale.US)
    }

    /**
     * Gets the message in the default locale.
     *
     * @return the localized message.
     */
    fun getLocalizedMessage(): String {
        return getMessage(Locale.getDefault());
    }

    /**
     * Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     * @return the localized message.
     */
    fun getMessage(locale: Locale): String {
        return buildMessage(locale, ": ");
    }

    /**
     * Gets the message in a specified locale.
     *
     * @param locale Locale in which the message should be translated.
     * @param separator Separator inserted between the message parts.
     * @return the localized message.
     */
    fun getMessage(locale: Locale, separator: String): String {
        return buildMessage(locale, separator);
    }

    /**
     * Builds a message string.
     *
     * @param locale Locale in which the message should be translated.
     * @param separator Message separator.
     * @return a localized message string.
     */
    private fun buildMessage(locale: Locale,
                                separator: String): String {
        val sb = StringBuilder()
        var count = 0
        val len = msgPatterns.size
        for (i in 0 .. len - 1) {
            val pat = msgPatterns[i]
            val args = msgArguments.get(i)
            val fmt = MessageFormat(pat.getLocalizedString(locale), locale)
            sb.append(fmt.format(args));
            if (++count < len) {
                // Add a separator if there are other messages.
                sb.append(separator);
            }
        }

        return sb.toString();
    }

    /**
     * Replaces a non-serializable object with an error message string.
     *
     * @param obj Object that does not implement the {@code Serializable}
     * interface.
     * @return a string that mentions which class could not be serialized.
     */
    private fun nonSerializableReplacement(obj: Any): String {
        return "[Object could not be serialized: " + obj::class.qualifiedName + "]";
    }

    companion object {
        /** Serializable version Id. */
        val serialVersionUID = -6024911025449780478L
    }
}
