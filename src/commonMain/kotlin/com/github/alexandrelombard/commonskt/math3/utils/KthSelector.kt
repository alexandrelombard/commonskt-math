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

import com.github.alexandrelombard.commonskt.math3.utils.FastMath.min

/**
 * A Simple K<sup>th</sup> selector implementation to pick up the
 * K<sup>th</sup> ordered element from a work array containing the input
 * numbers.
 * @since 3.4
 */
class KthSelector {
    /** A [PivotingStrategyInterface] used for pivoting   */
    private val pivotingStrategy: PivotingStrategyInterface

    /**
     * Constructor with default [median of 3][MedianOf3PivotingStrategy] pivoting strategy
     */
    constructor() {
        pivotingStrategy = MedianOf3PivotingStrategy()
    }

    /**
     * Constructor with specified pivoting strategy
     *
     * @param pivotingStrategy pivoting strategy to use
     * @see MedianOf3PivotingStrategy
     *
     * @see RandomPivotingStrategy
     *
     * @see CentralPivotingStrategy
     */
    constructor(pivotingStrategy: PivotingStrategyInterface) {
        this.pivotingStrategy = pivotingStrategy
    }

    /** Get the pivotin strategy.
     * @return pivoting strategy
     */
    fun getPivotingStrategy(): PivotingStrategyInterface {
        return pivotingStrategy
    }

    /**
     * Select K<sup>th</sup> value in the array.
     *
     * @param work work array to use to find out the K<sup>th</sup> value
     * @param pivotsHeap cached pivots heap that can be used for efficient estimation
     * @param k the index whose value in the array is of interest
     * @return K<sup>th</sup> value
     */
    fun select(work: DoubleArray, pivotsHeap: IntArray?, k: Int): Double {
        var begin = 0
        var end = work.size
        var node = 0
        val usePivotsHeap = pivotsHeap != null
        while (end - begin > MIN_SELECT_SIZE) {
            val pivot: Int
            if (usePivotsHeap && node < pivotsHeap!!.size && pivotsHeap[node] >= 0
            ) {
                // the pivot has already been found in a previous call
                // and the array has already been partitioned around it
                pivot = pivotsHeap[node]
            } else {
                // select a pivot and partition work array around it
                pivot = partition(work, begin, end, pivotingStrategy.pivotIndex(work, begin, end))
                if (usePivotsHeap && node < pivotsHeap!!.size) {
                    pivotsHeap[node] = pivot
                }
            }
            if (k == pivot) {
                // the pivot was exactly the element we wanted
                return work[k]
            } else if (k < pivot) {
                // the element is in the left partition
                end = pivot
                node = min(2 * node + 1, if (usePivotsHeap) pivotsHeap!!.size else end)
            } else {
                // the element is in the right partition
                begin = pivot + 1
                node = min(2 * node + 2, if (usePivotsHeap) pivotsHeap!!.size else end)
            }
        }
        Arrays.sort(work, begin, end)
        return work[k]
    }

    /**
     * Partition an array slice around a pivot.Partitioning exchanges array
     * elements such that all elements smaller than pivot are before it and
     * all elements larger than pivot are after it.
     *
     * @param work work array
     * @param begin index of the first element of the slice of work array
     * @param end index after the last element of the slice of work array
     * @param pivot initial index of the pivot
     * @return index of the pivot after partition
     */
    private fun partition(work: DoubleArray, begin: Int, end: Int, pivot: Int): Int {
        val value = work[pivot]
        work[pivot] = work[begin]
        var i = begin + 1
        var j = end - 1
        while (i < j) {
            while (i < j && work[j] > value) {
                --j
            }
            while (i < j && work[i] < value) {
                ++i
            }
            if (i < j) {
                val tmp = work[i]
                work[i++] = work[j]
                work[j--] = tmp
            }
        }
        if (i >= end || work[i] > value) {
            --i
        }
        work[begin] = work[i]
        work[i] = value
        return i
    }

    companion object {
        /** Serializable UID.  */
        private const val serialVersionUID = 20140713L

        /** Minimum selection size for insertion sort rather than selection.  */
        private const val MIN_SELECT_SIZE = 15
    }
}
