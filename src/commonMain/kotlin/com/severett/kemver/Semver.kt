package com.severett.kemver

import kotlin.jvm.JvmStatic

private val STRICT_REGEX =
    Regex(
        "^v?(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)" +
            "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*))?(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?$",
    )
private val COERCE_REGEX = Regex("(^|\\D)(\\d{1,16})(?:\\.(\\d{1,16}))?(?:\\.(\\d{1,16}))?(?:\$|\\D)")

/**
 * Semver is a tool that provides useful methods to manipulate versions that follow the "semantic versioning"
 * specification (see [semver.org](http://semver.org))
 */
class Semver : Comparable<Semver> {
    /**
     * The major part of the version.
     *
     * Example: `1` for `1.2.3-alpha.beta+1234.5678`
     */
    val major: Int

    /**
     * The minor part of the version.
     *
     * Example: `2` for `1.2.3-alpha.beta+1234.5678`
     */
    val minor: Int

    /**
     * The patch part of the version.
     *
     * Example: `3` for `1.2.3-alpha.beta+1234.5678`
     */
    val patch: Int

    /**
     * The pre-release part of the version.
     *
     * Example: `["alpha", "beta"]` for `1.2.3-alpha.beta+1234.5678`
     */
    val preRelease: List<String>

    /**
     * The build part of the version.
     *
     * Example: `["1234", "5678"]` for `1.2.3-alpha.beta+1234.5678`
     */
    val build: List<String>

    /**
     * Whether the current version is stable.
     *
     * A stable version has a major version number [strictly positive](https://semver.org/#spec-item-4)
     * and no [pre-release tokens](https://semver.org/#spec-item-9).
     */
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
        val matchResult =
            STRICT_REGEX.find(versionStr) ?: throw SemverException("Version [$versionStr] is not a valid semver.")

        major = parseInt(matchResult.groupValues[1])
        minor = parseInt(matchResult.groupValues[2])
        patch = parseInt(matchResult.groupValues[3])
        preRelease = parseList(matchResult.groupValues[4])
        build = parseList(matchResult.groupValues[5])
        version = createVersion(major, minor, patch, preRelease, build)
    }

    /**
     * Increments major to the next closest version.
     *
     * @return new incremented semver
     */
    fun nextMajor() =
        Semver(
            // Prerelease version 1.0.0-5 bumps to 1.0.0
            major = if (minor != 0 || patch != 0 || preRelease.isEmpty()) major + 1 else major,
            minor = 0,
            patch = 0,
            build = build,
        )

    /**
     * Increments major by specified amount (`1` by default).
     *
     * @return new incremented semver
     */
    fun withIncMajor(number: Int = 1) =
        Semver(
            major = major + number,
            minor = minor,
            patch = patch,
            preRelease = preRelease,
            build = build,
        )

    /**
     * Increments minor to the next closest version.
     *
     * @return new incremented semver
     */
    fun nextMinor() =
        Semver(
            major = major,
            // Prerelease version 1.2.0-5 bumps to 1.2.0
            minor = if (patch != 0 || preRelease.isEmpty()) minor + 1 else minor,
            patch = 0,
            build = build,
        )

    /**
     * Increments minor by specified amount (`1` by default).
     *
     * @return new incremented semver
     */
    fun withIncMinor(number: Int = 1) =
        Semver(
            major = major,
            minor = minor + number,
            patch = patch,
            preRelease = preRelease,
            build = build,
        )

    /**
     * Increments patch to the next closest version.
     *
     * @return new incremented semver
     */
    fun nextPatch() =
        Semver(
            major = major,
            minor = minor,
            // Prerelease version 1.2.0-5 bumps to 1.2.0
            patch = if (preRelease.isEmpty()) patch + 1 else patch,
            build = build,
        )

    /**
     * Increments patch by specified amount (`1` by default).
     *
     * @return new incremented semver
     */
    fun withIncPatch(number: Int = 1) =
        Semver(
            major = major,
            minor = minor,
            patch = patch + number,
            preRelease = preRelease,
            build = build,
        )

    /**
     * Sets pre-release version.
     *
     * @return semver with new pre-release
     */
    fun withPreRelease(newPreRelease: List<String>) =
        Semver(
            major = major,
            minor = minor,
            patch = patch,
            preRelease = newPreRelease,
            build = build,
        )

    /**
     * Sets pre-release version with dot-delineated string.
     *
     * @return semver with new pre-release
     */
    fun withPreRelease(newPreRelease: String) = withPreRelease(newPreRelease.split("."))

    /**
     * Sets build version.
     *
     * @return semver with new build
     */
    fun withBuild(newBuild: List<String>) =
        Semver(
            major = major,
            minor = minor,
            patch = patch,
            preRelease = preRelease,
            build = newBuild,
        )

    /**
     * Sets build version with dot-delineated string.
     *
     * @return semver with new build
     */
    fun withBuild(newBuild: String) = withBuild(newBuild.split("."))

    /**
     * Removes pre-release from semver
     *
     * @return semver without pre-release
     */
    fun withClearedPreRelease() = withPreRelease(emptyList())

    /**
     * Removes build from semver.
     *
     * @return semver without build
     */
    fun withClearedBuild() = withBuild(emptyList())

    /**
     * Removes both pre-release and build from semver.
     *
     * @return semver without pre-release and build
     */
    fun withClearedPreReleaseAndBuild() = Semver(major = major, minor = minor, patch = patch)

    /**
     * Returns the **greatest** difference between two versions.
     *
     * For example, if the current version is `1.2.3` and the compared version is `1.3.0`, the biggest difference
     * is the **MINOR** number.
     *
     * @param version should be a valid semver string
     * @return the greatest difference
     */
    fun diff(version: String) = diff(Semver(version))

    /**
     * Returns the **greatest** difference between two versions.
     *
     * For example, if the current version is `1.2.3` and the compared version is `1.3.0`, the biggest difference
     * is the **MINOR** number.
     *
     * @param version [Semver] instance to compare
     * @return the greatest difference
     */
    fun diff(version: Semver) =
        when {
            major != version.major -> VersionDiff.MAJOR
            minor != version.minor -> VersionDiff.MINOR
            patch != version.patch -> VersionDiff.PATCH
            preRelease != version.preRelease -> VersionDiff.PRE_RELEASE
            build != version.build -> VersionDiff.BUILD
            else -> VersionDiff.NONE
        }

    /**
     * Checks whether the given version is API compatible with this version.
     */
    fun isApiCompatible(version: Semver) = diff(version) < VersionDiff.MAJOR

    /**
     * Checks whether the given version is API compatible with this version.
     *
     * @param version should be a valid semver string
     */
    fun isApiCompatible(version: String) = isApiCompatible(Semver(version))

    override fun compareTo(other: Semver) = SemverComparator.compare(this, other)

    /**
     * Checks whether the version is greater than another version.
     *
     * @param other [Semver] instance to compare
     */
    infix fun isGreaterThan(other: Semver) = this > other

    /**
     * Checks whether the version is greater than another version.
     *
     * @param other should be a valid semver string
     */
    infix fun isGreaterThan(other: String) = isGreaterThan(Semver(other))

    /**
     * Checks whether the version is greater than or equal to another version.
     *
     * @param other [Semver] instance to compare
     */
    infix fun isGreaterThanOrEqualTo(other: Semver) = this >= other

    /**
     * Checks whether the version is greater than or equal to another version.
     *
     * @param other should be a valid semver string
     */
    infix fun isGreaterThanOrEqualTo(other: String) = isGreaterThanOrEqualTo(Semver(other))

    /**
     * Checks whether the version is lower than another version.
     *
     * @param other [Semver] instance to compare
     */
    infix fun isLowerThan(other: Semver) = this < other

    /**
     * Checks whether the version is lower than another version.
     *
     * @param other should be a valid semver string
     */
    infix fun isLowerThan(other: String) = isLowerThan(Semver(other))

    /**
     * Checks whether the version is lower than or equal to another version.
     *
     * @param other [Semver] instance to compare
     */
    infix fun isLowerThanOrEqualTo(other: Semver) = this <= other

    /**
     * Checks whether the version is lower than or equal to another version.
     *
     * @param other should be a valid semver string
     */
    infix fun isLowerThanOrEqualTo(other: String) = isLowerThanOrEqualTo(Semver(other))

    /**
     * Checks whether the version is equal to another version.
     *
     * @param other [Semver] instance to compare
     */
    infix fun isEqualTo(other: Semver) = this == other

    /**
     * Checks whether the version is equal to another version.
     *
     * @param other should be a valid semver string
     */
    infix fun isEqualTo(other: String) = isEqualTo(Semver(other))

    /**
     * Checks whether the version equals another version without taking the build into account.
     *
     * @param other [Semver] instance to compare
     */
    infix fun isEquivalentTo(other: Semver) = compareTo(other) == 0

    /**
     * Checks whether the version equals another version without taking the build into account.
     *
     * @param other should be a valid semver string
     */
    infix fun isEquivalentTo(other: String) = isEquivalentTo(Semver(other))

    /**
     * Check for whether the version satisfies a range string.
     */
    infix fun satisfies(range: String) = satisfies(RangesListFactory.create(range))

    /**
     * Check for whether the version satisfies a range expression.
     */
    infix fun satisfies(rangesExpression: RangesExpression) = satisfies(RangesListFactory.create(rangesExpression))

    /**
     * Check for whether the version satisfies a ranges list.
     */
    infix fun satisfies(rangesList: RangesList) = rangesList.isSatisfiedBy(this)

    /**
     * Format into a string using custom formatting rules.
     */
    fun format(formatter: (Semver) -> String) = formatter.invoke(this)

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

        /**
         * Try to parse a string as a semver.
         *
         * @param version version string to parse
         * @return [Semver] if valid, `null` otherwise
         */
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

        /**
         * Coerce a string into semver if possible.
         *
         * @param version version string to coerce.
         * @return [Semver] if a version can be coerced, `null` otherwise
         */
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

        /**
         * Checks whether the given string version is valid.
         *
         * @param version version string to check
         * @return `true` if is a valid version, `false` otherwise
         */
        @JvmStatic
        fun isValid(version: String?) = parse(version) != null
    }

    /**
     * Types of diffs between two versions. The higher the ordinal value of the enum, the greater the diff.
     */
    enum class VersionDiff {
        NONE,
        BUILD,
        PRE_RELEASE,
        PATCH,
        MINOR,
        MAJOR,
    }
}

private fun parseInt(intStr: String) =
    intStr.toIntOrNull()
        ?.takeIf { it >= 0 }
        ?: throw SemverException("Value [$intStr] must be a number between 0 and ${Int.MAX_VALUE}")

private fun parseList(listStr: String) = listStr.split(".").filter { it.trim().isNotEmpty() }

private fun createVersion(
    major: Int,
    minor: Int,
    patch: Int,
    preRelease: List<String>,
    build: List<String>,
): String {
    return buildString {
        append("$major.$minor.$patch")
        if (preRelease.isNotEmpty()) append("-${preRelease.joinToString(".")}")
        if (build.isNotEmpty()) append("+${build.joinToString(".")}")
    }
}
