package com.severett.kemver

import kotlin.jvm.JvmStatic

/**
 * The internal expression class used to create ranges.
 *
 * Allows the creation of ranges using a fluent interface.
 *
 * Usage:
 * ```
 * equal("1.0.0")
 *   .and(less("2.0.0"))
 *   .or(greaterOrEqual("3.0.0"))
 * ```
 * or:
 * ```
 * equal("1.0.0") {
 *   and { less("2.0.0") }
 *   or { greaterOrEqual("3.0.0") }
 * }
 * ```
 * Will produce range:
 * ```
 * (=1.0.0 and <2.0.0) or >=3.0.0
 * ```
 */
class RangesExpression internal constructor(range: Range) {
    private val rangesList = RangesList()
    private val andOperationRanges = mutableListOf(range)

    /**
     * Allows joining ranges using `AND` operator.
     */
    fun and(rangeExpression: RangesExpression): RangesExpression {
        val rangesList = rangeExpression.get()
        val lists = rangesList.get()
        lists.forEach { list ->
            andOperationRanges.addAll(list)
            if (lists.size > 1) flushAndClearAndOperationRangesToRangesList()
        }
        return this
    }

    /**
     * Allows joining ranges using `AND` operator on passed-in [RangesExpression] builder lambda.
     */
    fun and(builder: () -> RangesExpression) = and(builder.invoke())

    /**
     * Allows joining ranges using `OR` operator.
     */
    fun or(rangeExpression: RangesExpression): RangesExpression {
        flushAndClearAndOperationRangesToRangesList()
        return and(rangeExpression)
    }

    /**
     * Allows joining ranges using `OR` operator on passed-in [RangesExpression] builder lambda.
     */
    fun or(builder: () -> RangesExpression) = or(builder.invoke())

    internal fun get(): RangesList {
        if (andOperationRanges.isNotEmpty()) flushAndClearAndOperationRangesToRangesList()
        return rangesList
    }

    private fun flushAndClearAndOperationRangesToRangesList() {
        rangesList.add(andOperationRanges.toList())
        andOperationRanges.clear()
    }

    companion object {
        /**
         * Expression for equal range item.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun equal(version: String) = equal(Semver(version))

        /**
         * Expression for equal range item.
         */
        @JvmStatic
        fun equal(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.EQ))

        /**
         * Expression for equal range item and accompanying builder lambda.
         *
         * @param version should be a valid semver string.
         */
        @JvmStatic
        fun equal(
            version: String,
            builder: RangesExpression.() -> Unit,
        ) = equal(Semver(version), builder)

        /**
         * Expression for equal range item and accompanying builder lambda.
         */
        @JvmStatic
        fun equal(
            version: Semver,
            builder: RangesExpression.() -> Unit,
        ) = equal(version).apply(builder)

        /**
         * Expression for greater range item.
         *
         * @param version should be a valid semver string.
         */
        @JvmStatic
        fun greater(version: String) = greater(Semver(version))

        /**
         * Expression for greater range item.
         */
        @JvmStatic
        fun greater(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.GT))

        /**
         * Expression for greater range item and accompanying builder lambda.
         *
         * @param version should be a valid semver string.
         */
        @JvmStatic
        fun greater(
            version: String,
            builder: RangesExpression.() -> Unit,
        ) = greater(Semver(version), builder)

        /**
         * Expression for greater range item and accompanying builder lambda.
         */
        @JvmStatic
        fun greater(
            version: Semver,
            builder: RangesExpression.() -> Unit,
        ) = greater(version).apply(builder)

        /**
         * Expression for greater or equal range item.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun greaterOrEqual(version: String) = greaterOrEqual(Semver(version))

        /**
         * Expression for greater or equal range item.
         */
        @JvmStatic
        fun greaterOrEqual(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.GTE))

        /**
         * Expression for greater or equal range item and accompanying builder lambda.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun greaterOrEqual(
            version: String,
            builder: RangesExpression.() -> Unit,
        ) = greaterOrEqual(Semver(version), builder)

        /**
         * Expression for greater or equal range item and accompanying builder lambda.
         */
        @JvmStatic
        fun greaterOrEqual(
            version: Semver,
            builder: RangesExpression.() -> Unit,
        ) = greaterOrEqual(version).apply(builder)

        /**
         * Expression for less range item.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun less(version: String) = less(Semver(version))

        /**
         * Expression for less range item.
         */
        @JvmStatic
        fun less(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.LT))

        /**
         * Expression for less range item and accompanying builder lambda.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun less(
            version: String,
            builder: RangesExpression.() -> Unit,
        ) = less(Semver(version), builder)

        /**
         * Expression for less range item and accompanying builder lambda.
         */
        @JvmStatic
        fun less(
            version: Semver,
            builder: RangesExpression.() -> Unit,
        ) = less(version).apply(builder)

        /**
         * Expression for less or equal range item.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun lessOrEqual(version: String) = lessOrEqual(Semver(version))

        /**
         * Expression for less or equal range item.
         */
        @JvmStatic
        fun lessOrEqual(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.LTE))

        /**
         * Expression for less or equal range item and accompanying builder lambda.
         *
         * @param version should be a valid semver string
         */
        @JvmStatic
        fun lessOrEqual(
            version: String,
            builder: RangesExpression.() -> Unit,
        ) = lessOrEqual(Semver(version), builder)

        /**
         * Expression for less or equal range item and accompanying builder lambda.
         */
        @JvmStatic
        fun lessOrEqual(
            version: Semver,
            builder: RangesExpression.() -> Unit,
        ) = lessOrEqual(version).apply(builder)
    }
}
