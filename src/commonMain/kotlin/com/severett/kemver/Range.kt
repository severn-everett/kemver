package com.severett.kemver

data class Range(val rangeVersion: Semver, private val rangeOperator: RangeOperator) {

    val isSatisfiedByAny
        get() = rangeOperator == RangeOperator.GTE && rangeVersion isEqualTo Semver.ZERO

    constructor(rangeVersion: String, rangeOperator: RangeOperator) : this(Semver(rangeVersion), rangeOperator)

    infix fun isSatisfiedBy(version: String) = isSatisfiedBy(Semver(version))

    infix fun isSatisfiedBy(version: Semver) = when(rangeOperator) {
        RangeOperator.EQ -> version.isEquivalentTo(rangeVersion)
        RangeOperator.LT -> version.isLowerThan(rangeVersion)
        RangeOperator.LTE -> version.isLowerThanOrEqualTo(rangeVersion)
        RangeOperator.GT -> version.isGreaterThan(rangeVersion)
        RangeOperator.GTE -> version.isGreaterThanOrEqualTo(rangeVersion)
    }

    override fun toString() = rangeOperator.asString + rangeVersion

    enum class RangeOperator(private val string: String) {
        EQ("="),
        LT("<"),
        LTE("<="),
        GT(">"),
        GTE(">=");

        val asString = string

        companion object {
            fun value(string: String) = if (string.isEmpty()) {
                EQ
            } else {
                entries.find { it.string == string }
                    ?: throw IllegalArgumentException("Range operator for '$string' not found")
            }
        }
    }
}