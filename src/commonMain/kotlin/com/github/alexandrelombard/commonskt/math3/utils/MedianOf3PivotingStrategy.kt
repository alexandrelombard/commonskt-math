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

import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalArgumentException

/**
 * Classic median of 3 strategy given begin and end indices.
 * @since 3.4
 */
class MedianOf3PivotingStrategy : PivotingStrategyInterface {
    /**{@inheritDoc}
     * This in specific makes use of median of 3 pivoting.
     * @return The index corresponding to a pivot chosen between the
     * first, middle and the last indices of the array slice
     * @throws MathIllegalArgumentException when indices exceeds range
     */
    override fun pivotIndex(work: DoubleArray, begin: Int, end: Int): Int {
        MathArrays.verifyValues(work, begin, end - begin)
        val inclusiveEnd = end - 1
        val middle = begin + (inclusiveEnd - begin) / 2
        val wBegin = work[begin]
        val wMiddle = work[middle]
        val wEnd = work[inclusiveEnd]
        return if (wBegin < wMiddle) {
            if (wMiddle < wEnd) {
                middle
            } else {
                if (wBegin < wEnd) inclusiveEnd else begin
            }
        } else {
            if (wBegin < wEnd) {
                begin
            } else {
                if (wMiddle < wEnd) inclusiveEnd else middle
            }
        }
    }

    companion object {
        /** Serializable UID.  */
        private const val serialVersionUID = 20140713L
    }
}
