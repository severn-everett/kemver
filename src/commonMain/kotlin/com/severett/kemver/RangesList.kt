package com.severett.kemver

private const val OR_JOINER = " or "
private const val AND_JOINER = " and "
private val PARENTHESES_REGEX = Regex("^\\(([^()]+)\\)\$")

class RangesList(rangesList: List<List<Range>> = listOf()) {
    private val rangesList: MutableList<List<Range>> = rangesList.toMutableList()
    val isSatisfiedByAny: Boolean
        get() = rangesList.flatten().all(Range::isSatisfiedByAny)

    fun add(ranges: List<Range>) = this.apply {
        if (ranges.isNotEmpty()) rangesList.add(ranges)
    }

    fun get(): List<List<Range>> = rangesList

    infix fun isSatisfiedBy(version: String) = isSatisfiedBy(Semver(version))

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
