package com.severett.kemver

// TODO Refactor into using Kotlin-based DSL for builder
class RangesExpression internal constructor(range: Range) {
    private val rangesList = RangesList()
    private val andOperationRanges = mutableListOf(range)

    companion object {
        @JvmStatic
        fun equal(version: String) = equal(Semver(version))

        @JvmStatic
        fun equal(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.EQ))

        @JvmStatic
        fun greater(version: String) = greater(Semver(version))

        @JvmStatic
        fun greater(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.GT))

        @JvmStatic
        fun greaterOrEqual(version: String) = greaterOrEqual(Semver(version))

        @JvmStatic
        fun greaterOrEqual(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.GTE))

        @JvmStatic
        fun less(version: String) = less(Semver(version))

        @JvmStatic
        fun less(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.LT))

        @JvmStatic
        fun lessOrEqual(version: String) = lessOrEqual(Semver(version))

        @JvmStatic
        fun lessOrEqual(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.LTE))
    }

    fun and(rangeExpression: RangesExpression): RangesExpression {
        val rangesList = rangeExpression.get()
        val lists = rangesList.get()
        lists.forEach { list ->
            andOperationRanges.addAll(list)
            if (lists.size > 1) flushAndClearAndOperationRangesToRangesList()
        }
        return this
    }

    fun or(rangeExpression: RangesExpression): RangesExpression {
        flushAndClearAndOperationRangesToRangesList()
        return and(rangeExpression)
    }

    internal fun get(): RangesList {
        if (andOperationRanges.isNotEmpty()) flushAndClearAndOperationRangesToRangesList()
        return rangesList
    }

    private fun flushAndClearAndOperationRangesToRangesList() {
        rangesList.add(andOperationRanges.toList())
        andOperationRanges.clear()
    }
}