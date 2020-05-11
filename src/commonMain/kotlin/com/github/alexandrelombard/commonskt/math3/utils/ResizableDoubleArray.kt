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
import com.github.alexandrelombard.commonskt.math3.exception.*
import com.github.alexandrelombard.commonskt.math3.exception.util.LocalizedFormats
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.ceil
import com.github.alexandrelombard.commonskt.math3.utils.FastMath.round
import com.github.alexandrelombard.commonskt.math3.utils.MathUtils.checkNotNull
import com.github.alexandrelombard.commonskt.math3.utils.ResizableDoubleArray.ExpansionMode
import org.apache.commons.math3.exception.MathIllegalArgumentException
import org.apache.commons.math3.exception.MathIllegalStateException
import org.apache.commons.math3.exception.MathInternalError
import org.apache.commons.math3.exception.NotStrictlyPositiveException
import org.apache.commons.math3.exception.NullArgumentException
import org.apache.commons.math3.exception.NumberIsTooSmallException
import org.apache.commons.math3.exception.util.LocalizedFormats
import kotlin.jvm.JvmOverloads
import kotlin.jvm.Synchronized


/**
 *
 *
 * A variable length [DoubleArray] implementation that automatically
 * handles expanding and contracting its internal storage array as elements
 * are added and removed.
 *
 * <h3>Important note: Usage should not assume that this class is thread-safe
 * even though some of the methods are `synchronized`.
 * This qualifier will be dropped in the next major release (4.0).</h3>
 *
 *
 * The internal storage array starts with capacity determined by the
 * `initialCapacity` property, which can be set by the constructor.
 * The default initial capacity is 16.  Adding elements using
 * [.addElement] appends elements to the end of the array.
 * When there are no open entries at the end of the internal storage array,
 * the array is expanded.  The size of the expanded array depends on the
 * `expansionMode` and `expansionFactor` properties.
 * The `expansionMode` determines whether the size of the array is
 * multiplied by the `expansionFactor`
 * ([ExpansionMode.MULTIPLICATIVE]) or if the expansion is additive
 * ([ExpansionMode.ADDITIVE] -- `expansionFactor` storage
 * locations added).
 * The default `expansionMode` is `MULTIPLICATIVE` and the default
 * `expansionFactor` is 2.
 *
 *
 *
 * The [.addElementRolling] method adds a new element to the end
 * of the internal storage array and adjusts the "usable window" of the
 * internal array forward by one position (effectively making what was the
 * second element the first, and so on).  Repeated activations of this method
 * (or activation of [.discardFrontElements]) will effectively orphan
 * the storage locations at the beginning of the internal storage array.  To
 * reclaim this storage, each time one of these methods is activated, the size
 * of the internal storage array is compared to the number of addressable
 * elements (the `numElements` property) and if the difference
 * is too large, the internal array is contracted to size
 * `numElements + 1`.  The determination of when the internal
 * storage array is "too large" depends on the `expansionMode` and
 * `contractionFactor` properties.  If  the `expansionMode`
 * is `MULTIPLICATIVE`, contraction is triggered when the
 * ratio between storage array length and `numElements` exceeds
 * `contractionFactor.`  If the `expansionMode`
 * is `ADDITIVE`, the number of excess storage locations
 * is compared to `contractionFactor`.
 *
 *
 *
 * To avoid cycles of expansions and contractions, the
 * `expansionFactor` must not exceed the `contractionFactor`.
 * Constructors and mutators for both of these properties enforce this
 * requirement, throwing a `MathIllegalArgumentException` if it is
 * violated.
 *
 */
class ResizableDoubleArray : DoubleArray {
    /**
     * The contraction criterion defines when the internal array will contract
     * to store only the number of elements in the element array.
     * If  the `expansionMode` is `MULTIPLICATIVE_MODE`,
     * contraction is triggered when the ratio between storage array length
     * and `numElements` exceeds `contractionFactor`.
     * If the `expansionMode` is `ADDITIVE_MODE`, the
     * number of excess storage locations is compared to
     * `contractionFactor.`
     *
     * @return the contraction criterion used to reclaim memory.
     * @since 3.1
     */
    /**
     * The contraction criteria determines when the internal array will be
     * contracted to fit the number of elements contained in the element
     * array + 1.
     */
    var contractionCriterion = 2.5
        private set

    /**
     * The expansion factor of the array.  When the array needs to be expanded,
     * the new array size will be
     * `internalArray.length * expansionFactor`
     * if `expansionMode` is set to MULTIPLICATIVE_MODE, or
     * `internalArray.length + expansionFactor` if
     * `expansionMode` is set to ADDITIVE_MODE.
     */
    private var expansionFactor = 2.0

    /**
     * Determines whether array expansion by `expansionFactor`
     * is additive or multiplicative.
     */
    private var expansionMode = ExpansionMode.MULTIPLICATIVE

    /**
     * Returns the internal storage array.  Note that this method returns
     * a reference to the internal storage array, not a copy, and to correctly
     * address elements of the array, the `startIndex` is
     * required (available via the [.start] method).  This method should
     * only be used in cases where copying the internal array is not practical.
     * The [.getElements] method should be used in all other cases.
     *
     *
     * @return the internal storage array used by this object
     * @since 2.0
     */
    /**
     * Provides *direct* access to the internal storage array.
     * Please note that this method returns a reference to this object's
     * storage array, not a copy.
     * <br></br>
     * To correctly address elements of the array, the "start index" is
     * required (available via the [getStartIndex][.getStartIndex]
     * method.
     * <br></br>
     * This method should only be used to avoid copying the internal array.
     * The returned value *must* be used for reading only; other
     * uses could lead to this object becoming inconsistent.
     * <br></br>
     * The [.getElements] method has no such limitation since it
     * returns a copy of this array's addressable elements.
     *
     * @return the internal storage array used by this object.
     * @since 3.1
     */
    /**
     * The internal storage array.
     */
    @get:Synchronized
    @get:Deprecated("As of 3.1.")
    protected var arrayRef: DoubleArray
        private set
        protected get() = field
    set

    /**
     * The number of addressable elements in the array.  Note that this
     * has nothing to do with the length of the internal storage array.
     */
    private var numElements = 0

    /**
     * Returns the "start index" of the internal array.
     * This index is the position of the first addressable element in the
     * internal storage array.
     * The addressable elements in the array are at indices contained in
     * the interval [[.getStartIndex],
     * [.getStartIndex] + [.getNumElements] - 1].
     *
     * @return the start index.
     * @since 3.1
     */
    /**
     * The position of the first addressable element in the internal storage
     * array.  The addressable elements in the array are
     * `internalArray[startIndex],...,internalArray[startIndex + numElements - 1]`.
     */
    protected var startIndex = 0
        private set

    /**
     * Specification of expansion algorithm.
     * @since 3.1
     */
    enum class ExpansionMode {
        /** Multiplicative expansion mode.  */
        MULTIPLICATIVE,

        /** Additive expansion mode.  */
        ADDITIVE
    }

    /**
     * Creates an instance from an existing `double[]` with the
     * initial capacity and numElements corresponding to the size of
     * the supplied `double[]` array.
     * If the supplied array is null, a new empty array with the default
     * initial capacity will be created.
     * The input array is copied, not referenced.
     * Other properties take default values:
     *
     *  * `initialCapacity = 16`
     *  * `expansionMode = MULTIPLICATIVE`
     *  * `expansionFactor = 2.0`
     *  * `contractionCriterion = 2.5`
     *
     *
     * @param initialArray initial array
     * @since 2.2
     */
    constructor(initialArray: DoubleArray) : this(
        DEFAULT_INITIAL_CAPACITY,
        DEFAULT_EXPANSION_FACTOR,
        DEFAULT_CONTRACTION_DELTA + DEFAULT_EXPANSION_FACTOR,
        ExpansionMode.MULTIPLICATIVE,
        *initialArray
    ) {
    }

    /**
     * Creates an instance with the specified initial capacity
     * and expansion factor.
     * The remaining properties take default values:
     *
     *  * `expansionMode = MULTIPLICATIVE`
     *  * `contractionCriterion = 0.5 + expansionFactor`
     *
     * <br></br>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     *
     *  * `initialCapacity > 0`
     *  * `expansionFactor > 1`
     *
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @throws MathIllegalArgumentException if parameters are not valid.
     */
    @Deprecated(
        """As of 3.1. Please use
      {@link #ResizableDoubleArray(int,double)} instead."""
    )
    constructor(
        initialCapacity: Int,
        expansionFactor: Float
    ) : this(
        initialCapacity,
        expansionFactor.toDouble()
    ) {
    }

    /**
     * Creates an instance with the specified initialCapacity,
     * expansionFactor, and contractionCriterion.
     * The expansion mode will default to `MULTIPLICATIVE`.
     * <br></br>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     *
     *  * `initialCapacity > 0`
     *  * `expansionFactor > 1`
     *  * `contractionCriterion >= expansionFactor`
     *
     *
     * @param initialCapacity Initial size of the internal storage array..
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @param contractionCriteria Contraction criteria.
     * @throws MathIllegalArgumentException if parameters are not valid.
     */
    @Deprecated(
        """As of 3.1. Please use
      {@link #ResizableDoubleArray(int,double,double)} instead."""
    )
    constructor(
        initialCapacity: Int,
        expansionFactor: Float,
        contractionCriteria: Float
    ) : this(
        initialCapacity,
        expansionFactor.toDouble(),
        contractionCriteria.toDouble()
    ) {
    }

    /**
     *
     *
     * Create a ResizableArray with the specified properties.
     *
     *
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     *
     *  * `initialCapacity > 0`
     *  * `expansionFactor > 1`
     *  * `contractionFactor >= expansionFactor`
     *  * `expansionMode in {MULTIPLICATIVE_MODE, ADDITIVE_MODE}`
     *
     *
     *
     * @param initialCapacity the initial size of the internal storage array
     * @param expansionFactor the array will be expanded based on this
     * parameter
     * @param contractionCriteria the contraction Criteria
     * @param expansionMode  the expansion mode
     * @throws MathIllegalArgumentException if parameters are not valid
     */
    @Deprecated(
        """As of 3.1. Please use
      {@link #ResizableDoubleArray(int,double,double,ExpansionMode,double[])}
      instead."""
    )
    constructor(
        initialCapacity: Int, expansionFactor: Float,
        contractionCriteria: Float, expansionMode: Int
    ) : this(
        initialCapacity,
        expansionFactor.toDouble(),
        contractionCriteria.toDouble(),
        if (expansionMode == ADDITIVE_MODE) ExpansionMode.ADDITIVE else ExpansionMode.MULTIPLICATIVE,
        null
    ) {
        // XXX Just ot retain the expected failure in a unit test.
        // With the new "enum", that test will become obsolete.
        setExpansionMode(expansionMode)
    }
    /**
     * Creates an instance with the specified properties.
     * <br></br>
     * Throws MathIllegalArgumentException if the following conditions are
     * not met:
     *
     *  * `initialCapacity > 0`
     *  * `expansionFactor > 1`
     *  * `contractionCriterion >= expansionFactor`
     *
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @param contractionCriterion Contraction criteria.
     * @param expansionMode Expansion mode.
     * @param data Initial contents of the array.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     */
    /**
     * Creates an instance with the specified initial capacity,
     * expansion factor, and contraction criteria.
     * The expansion mode will default to `MULTIPLICATIVE`.
     * <br></br>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     *
     *  * `initialCapacity > 0`
     *  * `expansionFactor > 1`
     *  * `contractionCriterion >= expansionFactor`
     *
     *
     * @param initialCapacity Initial size of the internal storage array..
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @param contractionCriterion Contraction criterion.
     * @throws MathIllegalArgumentException if the parameters are not valid.
     * @since 3.1
     */
    /**
     * Creates an instance with the specified initial capacity
     * and expansion factor.
     * The remaining properties take default values:
     *
     *  * `expansionMode = MULTIPLICATIVE`
     *  * `contractionCriterion = 0.5 + expansionFactor`
     *
     * <br></br>
     * Throws IllegalArgumentException if the following conditions are
     * not met:
     *
     *  * `initialCapacity > 0`
     *  * `expansionFactor > 1`
     *
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @param expansionFactor The array will be expanded based on this
     * parameter.
     * @throws MathIllegalArgumentException if parameters are not valid.
     * @since 3.1
     */
    /**
     * Creates an instance with the specified initial capacity.
     * Other properties take default values:
     *
     *  * `expansionMode = MULTIPLICATIVE`
     *  * `expansionFactor = 2.0`
     *  * `contractionCriterion = 2.5`
     *
     * @param initialCapacity Initial size of the internal storage array.
     * @throws MathIllegalArgumentException if `initialCapacity <= 0`.
     */
    /**
     * Creates an instance with default properties.
     *
     *  * `initialCapacity = 16`
     *  * `expansionMode = MULTIPLICATIVE`
     *  * `expansionFactor = 2.0`
     *  * `contractionCriterion = 2.5`
     *
     */
    @JvmOverloads
    constructor(
        initialCapacity: Int = DEFAULT_INITIAL_CAPACITY,
        expansionFactor: Double = DEFAULT_EXPANSION_FACTOR,
        contractionCriterion: Double =
            DEFAULT_CONTRACTION_DELTA + expansionFactor,
        expansionMode: ExpansionMode =
            ExpansionMode.MULTIPLICATIVE,
        vararg data: Double =
            null
    ) {
        if (initialCapacity <= 0) {
            throw NotStrictlyPositiveException(
                LocalizedFormats.INITIAL_CAPACITY_NOT_POSITIVE,
                initialCapacity
            )
        }
        checkContractExpand(contractionCriterion, expansionFactor)
        this.expansionFactor = expansionFactor
        this.contractionCriterion = contractionCriterion
        this.expansionMode = expansionMode
        arrayRef = DoubleArray(initialCapacity)
        numElements = 0
        startIndex = 0
        if (data != null && data.size > 0) {
            addElements(data)
        }
    }

    /**
     * Copy constructor.  Creates a new ResizableDoubleArray that is a deep,
     * fresh copy of the original. Needs to acquire synchronization lock
     * on original.  Original may not be null; otherwise a [NullArgumentException]
     * is thrown.
     *
     * @param original array to copy
     * @exception NullArgumentException if original is null
     * @since 2.0
     */
    constructor(original: ResizableDoubleArray) {
        checkNotNull(original)
        copy(original, this)
    }

    /**
     * Adds an element to the end of this expandable array.
     *
     * @param value Value to be added to end of array.
     */
    @Synchronized
    fun addElement(value: Double) {
        if (arrayRef.size <= startIndex + numElements) {
            expand()
        }
        arrayRef[startIndex + numElements++] = value
    }

    /**
     * Adds several element to the end of this expandable array.
     *
     * @param values Values to be added to end of array.
     * @since 2.2
     */
    @Synchronized
    fun addElements(values: DoubleArray) {
        val tempArray = DoubleArray(numElements + values.size + 1)
        java.lang.System.arraycopy(arrayRef, startIndex, tempArray, 0, numElements)
        java.lang.System.arraycopy(values, 0, tempArray, numElements, values.size)
        arrayRef = tempArray
        startIndex = 0
        numElements += values.size
    }

    /**
     *
     *
     * Adds an element to the end of the array and removes the first
     * element in the array.  Returns the discarded first element.
     * The effect is similar to a push operation in a FIFO queue.
     *
     *
     *
     * Example: If the array contains the elements 1, 2, 3, 4 (in that order)
     * and addElementRolling(5) is invoked, the result is an array containing
     * the entries 2, 3, 4, 5 and the value returned is 1.
     *
     *
     * @param value Value to be added to the array.
     * @return the value which has been discarded or "pushed" out of the array
     * by this rolling insert.
     */
    @Synchronized
    fun addElementRolling(value: Double): Double {
        val discarded = arrayRef[startIndex]
        if (startIndex + (numElements + 1) > arrayRef.size) {
            expand()
        }
        // Increment the start index
        startIndex += 1

        // Add the new value
        arrayRef[startIndex + (numElements - 1)] = value

        // Check the contraction criterion.
        if (shouldContract()) {
            contract()
        }
        return discarded
    }

    /**
     * Substitutes `value` for the most recently added value.
     * Returns the value that has been replaced. If the array is empty (i.e.
     * if [.numElements] is zero), an IllegalStateException is thrown.
     *
     * @param value New value to substitute for the most recently added value
     * @return the value that has been replaced in the array.
     * @throws MathIllegalStateException if the array is empty
     * @since 2.0
     */
    @Synchronized
    @Throws(MathIllegalStateException::class)
    fun substituteMostRecentElement(value: Double): Double {
        if (numElements < 1) {
            throw MathIllegalStateException(
                LocalizedFormats.CANNOT_SUBSTITUTE_ELEMENT_FROM_EMPTY_ARRAY
            )
        }
        val substIndex = startIndex + (numElements - 1)
        val discarded = arrayRef[substIndex]
        arrayRef[substIndex] = value
        return discarded
    }

    /**
     * Checks the expansion factor and the contraction criterion and throws an
     * IllegalArgumentException if the contractionCriteria is less than the
     * expansionCriteria
     *
     * @param expansion factor to be checked
     * @param contraction criteria to be checked
     * @throws MathIllegalArgumentException if the contractionCriteria is less than
     * the expansionCriteria.
     */
    @Deprecated(
        """As of 3.1. Please use
      {@link #checkContractExpand(double,double)} instead."""
    )
    @Throws(
        MathIllegalArgumentException::class
    )
    protected fun checkContractExpand(contraction: Float, expansion: Float) {
        checkContractExpand(
            contraction.toDouble(),
            expansion.toDouble()
        )
    }

    /**
     * Checks the expansion factor and the contraction criterion and raises
     * an exception if the contraction criterion is smaller than the
     * expansion criterion.
     *
     * @param contraction Criterion to be checked.
     * @param expansion Factor to be checked.
     * @throws NumberIsTooSmallException if `contraction < expansion`.
     * @throws NumberIsTooSmallException if `contraction <= 1`.
     * @throws NumberIsTooSmallException if `expansion <= 1 `.
     * @since 3.1
     */
    @Throws(NumberIsTooSmallException::class)
    protected fun checkContractExpand(
        contraction: Double,
        expansion: Double
    ) {
        if (contraction < expansion) {
            val e = NumberIsTooSmallException(contraction, 1, true)
            e.context.addMessage(
                LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_EXPANSION_FACTOR,
                contraction, expansion
            )
            throw e
        }
        if (contraction <= 1) {
            val e = NumberIsTooSmallException(contraction, 1, false)
            e.context.addMessage(
                LocalizedFormats.CONTRACTION_CRITERIA_SMALLER_THAN_ONE,
                contraction
            )
            throw e
        }
        if (expansion <= 1) {
            val e = NumberIsTooSmallException(contraction, 1, false)
            e.context.addMessage(
                LocalizedFormats.EXPANSION_FACTOR_SMALLER_THAN_ONE,
                expansion
            )
            throw e
        }
    }

    /**
     * Clear the array contents, resetting the number of elements to zero.
     */
    @Synchronized
    fun clear() {
        numElements = 0
        startIndex = 0
    }

    /**
     * Contracts the storage array to the (size of the element set) + 1 - to
     * avoid a zero length array. This function also resets the startIndex to
     * zero.
     */
    @Synchronized
    fun contract() {
        val tempArray = DoubleArray(numElements + 1)

        // Copy and swap - copy only the element array from the src array.
        java.lang.System.arraycopy(arrayRef, startIndex, tempArray, 0, numElements)
        arrayRef = tempArray

        // Reset the start index to zero
        startIndex = 0
    }

    /**
     * Discards the `i` initial elements of the array.  For example,
     * if the array contains the elements 1,2,3,4, invoking
     * `discardFrontElements(2)` will cause the first two elements
     * to be discarded, leaving 3,4 in the array.  Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the front of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    @Synchronized
    @Throws(MathIllegalArgumentException::class)
    fun discardFrontElements(i: Int) {
        discardExtremeElements(i, true)
    }

    /**
     * Discards the `i` last elements of the array.  For example,
     * if the array contains the elements 1,2,3,4, invoking
     * `discardMostRecentElements(2)` will cause the last two elements
     * to be discarded, leaving 1,2 in the array.  Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the end of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    @Synchronized
    @Throws(MathIllegalArgumentException::class)
    fun discardMostRecentElements(i: Int) {
        discardExtremeElements(i, false)
    }

    /**
     * Discards the `i` first or last elements of the array,
     * depending on the value of `front`.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * `discardExtremeElements(2,false)` will cause the last two elements
     * to be discarded, leaving 1,2 in the array.
     * For example, if the array contains the elements 1,2,3,4, invoking
     * `discardExtremeElements(2,true)` will cause the first two elements
     * to be discarded, leaving 3,4 in the array.
     * Throws illegalArgumentException
     * if i exceeds numElements.
     *
     * @param i  the number of elements to discard from the front/end of the array
     * @param front true if elements are to be discarded from the front
     * of the array, false if elements are to be discarded from the end
     * of the array
     * @throws MathIllegalArgumentException if i is greater than numElements.
     * @since 2.0
     */
    @Synchronized
    @Throws(MathIllegalArgumentException::class)
    private fun discardExtremeElements(
        i: Int,
        front: Boolean
    ) {
        if (i > numElements) {
            throw MathIllegalArgumentException(
                LocalizedFormats.TOO_MANY_ELEMENTS_TO_DISCARD_FROM_ARRAY,
                i, numElements
            )
        } else if (i < 0) {
            throw MathIllegalArgumentException(
                LocalizedFormats.CANNOT_DISCARD_NEGATIVE_NUMBER_OF_ELEMENTS,
                i
            )
        } else {
            // "Subtract" this number of discarded from numElements
            numElements -= i
            if (front) {
                startIndex += i
            }
        }
        if (shouldContract()) {
            contract()
        }
    }

    /**
     * Expands the internal storage array using the expansion factor.
     *
     *
     * if `expansionMode` is set to MULTIPLICATIVE_MODE,
     * the new array size will be `internalArray.length * expansionFactor.`
     * If `expansionMode` is set to ADDITIVE_MODE,  the length
     * after expansion will be `internalArray.length + expansionFactor`
     *
     */
    @Synchronized
    protected fun expand() {
        // notice the use of FastMath.ceil(), this guarantees that we will always
        // have an array of at least currentSize + 1.   Assume that the
        // current initial capacity is 1 and the expansion factor
        // is 1.000000000000000001.  The newly calculated size will be
        // rounded up to 2 after the multiplication is performed.
        var newSize = 0
        newSize = if (expansionMode == ExpansionMode.MULTIPLICATIVE) {
            ceil(arrayRef.size * expansionFactor).toInt()
        } else {
            (arrayRef.size + round(expansionFactor)).toInt()
        }
        val tempArray = DoubleArray(newSize)

        // Copy and swap
        java.lang.System.arraycopy(arrayRef, 0, tempArray, 0, arrayRef.size)
        arrayRef = tempArray
    }

    /**
     * Expands the internal storage array to the specified size.
     *
     * @param size Size of the new internal storage array.
     */
    @Synchronized
    private fun expandTo(size: Int) {
        val tempArray = DoubleArray(size)
        // Copy and swap
        java.lang.System.arraycopy(arrayRef, 0, tempArray, 0, arrayRef.size)
        arrayRef = tempArray
    }

    /**
     * The contraction criteria defines when the internal array will contract
     * to store only the number of elements in the element array.
     * If  the `expansionMode` is `MULTIPLICATIVE_MODE`,
     * contraction is triggered when the ratio between storage array length
     * and `numElements` exceeds `contractionFactor`.
     * If the `expansionMode` is `ADDITIVE_MODE`, the
     * number of excess storage locations is compared to
     * `contractionFactor.`
     *
     * @return the contraction criteria used to reclaim memory.
     */
    /**
     * Sets the contraction criteria.
     *
     * @param contractionCriteria contraction criteria
     * @throws MathIllegalArgumentException if the contractionCriteria is less than
     * the expansionCriteria.
     */
    @get:Deprecated(
        """As of 3.1. Please use {@link #getContractionCriterion()}
      instead."""
    )
    @set:Throws(
        MathIllegalArgumentException::class
    )
    @set:Deprecated("As of 3.1 (to be removed in 4.0 as field will become " final ").")
    var contractionCriteria: Float
        get() = contractionCriterion.toFloat()
        set(contractionCriteria) {
            checkContractExpand(contractionCriteria, getExpansionFactor())
            synchronized(this) { contractionCriterion = contractionCriteria.toDouble() }
        }

    /**
     * Returns the element at the specified index
     *
     * @param index index to fetch a value from
     * @return value stored at the specified index
     * @throws ArrayIndexOutOfBoundsException if `index` is less than
     * zero or is greater than `getNumElements() - 1`.
     */
    @Synchronized
    fun getElement(index: Int): Double {
        return if (index >= numElements) {
            throw ArrayIndexOutOfBoundsException(index)
        } else if (index >= 0) {
            arrayRef[startIndex + index]
        } else {
            throw ArrayIndexOutOfBoundsException(index)
        }
    }

    /**
     * Returns a double array containing the elements of this
     * `ResizableArray`.  This method returns a copy, not a
     * reference to the underlying array, so that changes made to the returned
     * array have no effect on this `ResizableArray.`
     * @return the double array.
     */
    @get:Synchronized
    val elements: DoubleArray
        get() {
            val elementArray = DoubleArray(numElements)
            java.lang.System.arraycopy(arrayRef, startIndex, elementArray, 0, numElements)
            return elementArray
        }

    /**
     * The expansion factor controls the size of a new array when an array
     * needs to be expanded.  The `expansionMode`
     * determines whether the size of the array is multiplied by the
     * `expansionFactor` (MULTIPLICATIVE_MODE) or if
     * the expansion is additive (ADDITIVE_MODE -- `expansionFactor`
     * storage locations added).  The default `expansionMode` is
     * MULTIPLICATIVE_MODE and the default `expansionFactor`
     * is 2.0.
     *
     * @return the expansion factor of this expandable double array
     */
    @Deprecated("As of 3.1. Return type will be changed to " double " in 4.0.")
    fun getExpansionFactor(): Float {
        return expansionFactor.toFloat()
    }

    /**
     * The expansion mode determines whether the internal storage
     * array grows additively or multiplicatively when it is expanded.
     *
     * @return the expansion mode.
     */
    @Deprecated(
        """As of 3.1. Return value to be changed to
      {@link ExpansionMode} in 4.0."""
    )
    fun getExpansionMode(): Int {
        synchronized(this) {
            return when (expansionMode) {
                ExpansionMode.MULTIPLICATIVE -> MULTIPLICATIVE_MODE
                ExpansionMode.ADDITIVE -> ADDITIVE_MODE
                else -> throw MathInternalError() // Should never happen.
            }
        }
    }

    /**
     * Notice the package scope on this method.   This method is simply here
     * for the JUnit test, it allows us check if the expansion is working
     * properly after a number of expansions.  This is not meant to be a part
     * of the public interface of this class.
     *
     * @return the length of the internal storage array.
     */
    @get:Synchronized
    @get:Deprecated("As of 3.1. Please use {@link #getCapacity()} instead.")
    val internalLength: Int
        get() = arrayRef.size

    /**
     * Gets the currently allocated size of the internal data structure used
     * for storing elements.
     * This is not to be confused with [the number of][.getNumElements].
     *
     * @return the length of the internal array.
     * @since 3.1
     */
    val capacity: Int
        get() = arrayRef.size

    /**
     * Returns the number of elements currently in the array.  Please note
     * that this is different from the length of the internal storage array.
     *
     * @return the number of elements.
     */
    @Synchronized
    fun getNumElements(): Int {
        return numElements
    }

    /**
     * Performs an operation on the addressable elements of the array.
     *
     * @param f Function to be applied on this array.
     * @return the result.
     * @since 3.1
     */
    fun compute(f: MathArrays.Function): Double {
        var array: DoubleArray
        var start: Int
        var num: Int
        synchronized(this) {
            array = arrayRef
            start = startIndex
            num = numElements
        }
        return f.evaluate(array, start, num)
    }

    /**
     * Sets the element at the specified index.  If the specified index is greater than
     * `getNumElements() - 1`, the `numElements` property
     * is increased to `index +1` and additional storage is allocated
     * (if necessary) for the new element and all  (uninitialized) elements
     * between the new element and the previous end of the array).
     *
     * @param index index to store a value in
     * @param value value to store at the specified index
     * @throws ArrayIndexOutOfBoundsException if `index < 0`.
     */
    @Synchronized
    fun setElement(index: Int, value: Double) {
        if (index < 0) {
            throw ArrayIndexOutOfBoundsException(index)
        }
        if (index + 1 > numElements) {
            numElements = index + 1
        }
        if (startIndex + index >= arrayRef.size) {
            expandTo(startIndex + (index + 1))
        }
        arrayRef[startIndex + index] = value
    }

    /**
     * Sets the expansionFactor.  Throws IllegalArgumentException if the
     * the following conditions are not met:
     *
     *  * `expansionFactor > 1`
     *  * `contractionFactor >= expansionFactor`
     *
     * @param expansionFactor the new expansion factor value.
     * @throws MathIllegalArgumentException if expansionFactor is <= 1 or greater
     * than contractionFactor
     */
    @Deprecated("As of 3.1 (to be removed in 4.0 as field will become " final ").")
    @Throws(
        MathIllegalArgumentException::class
    )
    fun setExpansionFactor(expansionFactor: Float) {
        checkContractExpand(contractionCriterion, expansionFactor.toDouble())
        // The check above verifies that the expansion factor is > 1.0;
        synchronized(this) { this.expansionFactor = expansionFactor.toDouble() }
    }

    /**
     * Sets the `expansionMode`. The specified value must be one of
     * ADDITIVE_MODE, MULTIPLICATIVE_MODE.
     *
     * @param expansionMode The expansionMode to set.
     * @throws MathIllegalArgumentException if the specified mode value is not valid.
     */
    @Deprecated("As of 3.1. Please use {@link #setExpansionMode(ExpansionMode)} instead.")
    @Throws(
        MathIllegalArgumentException::class
    )
    fun setExpansionMode(expansionMode: Int) {
        if (expansionMode != MULTIPLICATIVE_MODE &&
            expansionMode != ADDITIVE_MODE
        ) {
            throw MathIllegalArgumentException(
                LocalizedFormats.UNSUPPORTED_EXPANSION_MODE, expansionMode,
                MULTIPLICATIVE_MODE, "MULTIPLICATIVE_MODE",
                ADDITIVE_MODE, "ADDITIVE_MODE"
            )
        }
        synchronized(this) {
            if (expansionMode == MULTIPLICATIVE_MODE) {
                setExpansionMode(ExpansionMode.MULTIPLICATIVE)
            } else if (expansionMode == ADDITIVE_MODE) {
                setExpansionMode(ExpansionMode.ADDITIVE)
            }
        }
    }

    /**
     * Sets the [expansion mode][ExpansionMode].
     *
     * @param expansionMode Expansion mode to use for resizing the array.
     */
    @Deprecated("As of 3.1 (to be removed in 4.0 as field will become " final ").")
    fun setExpansionMode(expansionMode: ExpansionMode) {
        synchronized(this) { this.expansionMode = expansionMode }
    }

    /**
     * Sets the initial capacity.  Should only be invoked by constructors.
     *
     * @param initialCapacity of the array
     * @throws MathIllegalArgumentException if `initialCapacity` is not
     * positive.
     */
    @Deprecated("As of 3.1, this is a no-op.")
    @Throws(MathIllegalArgumentException::class)
    protected fun setInitialCapacity(initialCapacity: Int) {
        // Body removed in 3.1.
    }

    /**
     * This function allows you to control the number of elements contained
     * in this array, and can be used to "throw out" the last n values in an
     * array. This function will also expand the internal array as needed.
     *
     * @param i a new number of elements
     * @throws MathIllegalArgumentException if `i` is negative.
     */
    @Synchronized
    @Throws(MathIllegalArgumentException::class)
    fun setNumElements(i: Int) {
        // If index is negative thrown an error.
        if (i < 0) {
            throw MathIllegalArgumentException(
                LocalizedFormats.INDEX_NOT_POSITIVE,
                i
            )
        }

        // Test the new num elements, check to see if the array needs to be
        // expanded to accommodate this new number of elements.
        val newSize = startIndex + i
        if (newSize > arrayRef.size) {
            expandTo(newSize)
        }

        // Set the new number of elements to new value.
        numElements = i
    }

    /**
     * Returns true if the internal storage array has too many unused
     * storage positions.
     *
     * @return true if array satisfies the contraction criteria
     */
    @Synchronized
    private fun shouldContract(): Boolean {
        return if (expansionMode == ExpansionMode.MULTIPLICATIVE) {
            arrayRef.size / numElements.toFloat() > contractionCriterion
        } else {
            arrayRef.size - numElements > contractionCriterion
        }
    }

    /**
     * Returns the starting index of the internal array.  The starting index is
     * the position of the first addressable element in the internal storage
     * array.  The addressable elements in the array are `
     * internalArray[startIndex],...,internalArray[startIndex + numElements -1]
    ` *
     *
     * @return the starting index.
     */
    @Deprecated("As of 3.1.")
    @Synchronized
    fun start(): Int {
        return startIndex
    }

    /**
     * Returns a copy of the ResizableDoubleArray.  Does not contract before
     * the copy, so the returned object is an exact copy of this.
     *
     * @return a new ResizableDoubleArray with the same data and configuration
     * properties as this
     * @since 2.0
     */
    @Synchronized
    fun copy(): ResizableDoubleArray {
        val result = ResizableDoubleArray()
        copy(this, result)
        return result
    }

    /**
     * Returns true iff object is a ResizableDoubleArray with the same properties
     * as this and an identical internal storage array.
     *
     * @param object object to be compared for equality with this
     * @return true iff object is a ResizableDoubleArray with the same data and
     * properties as this
     * @since 2.0
     */
    override fun equals(`object`: Any?): Boolean {
        if (`object` === this) {
            return true
        }
        if (`object` is ResizableDoubleArray == false) {
            return false
        }
        synchronized(this) {
            synchronized(`object`) {
                var result = true
                val other = `object`
                result = result && other.contractionCriterion == contractionCriterion
                result = result && other.expansionFactor == expansionFactor
                result = result && other.expansionMode == expansionMode
                result = result && other.numElements == numElements
                result = result && other.startIndex == startIndex
                return if (!result) {
                    false
                } else {
                    Arrays.equals(arrayRef, other.arrayRef)
                }
            }
        }
    }

    /**
     * Returns a hash code consistent with equals.
     *
     * @return the hash code representing this `ResizableDoubleArray`.
     * @since 2.0
     */
    @Synchronized
    override fun hashCode(): Int {
        val hashData = IntArray(6)
        hashData[0] = java.lang.Double.valueOf(expansionFactor).hashCode()
        hashData[1] = java.lang.Double.valueOf(contractionCriterion).hashCode()
        hashData[2] = expansionMode.hashCode()
        hashData[3] = Arrays.hashCode(arrayRef)
        hashData[4] = numElements
        hashData[5] = startIndex
        return Arrays.hashCode(hashData)
    }

    companion object {
        /** Additive expansion mode.
         */
        @Deprecated("As of 3.1. Please use {@link ExpansionMode#ADDITIVE} instead.")
        val ADDITIVE_MODE = 1

        /** Multiplicative expansion mode.
         */
        @Deprecated("As of 3.1. Please use {@link ExpansionMode#MULTIPLICATIVE} instead.")
        val MULTIPLICATIVE_MODE = 0

        /** Serializable version identifier.  */
        private const val serialVersionUID = -3485529955529426875L

        /** Default value for initial capacity.  */
        private const val DEFAULT_INITIAL_CAPACITY = 16

        /** Default value for array size modifier.  */
        private const val DEFAULT_EXPANSION_FACTOR = 2.0

        /**
         * Default value for the difference between [.contractionCriterion]
         * and [.expansionFactor].
         */
        private const val DEFAULT_CONTRACTION_DELTA = 0.5

        /**
         *
         * Copies source to dest, copying the underlying data, so dest is
         * a new, independent copy of source.  Does not contract before
         * the copy.
         *
         *
         * Obtains synchronization locks on both source and dest
         * (in that order) before performing the copy.
         *
         *
         * Neither source nor dest may be null; otherwise a [NullArgumentException]
         * is thrown
         *
         * @param source ResizableDoubleArray to copy
         * @param dest ResizableArray to replace with a copy of the source array
         * @exception NullArgumentException if either source or dest is null
         * @since 2.0
         */
        @Throws(NullArgumentException::class)
        fun copy(
            source: ResizableDoubleArray,
            dest: ResizableDoubleArray
        ) {
            checkNotNull(source)
            checkNotNull(dest)
            synchronized(source) {
                synchronized(dest) {
                    dest.contractionCriterion = source.contractionCriterion
                    dest.expansionFactor = source.expansionFactor
                    dest.expansionMode = source.expansionMode
                    dest.arrayRef = DoubleArray(source.arrayRef.size)
                    java.lang.System.arraycopy(
                        source.arrayRef, 0, dest.arrayRef,
                        0, dest.arrayRef.size
                    )
                    dest.numElements = source.numElements
                    dest.startIndex = source.startIndex
                }
            }
        }
    }
}
