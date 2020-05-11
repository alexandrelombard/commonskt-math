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
 * Utility class for transforming the list of arguments passed to
 * constructors of exceptions.
 *
 */
object ArgUtils {
    /**
     * Transform a multidimensional array into a one-dimensional list.
     *
     * @param array Array (possibly multidimensional).
     * @return a list of all the `Object` instances contained in
     * `array`.
     */
    fun flatten(array: Array<*>): Array<Any?> {
        val list: MutableList<Any?> = arrayListOf()
        for (o in array) {
            if (o is Array<*>) {
                for (oR in flatten(o)) {
                    list.add(oR)
                }
            } else {
                list.add(o)
            }
        }
        return list.toTypedArray()
    }
}
