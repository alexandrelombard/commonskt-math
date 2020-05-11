package com.github.alexandrelombard.commonskt.math3.utils
import com.github.alexandrelombard.commonskt.math3.exception.MathIllegalArgumentException


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
import org.apache.commons.math3.exception.MathIllegalArgumentException


/**
 * A mid point strategy based on the average of begin and end indices.
 * @since 3.4
 */
class CentralPivotingStrategy : PivotingStrategyInterface, java.io.Serializable {
    /**
     * {@inheritDoc}
     * This in particular picks a average of begin and end indices
     * @return The index corresponding to a simple average of
     * the first and the last element indices of the array slice
     * @throws MathIllegalArgumentException when indices exceeds range
     */
    @Throws(MathIllegalArgumentException::class)
    fun pivotIndex(work: DoubleArray?, begin: Int, end: Int): Int {
        MathArrays.verifyValues(work, begin, end - begin)
        return begin + (end - begin) / 2
    }

    companion object {
        /** Serializable UID.  */
        private const val serialVersionUID = 20140713L
    }
}
