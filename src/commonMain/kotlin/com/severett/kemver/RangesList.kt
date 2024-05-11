package com.severett.kemver

private const val OR_JOINER = " or "
private const val AND_JOINER = " and "
private val PARENTHESES_REGEX = Regex("^\\(([^()]+)\\)\$")

/**
 * Represents a set of single [Range]'s.
 *
 * It's a very convenient way to handle a complex ranges string.
 *
 * The following range:
 * ```
 * <=2.6.8 || >=3.0.0 <=3.0.1
 * ```
 * is parsed into:
 * ```
 * RangesList[
 *   rangesList=[
 *     [<=2.6.8],
 *     [>=3.0.0, <=3.0.1]
 *   ]
 * ]
 * ```
 * That means that one of two ranges `[>=2.6.8]` or `[>=3.0.0, <=3.0.1]` must be fully applied.
 * Given this example, the following versions pass:
 * * `2.6.8` - because of range `[>=2.6.8]`
 * * `3.0.0` - because of range `[>=3.0.0, <=3.0.1]`
 * * `3.0.1` - because of range `[>=3.0.0, <=3.0.1]`
 */
class RangesList(rangesList: List<List<Range>> = listOf()) {
    private val rangesList: MutableList<List<Range>> = rangesList.toMutableList()

    /**
     * Check whether this ranges list is satisfied by any version.
     */
    val isSatisfiedByAny: Boolean
        get() = rangesList.flatten().all(Range::isSatisfiedByAny)

    /**
     * Add ranges to ranges list.
     */
    fun add(ranges: List<Range>) = this.apply {
        if (ranges.isNotEmpty()) rangesList.add(ranges)
    }

    /**
     * Return the list of range lists
     */
    fun get(): List<List<Range>> = rangesList

    /**
     * Check whether this ranges list is satisfied by the given version.
     *
     * @param version should be a valid semver string
     */
    infix fun isSatisfiedBy(version: String) = isSatisfiedBy(Semver(version))

    /**
     * check whether this ranges list is satisfied by the given version.
     */
    infix fun isSatisfiedBy(version: Semver) = rangesList.any {
        isSingleSetOfRangesIsSatisfied(ranges = it, version = version)
    }

    override fun toString() = rangesList.joinToString(OR_JOINER, transform = ::formatRanges)
        .replace(regex = PARENTHESES_REGEX, replacement = "\$1")
}

private fun formatRanges(ranges: List<Range>): String {
    val representation = ranges.joinToString(AND_JOINER, transform = Range::toString)

    return if (ranges.size > 1) "($representation)" else representation
}

private fun isSingleSetOfRangesIsSatisfied(ranges: List<Range>, version: Semver) = when {
    ranges.any { !it.isSatisfiedBy(version) } -> false
    version.preRelease.isNotEmpty() -> {
        ranges.any { range ->
            val rangeSemver = range.rangeVersion
            rangeSemver.preRelease.isNotEmpty() &&
                    version.major == rangeSemver.major &&
                    version.minor == rangeSemver.minor &&
                    version.patch == rangeSemver.patch
        }
    }

    else -> true
}
