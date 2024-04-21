package com.severett.kemver.extensions

import com.severett.kemver.Semver

internal object Modifier {
    fun Semver.nextMajor() = Semver(
        // Prerelease version 1.0.0-5 bumps to 1.0.0
        major = if (minor != 0 || patch != 0 || preRelease.isEmpty()) major + 1 else major,
        minor = 0,
        patch = 0,
        build = build,
    )

    fun Semver.withIncMajor(number: Int) = Semver(
        major = major + number,
        minor = minor,
        patch = patch,
        preRelease = preRelease,
        build = build,
    )

    fun Semver.nextMinor() = Semver(
        major = major,
        // Prerelease version 1.2.0-5 bumps to 1.2.0
        minor = if (patch != 0 || preRelease.isEmpty()) minor + 1 else minor,
        patch = 0,
        build = build,
    )

    fun Semver.withIncMinor(number: Int) = Semver(
        major = major,
        minor = minor + number,
        patch = patch,
        preRelease = preRelease,
        build = build,
    )

    fun Semver.nextPatch() = Semver(
        major = major,
        minor = minor,
        // Prerelease version 1.2.0-5 bumps to 1.2.0
        patch = if (preRelease.isEmpty()) patch + 1 else patch,
        build = build,
    )

    fun Semver.withIncPatch(number: Int) = Semver(
        major = major,
        minor = minor,
        patch = patch + number,
        preRelease = preRelease,
        build = build,
    )

    fun Semver.withPreRelease(newPreRelease: List<String>) = Semver(
        major = major,
        minor = minor,
        patch = patch,
        preRelease = newPreRelease,
        build = build,
    )

    fun Semver.withPreRelease(newPreRelease: String) = withPreRelease(newPreRelease.split("."))

    fun Semver.withBuild(newBuild: List<String>) = Semver(
        major = major,
        minor = minor,
        patch = patch,
        preRelease = preRelease,
        build = newBuild,
    )

    fun Semver.withBuild(newBuild: String) = withBuild(newBuild.split("."))

    fun Semver.withClearedPreRelease() = withPreRelease(emptyList())

    fun Semver.withClearedBuild() = withBuild(emptyList())

    fun Semver.withClearedPreReleaseAndBuild() = Semver(major = major, minor = minor, patch = patch)
}
