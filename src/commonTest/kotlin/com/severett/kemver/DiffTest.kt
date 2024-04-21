package com.severett.kemver

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val BASE_MAJOR = 1
private const val BASE_MINOR = 2
private const val BASE_PATCH = 3
private val BASE_PRERELEASE = listOf("PreRelease")
private val BASE_BUILD = listOf("Build")

class DiffTest : FunSpec({
    val baseSemver = Semver(
        major = BASE_MAJOR,
        minor = BASE_MINOR,
        patch = BASE_PATCH,
        preRelease = BASE_PRERELEASE,
        build = BASE_BUILD,
    )

    fun createSemver(
        major: Int = BASE_MAJOR,
        minor: Int = BASE_MINOR,
        patch: Int = BASE_PATCH,
        preRelease: List<String> = BASE_PRERELEASE,
        build: List<String> = BASE_BUILD,
    ) = Semver(major, minor, patch, preRelease, build)

    listOf(
        createSemver(major = 0) to Semver.VersionDiff.MAJOR,
        createSemver(minor = 0) to Semver.VersionDiff.MINOR,
        createSemver(patch = 0) to Semver.VersionDiff.PATCH,
        createSemver(preRelease = emptyList()) to Semver.VersionDiff.PRE_RELEASE,
        createSemver(build = emptyList()) to Semver.VersionDiff.BUILD,
        createSemver() to Semver.VersionDiff.NONE,
    ).forEach { (otherSemver, expectedDiff) ->
        test("Comparing Semver [$otherSemver] to Semver [$baseSemver] should return a diff of [$expectedDiff]") {
            baseSemver.diff(otherSemver) shouldBe expectedDiff
        }
    }

    test("Comparing string [2.2.3] to Semver [$baseSemver] should return a diff of [${Semver.VersionDiff.MAJOR}") {
        baseSemver.diff("2.2.3") shouldBe Semver.VersionDiff.MAJOR
    }

    listOf(
        createSemver(major = 0) to false,
        createSemver(minor = 0) to true,
        createSemver(patch = 0) to true,
        createSemver(preRelease = emptyList()) to true,
        createSemver(build = emptyList()) to true,
    ).forEach { (otherSemver, expectedResult) ->
        test("Comparing API compatibility of Semver [$otherSemver] to Semver [$baseSemver] should produce $expectedResult") {
            baseSemver.isApiCompatible(otherSemver) shouldBe expectedResult
        }
    }

    test("Comparing API compatibility of string [2.2.3] to Semver [$baseSemver] should produce false") {
        baseSemver.isApiCompatible("2.2.3") shouldBe false
    }
})
