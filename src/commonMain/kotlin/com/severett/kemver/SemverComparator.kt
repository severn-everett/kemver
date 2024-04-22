package com.severett.kemver

import kotlin.math.max

internal object SemverComparator {
    private const val LESS_THAN = -1
    private const val EQUAL = 0
    private const val GREATER_THAN = 1

    private const val ALL_DIGITS = "^\\d+$"
    private const val CONTAINS_DIGITS = ".*\\d.*"
    private const val TRAILING_DIGITS_EXTRACT = "(?<=\\D)(?=\\d)"
    private const val LEADING_DIGITS_EXTRACT = "(?<=\\d)(?=\\D)"

    fun compare(semver: Semver, otherSemver: Semver): Int {
        val mainResult = mainCompare(semver, otherSemver)
        return if (mainResult == EQUAL) {
            preReleaseCompare(semver, otherSemver)
        } else {
            mainResult
        }
    }

    private fun mainCompare(semver: Semver, otherSemver: Semver): Int {
        val majorCompare = semver.major.compareTo(otherSemver.major)
        return if (majorCompare == EQUAL) {
            val minorCompare = semver.minor.compareTo(otherSemver.minor)
            if (minorCompare == EQUAL) {
                semver.patch.compareTo(otherSemver.patch)
            } else {
                minorCompare
            }
        } else {
            majorCompare
        }
    }

    private fun preReleaseCompare(semver: Semver, otherSemver: Semver): Int {
        if (semver.preRelease.isNotEmpty() && otherSemver.preRelease.isEmpty()) {
            return LESS_THAN
        } else if (semver.preRelease.isEmpty()) {
            return if (otherSemver.preRelease.isNotEmpty()) GREATER_THAN else EQUAL
        }

        val maxElements = max(semver.preRelease.size, otherSemver.preRelease.size)

        var i = 0
        do {
            val subPreRelease = semver.preRelease.getOrNull(i)
            val otherSubPreRelease = otherSemver.preRelease.getOrNull(i)

            when {
                subPreRelease == null && otherSubPreRelease == null -> return EQUAL
                otherSubPreRelease == null -> return GREATER_THAN
                subPreRelease == null -> return LESS_THAN
                subPreRelease == otherSubPreRelease -> i++
                else -> return compareIdentifiers(subPreRelease, otherSubPreRelease)
            }
        } while(maxElements > i)

        return EQUAL
    }

    private fun compareIdentifiers(identifier: String, otherIdentifier: String): Int {
        // Only attempt to parse fully/numeric string sequences so that we can avoid
        // raising a costly exception
        if (identifier.matches(ALL_DIGITS.toRegex()) && otherIdentifier.matches(ALL_DIGITS.toRegex())) {
            return identifier.toLong().compareTo(otherIdentifier.toLong())
        }

        if (identifier.matches(CONTAINS_DIGITS.toRegex()) && otherIdentifier.matches(CONTAINS_DIGITS.toRegex())) {
            val alphaNumericComparison = checkAlphanumericPreRelease(identifier, otherIdentifier)
            if (alphaNumericComparison != null) {
                return alphaNumericComparison
            }
        }

        return identifier.compareTo(otherIdentifier)
    }

    private fun checkAlphanumericPreRelease(preRelease: String, otherPreRelease: String): Int? {
        val tokenArr = preRelease.split(TRAILING_DIGITS_EXTRACT)
        val otherTokenArr = otherPreRelease.split(TRAILING_DIGITS_EXTRACT)
        return if (tokenArr[0] == otherTokenArr[0]) {
            val leadingDigitsArr = tokenArr[1].split(LEADING_DIGITS_EXTRACT)
            val otherLeadingDigitsArr = otherTokenArr[1].split(LEADING_DIGITS_EXTRACT)
            val digitComparison = leadingDigitsArr[0].toLong().compareTo(otherLeadingDigitsArr[0].toLong())
            if (digitComparison != 0) {
                digitComparison
            } else if (leadingDigitsArr.size != otherLeadingDigitsArr.size) {
                leadingDigitsArr.size - otherLeadingDigitsArr.size
            } else {
                compareIdentifiers(
                    identifier = preRelease.substring(preRelease.indexOf(leadingDigitsArr[0]) + 1),
                    otherIdentifier = otherPreRelease.substring(otherPreRelease.indexOf(otherLeadingDigitsArr[0]) + 1),
                )
            }
        } else {
            null
        }
    }
}
