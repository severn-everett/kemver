package com.severett.kemver

import com.severett.kemver.Tokenizers.STRICT

private val STRICT_REGEX = Regex(STRICT)
private val COERCE_REGEX = Regex(
    "(^|\\D)(\\d{1,16})(?:\\.(\\d{1,16}))?(?:\\.(\\d{1,16}))?(?:\$|\\D)"
)

class Semver {
    val major: Int
    val minor: Int
    val patch: Int
    val preRelease: List<String>
    val build: List<String>

    constructor(
        major: Int,
        minor: Int,
        patch: Int,
        preRelease: List<String> = emptyList(),
        build: List<String> = emptyList(),
    ) {
        if (major < 0) throw SemverException("Major [$major] should be >= 0")
        if (minor < 0) throw SemverException("Minor [$minor] should be >= 0")
        if (patch < 0) throw SemverException("Patch [$patch] should be >= 0")
        this.major = major
        this.minor = minor
        this.patch = patch
        this.preRelease = preRelease
        this.build = build
    }

    constructor(versionStr: String) {
        val matchResult = STRICT_REGEX.find(versionStr)
            ?: throw SemverException("Version [$versionStr] is not a valid semver.")

        major = parseInt(matchResult.groupValues[1])
        minor = parseInt(matchResult.groupValues[2])
        patch = parseInt(matchResult.groupValues[3])
        preRelease = parseList(matchResult.groupValues[4])
        build = parseList(matchResult.groupValues[5])
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Semver) return false

        if (major != other.major) return false
        if (minor != other.minor) return false
        if (patch != other.patch) return false
        if (preRelease != other.preRelease) return false
        if (build != other.build) return false

        return true
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + preRelease.hashCode()
        result = 31 * result + build.hashCode()
        return result
    }

    override fun toString(): String {
        return buildString {
            append("$major.$minor.$patch")
            if (preRelease.isNotEmpty()) append("-${preRelease.joinToString(".")}")
            if (build.isNotEmpty()) append("+${build.joinToString(".")}")
        }
    }

    companion object {
        @JvmStatic
        fun parse(version: String?): Semver? {
            return version?.let {
                try {
                    Semver(version)
                } catch (e: Exception) {
                    null
                }
            }
        }

        @JvmStatic
        fun coerce(version: String?): Semver? {
            if (version == null) return null

            return parse(version) ?: COERCE_REGEX.find(version)?.let { matchResult ->
                Semver(
                    major = matchResult.groupValues[2].toInt(),
                    minor = matchResult.groupValues[3].toIntOrNull() ?: 0,
                    patch = matchResult.groupValues[4].toIntOrNull() ?: 0,
                )
            }
        }

        @JvmStatic
        fun isValid(version: String?) = parse(version) != null
    }
}

private fun parseInt(intStr: String) = intStr.toIntOrNull()
    ?.takeIf { it >= 0 }
    ?: throw SemverException("Value [$intStr] must be a number between 0 and ${Int.MAX_VALUE}")

private fun parseList(listStr: String) = listStr.split(".").filter { it.trim().isNotEmpty() }
