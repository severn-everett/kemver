package com.severett.kemver

class RangesExpression internal constructor(range: Range) {
    private val rangesList = RangesList()
    private val andOperationRanges = mutableListOf(range)

    companion object {
        @JvmStatic
        fun equal(version: String) = equal(Semver(version))

        @JvmStatic
        fun equal(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.EQ))

        @JvmStatic
        fun equal(version: String, builder: RangesExpression.() -> Unit) = equal(Semver(version), builder)

        @JvmStatic
        fun equal(version: Semver, builder: RangesExpression.() -> Unit) = equal(version).apply(builder)

        @JvmStatic
        fun greater(version: String) = greater(Semver(version))

        @JvmStatic
        fun greater(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.GT))

        @JvmStatic
        fun greater(version: String, builder: RangesExpression.() -> Unit) = greater(Semver(version), builder)

        @JvmStatic
        fun greater(version: Semver, builder: RangesExpression.() -> Unit) = greater(version).apply(builder)

        @JvmStatic
        fun greaterOrEqual(version: String) = greaterOrEqual(Semver(version))

        @JvmStatic
        fun greaterOrEqual(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.GTE))

        @JvmStatic
        fun greaterOrEqual(version: String, builder: RangesExpression.() -> Unit) =
            greaterOrEqual(Semver(version), builder)

        @JvmStatic
        fun greaterOrEqual(version: Semver, builder: RangesExpression.() -> Unit) =
            greaterOrEqual(version).apply(builder)

        @JvmStatic
        fun less(version: String) = less(Semver(version))

        @JvmStatic
        fun less(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.LT))

        @JvmStatic
        fun less(version: String, builder: RangesExpression.() -> Unit) =
            less(Semver(version), builder)

        @JvmStatic
        fun less(version: Semver, builder: RangesExpression.() -> Unit) =
            less(version).apply(builder)

        @JvmStatic
        fun lessOrEqual(version: String) = lessOrEqual(Semver(version))

        @JvmStatic
        fun lessOrEqual(version: Semver) = RangesExpression(Range(version, Range.RangeOperator.LTE))

        @JvmStatic
        fun lessOrEqual(version: String, builder: RangesExpression.() -> Unit) =
            lessOrEqual(Semver(version), builder)

        @JvmStatic
        fun lessOrEqual(version: Semver, builder: RangesExpression.() -> Unit) =
            lessOrEqual(version).apply(builder)
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

    fun and(builder: () -> RangesExpression) = and(builder.invoke())

    fun or(rangeExpression: RangesExpression): RangesExpression {
        flushAndClearAndOperationRangesToRangesList()
        return and(rangeExpression)
    }

    fun or(builder: () -> RangesExpression) = or(builder.invoke())

    internal fun get(): RangesList {
        if (andOperationRanges.isNotEmpty()) flushAndClearAndOperationRangesToRangesList()
        return rangesList
    }

    private fun flushAndClearAndOperationRangesToRangesList() {
        rangesList.add(andOperationRanges.toList())
        andOperationRanges.clear()
    }
}