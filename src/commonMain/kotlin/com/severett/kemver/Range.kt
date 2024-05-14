package com.severett.kemver

/**
 * Represents a single range item.
 */
data class Range(val rangeVersion: Semver, private val rangeOperator: RangeOperator) {
    /**
     * Check for whether this range is satisfied by any version.
     */
    val isSatisfiedByAny
        get() = rangeOperator == RangeOperator.GTE && rangeVersion isEqualTo Semver.ZERO

    constructor(rangeVersion: String, rangeOperator: RangeOperator) : this(Semver(rangeVersion), rangeOperator)

    /**
     * Check for whether the range is satisfied by a given version string.
     *
     * @param version the version to check
     * @return `true` if the range is satisfied by the version, otherwise `false`.
     */
    infix fun isSatisfiedBy(version: String) = isSatisfiedBy(Semver(version))

    /**
     * Check for whether the range is satisfied by the given [Semver] instance.
     * @param version the version to check
     * @return `true` if the range is satisfied by the version, otherwise `false`.
     */
    infix fun isSatisfiedBy(version: Semver) =
        when (rangeOperator) {
            RangeOperator.EQ -> version.isEquivalentTo(rangeVersion)
            RangeOperator.LT -> version.isLowerThan(rangeVersion)
            RangeOperator.LTE -> version.isLowerThanOrEqualTo(rangeVersion)
            RangeOperator.GT -> version.isGreaterThan(rangeVersion)
            RangeOperator.GTE -> version.isGreaterThanOrEqualTo(rangeVersion)
        }

    override fun toString() = rangeOperator.asString + rangeVersion

    enum class RangeOperator(private val string: String) {
        /**
         * The version and requirement are equivalent.
         */
        EQ("="),

        /**
         * The version is lower than the requirement.
         */
        LT("<"),

        /**
         * The version is lower than or equivalent to the requirement.
         */
        LTE("<="),

        /**
         * The version is greater than the requirement.
         */
        GT(">"),

        /**
         * The version is greater than or equivalent to the requirement.
         */
        GTE(">="),
        ;

        /**
         * String representation of the range operator.
         */
        val asString = string

        companion object {
            fun value(string: String) =
                if (string.isEmpty()) {
                    EQ
                } else {
                    entries.find { it.string == string }
                        ?: throw IllegalArgumentException("Range operator for '$string' not found")
                }
        }
    }
}
