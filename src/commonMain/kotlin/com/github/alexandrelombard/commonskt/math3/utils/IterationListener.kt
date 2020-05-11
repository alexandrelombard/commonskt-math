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
package com.github.alexandrelombard.commonskt.math3.utils

/**
 * The listener interface for receiving events occurring in an iterative
 * algorithm.
 *
 */
interface IterationListener {
    /**
     * Invoked after completion of the initial phase of the iterative algorithm
     * (prior to the main iteration loop).
     *
     * @param e The [IterationEvent] object.
     */
    fun initializationPerformed(e: IterationEvent)

    /**
     * Invoked each time an iteration is completed (in the main iteration loop).
     *
     * @param e The [IterationEvent] object.
     */
    fun iterationPerformed(e: IterationEvent)

    /**
     * Invoked each time a new iteration is completed (in the main iteration
     * loop).
     *
     * @param e The [IterationEvent] object.
     */
    fun iterationStarted(e: IterationEvent)

    /**
     * Invoked after completion of the operations which occur after breaking out
     * of the main iteration loop.
     *
     * @param e The [IterationEvent] object.
     */
    fun terminationPerformed(e: IterationEvent)
}
