package com.severett.kemver

import com.severett.kemver.Tokenizers.STRICT

private val STRICT_REGEX = Regex(STRICT)
private val COERCE_REGEX = Regex("(^|\\D)(\\d{1,16})(?:\\.(\\d{1,16}))?(?:\\.(\\d{1,16}))?(?:\$|\\D)")

class Semver : Comparable<Semver> {
    val major: Int
    val minor: Int
    val patch: Int
    val preRelease: List<String>
    val build: List<String>
    val isStable: Boolean
        get() = major > 0 && preRelease.isEmpty()
    val version: String

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
        this.version = createVersion(major, minor, patch, preRelease, build)
    }

    constructor(versionStr: String) {
        val matchResult = STRICT_REGEX.find(versionStr)
            ?: throw SemverException("Version [$versionStr] is not a valid semver.")

        major = parseInt(matchResult.groupValues[1])
        minor = parseInt(matchResult.groupValues[2])
        patch = parseInt(matchResult.groupValues[3])
        preRelease = parseList(matchResult.groupValues[4])
        build = parseList(matchResult.groupValues[5])
        version = createVersion(major, minor, patch, preRelease, build)
    }

    fun nextMajor() = Semver(
        // Prerelease version 1.0.0-5 bumps to 1.0.0
        major = if (minor != 0 || patch != 0 || preRelease.isEmpty()) major + 1 else major,
        minor = 0,
        patch = 0,
        build = build,
    )

    fun withIncMajor(number: Int = 1) = Semver(
        major = major + number,
        minor = minor,
        patch = patch,
        preRelease = preRelease,
        build = build,
    )

    fun nextMinor() = Semver(
        major = major,
        // Prerelease version 1.2.0-5 bumps to 1.2.0
        minor = if (patch != 0 || preRelease.isEmpty()) minor + 1 else minor,
        patch = 0,
        build = build,
    )

    fun withIncMinor(number: Int = 1) = Semver(
        major = major,
        minor = minor + number,
        patch = patch,
        preRelease = preRelease,
        build = build,
    )

    fun nextPatch() = Semver(
        major = major,
        minor = minor,
        // Prerelease version 1.2.0-5 bumps to 1.2.0
        patch = if (preRelease.isEmpty()) patch + 1 else patch,
        build = build,
    )

    fun withIncPatch(number: Int = 1) = Semver(
        major = major,
        minor = minor,
        patch = patch + number,
        preRelease = preRelease,
        build = build,
    )

    fun withPreRelease(newPreRelease: List<String>) = Semver(
        major = major,
        minor = minor,
        patch = patch,
        preRelease = newPreRelease,
        build = build,
    )

    fun withPreRelease(newPreRelease: String) = withPreRelease(newPreRelease.split("."))

    fun withBuild(newBuild: List<String>) = Semver(
        major = major,
        minor = minor,
        patch = patch,
        preRelease = preRelease,
        build = newBuild,
    )

    fun withBuild(newBuild: String) = withBuild(newBuild.split("."))

    fun withClearedPreRelease() = withPreRelease(emptyList())

    fun withClearedBuild() = withBuild(emptyList())

    fun withClearedPreReleaseAndBuild() = Semver(major = major, minor = minor, patch = patch)

    fun diff(version: String) = diff(Semver(version))

    fun diff(version: Semver) = when {
        major != version.major -> VersionDiff.MAJOR
        minor != version.minor -> VersionDiff.MINOR
        patch != version.patch -> VersionDiff.PATCH
        preRelease != version.preRelease -> VersionDiff.PRE_RELEASE
        build != version.build -> VersionDiff.BUILD
        else -> VersionDiff.NONE
    }

    fun isApiCompatible(version: Semver) = diff(version) < VersionDiff.MAJOR

    fun isApiCompatible(version: String) = isApiCompatible(Semver(version))

    override fun compareTo(other: Semver) = SemverComparator.compare(this, other)

    infix fun isGreaterThan(other: Semver) = this > other

    infix fun isGreaterThan(other: String) = isGreaterThan(Semver(other))

    infix fun isGreaterThanOrEqualTo(other: Semver) = this >= other

    infix fun isGreaterThanOrEqualTo(other: String) = isGreaterThanOrEqualTo(Semver(other))

    infix fun isLowerThan(other: Semver) = this < other

    infix fun isLowerThan(other: String) = isLowerThan(Semver(other))

    infix fun isLowerThanOrEqualTo(other: Semver) = this <= other

    infix fun isLowerThanOrEqualTo(other: String) = isLowerThanOrEqualTo(Semver(other))

    infix fun isEqualTo(other: Semver) = this == other

    infix fun isEqualTo(other: String) = isEqualTo(Semver(other))

    infix fun isEquivalentTo(other: Semver) = compareTo(other) == 0

    infix fun isEquivalentTo(other: String) = isEquivalentTo(Semver(other))

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

    override fun toString() = version

    companion object {
        @JvmStatic
        val ZERO = Semver(major = 0, minor = 0, patch = 0)

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

    enum class VersionDiff {
        NONE,
        BUILD,
        PRE_RELEASE,
        PATCH,
        MINOR,
        MAJOR,
    }
}

private fun parseInt(intStr: String) = intStr.toIntOrNull()
    ?.takeIf { it >= 0 }
    ?: throw SemverException("Value [$intStr] must be a number between 0 and ${Int.MAX_VALUE}")

private fun parseList(listStr: String) = listStr.split(".").filter { it.trim().isNotEmpty() }

private fun createVersion(major: Int, minor: Int, patch: Int, preRelease: List<String>, build: List<String>): String {
    return buildString {
        append("$major.$minor.$patch")
        if (preRelease.isNotEmpty()) append("-${preRelease.joinToString(".")}")
        if (build.isNotEmpty()) append("+${build.joinToString(".")}")
    }
}
