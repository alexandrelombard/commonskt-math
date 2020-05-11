package com.github.alexandrelombard.commonskt.math3.utils

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


/**
 * The root class from which all events occurring while running an
 * [IterationManager] should be derived.
 *
 */
class IterationEvent
/**
 * Creates a new instance of this class.
 *
 * @param source the iterative algorithm on which the event initially
 * occurred
 * @param iterations the number of iterations performed at the time
 * `this` event is created
 */(
    source: Any,
    /** The number of iterations performed so far.  */
    val iterations: Int
) : EventObject(source) {
    /**
     * Returns the number of iterations performed at the time `this` event
     * is created.
     *
     * @return the number of iterations performed
     */

    companion object {
        /**  */
        private const val serialVersionUID = 20120128L
    }

}
