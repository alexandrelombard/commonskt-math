package com.github.alexandrelombard.commonskt.math3.utils
import com.github.alexandrelombard.commonskt.math3.exception.MaxCountExceededException


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
import org.apache.commons.math3.exception.MaxCountExceededException


/**
 * This abstract class provides a general framework for managing iterative
 * algorithms. The maximum number of iterations can be set, and methods are
 * provided to monitor the current iteration count. A lightweight event
 * framework is also provided.
 *
 */
class IterationManager {
    /** Keeps a count of the number of iterations.  */
    private var iterations: IntegerSequence.Incrementor

    /** The collection of all listeners attached to this iterative algorithm.  */
    private val listeners: MutableCollection<IterationListener>

    /**
     * Creates a new instance of this class.
     *
     * @param maxIterations the maximum number of iterations
     */
    constructor(maxIterations: Int) {
        iterations = IntegerSequence.Incrementor.create().withMaximalCount(maxIterations)
        listeners = CopyOnWriteArrayList<IterationListener>()
    }

    /**
     * Creates a new instance of this class.
     *
     * @param maxIterations the maximum number of iterations
     * @param callBack the function to be called when the maximum number of
     * iterations has been reached
     * @throws org.apache.commons.math3.exception.NullArgumentException if `callBack` is `null`
     * @since 3.1
     */
    @Deprecated(
        """as of 3.6, replaced with {@link #IterationManager(int,
     * org.apache.commons.math3.util.IntegerSequence.Incrementor.MaxCountExceededCallback)}"""
    )
    constructor(
        maxIterations: Int,
        callBack: Incrementor.MaxCountExceededCallback
    ) : this(
        maxIterations,
        object :
            IntegerSequence.Incrementor.MaxCountExceededCallback {
            /** {@inheritDoc}  */
            override fun trigger(maximalCount: Int) {
                callBack.trigger(maximalCount)
            }
        }) {
    }

    /**
     * Creates a new instance of this class.
     *
     * @param maxIterations the maximum number of iterations
     * @param callBack the function to be called when the maximum number of
     * iterations has been reached
     * @throws org.apache.commons.math3.exception.NullArgumentException if `callBack` is `null`
     * @since 3.6
     */
    constructor(
        maxIterations: Int,
        callBack: IntegerSequence.Incrementor.MaxCountExceededCallback?
    ) {
        iterations = IntegerSequence.Incrementor.create().withMaximalCount(maxIterations).withCallback(callBack)
        listeners = CopyOnWriteArrayList<IterationListener>()
    }

    /**
     * Attaches a listener to this manager.
     *
     * @param listener A `IterationListener` object.
     */
    fun addIterationListener(listener: IterationListener) {
        listeners.add(listener)
    }

    /**
     * Informs all registered listeners that the initial phase (prior to the
     * main iteration loop) has been completed.
     *
     * @param e The [IterationEvent] object.
     */
    fun fireInitializationEvent(e: IterationEvent?) {
        for (l in listeners) {
            l.initializationPerformed(e)
        }
    }

    /**
     * Informs all registered listeners that a new iteration (in the main
     * iteration loop) has been performed.
     *
     * @param e The [IterationEvent] object.
     */
    fun fireIterationPerformedEvent(e: IterationEvent?) {
        for (l in listeners) {
            l.iterationPerformed(e)
        }
    }

    /**
     * Informs all registered listeners that a new iteration (in the main
     * iteration loop) has been started.
     *
     * @param e The [IterationEvent] object.
     */
    fun fireIterationStartedEvent(e: IterationEvent?) {
        for (l in listeners) {
            l.iterationStarted(e)
        }
    }

    /**
     * Informs all registered listeners that the final phase (post-iterations)
     * has been completed.
     *
     * @param e The [IterationEvent] object.
     */
    fun fireTerminationEvent(e: IterationEvent?) {
        for (l in listeners) {
            l.terminationPerformed(e)
        }
    }

    /**
     * Returns the number of iterations of this solver, 0 if no iterations has
     * been performed yet.
     *
     * @return the number of iterations.
     */
    fun getIterations(): Int {
        return iterations.count
    }

    /**
     * Returns the maximum number of iterations.
     *
     * @return the maximum number of iterations.
     */
    val maxIterations: Int
        get() = iterations.maximalCount

    /**
     * Increments the iteration count by one, and throws an exception if the
     * maximum number of iterations is reached. This method should be called at
     * the beginning of a new iteration.
     *
     * @throws MaxCountExceededException if the maximum number of iterations is
     * reached.
     */
    fun incrementIterationCount() {
        iterations.increment()
    }

    /**
     * Removes the specified iteration listener from the list of listeners
     * currently attached to `this` object. Attempting to remove a
     * listener which was *not* previously registered does not cause any
     * error.
     *
     * @param listener The [IterationListener] to be removed.
     */
    fun removeIterationListener(listener: IterationListener) {
        listeners.remove(listener)
    }

    /**
     * Sets the iteration count to 0. This method must be called during the
     * initial phase.
     */
    fun resetIterationCount() {
        iterations = iterations.withStart(0)
    }
}
