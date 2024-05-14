package com.severett.kemver

import com.severett.kemver.util.stripDots
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe

class SemverTest : FunSpec({
    listOf(
        "0.0.4" to Semver(0, 0, 4),
        "1.2.3" to Semver(1, 2, 3),
        "10.20.30" to Semver(10, 20, 30),
        "1.1.2-prerelease+meta" to Semver(1, 1, 2, listOf("prerelease"), listOf("meta")),
        "1.1.2--prerelease+meta" to Semver(1, 1, 2, listOf("-prerelease"), listOf("meta")),
        "1.0.0-alpha" to Semver(1, 0, 0, preRelease = listOf("alpha")),
        "1.0.0-alpha-beta" to Semver(1, 0, 0, preRelease = listOf("alpha-beta")),
        "1.0.0-alpha.beta" to Semver(1, 0, 0, preRelease = listOf("alpha", "beta")),
        "1.0.0+alpha" to Semver(1, 0, 0, build = listOf("alpha")),
        "1.0.0+alpha-beta" to Semver(1, 0, 0, build = listOf("alpha-beta")),
        "1.0.0+alpha.beta" to Semver(1, 0, 0, build = listOf("alpha", "beta")),
    ).forEach { (input, expected) ->
        test("[$input] should be equal to $expected".stripDots()) {
            Semver(input) shouldBeEqual expected
        }
    }

    listOf(
        "1",
        "1.2",
        "1.2.3-0123",
        "1.2.3-0123.0123",
        "1.1.2+.123",
        "+invalid",
        "-invalid",
        "-invalid+invalid",
        "-invalid .01",
        "alpha",
        "alpha.beta",
        "alpha.beta.1",
        "alpha.1",
        "alpha+beta",
        "alpha_beta",
        "alpha.",
        "alpha..",
        "1.0.0-alpha_beta",
        " - alpha.",
        "1.0.0-alpha..",
        "1.0.0-alpha..1",
        "1.0.0-alpha...1",
        "1.0.0-alpha....1",
        "1.0.0-alpha.....1",
        "1.0.0-alpha......1",
        "1.0.0-alpha.......1",
        "01.1.1",
        "1.01.1",
        "1.1.01",
        "1.2.3.DEV",
        "1.2-SNAPSHOT",
        "1.2.31.2.3----RC-SNAPSHOT.12.09.1--..12+788",
        "1.2-RC-SNAPSHOT",
        "-1.0.3-gamma+b7718",
        "+justmeta",
        "9.8.7+meta+meta",
        "9.8.7-whatever+meta+meta",
        "99999999999999999999999.999999999999999999.99999999999999999----RC-SNAPSHOT.12.09.1--------------------------------..12",
        "1.1.1.1",
    ).forEach { invalid ->
        test("Parsing invalid string [$invalid] should throw an exception".stripDots()) {
            val thrownException = shouldThrow<SemverException> { Semver(invalid) }
            thrownException.message shouldBe "Version [$invalid] is not a valid semver."
        }
    }

    data class InvalidNumberParams(val title: String, val semverInit: () -> Semver, val expectedMessage: String)

    listOf(
        InvalidNumberParams(
            title = "Invalid number string should cause a parsing exception",
            semverInit = { Semver("99999999999999999999999.1.1") },
            expectedMessage = "Value [99999999999999999999999] must be a number between 0 and ${Int.MAX_VALUE}",
        ),
        InvalidNumberParams(
            title = "Invalid major should cause an instantiation exception",
            semverInit = { Semver(-1, 0, 0) },
            expectedMessage = "Major [-1] should be >= 0",
        ),
        InvalidNumberParams(
            title = "Invalid minor should cause an instantiation exception",
            semverInit = { Semver(0, -1, 0) },
            expectedMessage = "Minor [-1] should be >= 0",
        ),
        InvalidNumberParams(
            title = "Invalid patch should cause an instantiation exception",
            semverInit = { Semver(0, 0, -1) },
            expectedMessage = "Patch [-1] should be >= 0",
        ),
    ).forEach { (title, semverInit, expectedMessage) ->
        test(title.stripDots()) {
            val thrownException = shouldThrow<SemverException>(semverInit)
            thrownException.message shouldBe expectedMessage
        }
    }

    test("It should parse a valid version string successfully".stripDots()) {
        Semver.parse("1.0.0") shouldBe Semver(1, 0, 0)
    }

    listOf("INVALID", null).forEach { invalid ->
        test("It should return null when parsing an invalid version string of [$invalid]".stripDots()) {
            Semver.parse(invalid) shouldBe null
        }
    }

    listOf(
        "1.0.0" to true,
        "INVALID" to false,
    ).forEach { (versionStr, expectedResult) ->
        test("It should return $expectedResult when determining validity of [$versionStr]".stripDots()) {
            Semver.isValid(versionStr) shouldBe expectedResult
        }
    }

    listOf(
        ".1" to Semver("1.0.0"),
        ".1." to Semver("1.0.0"),
        "..1" to Semver("1.0.0"),
        ".1.1" to Semver("1.1.0"),
        "1." to Semver("1.0.0"),
        "1.0" to Semver("1.0.0"),
        "1.0.0" to Semver("1.0.0"),
        "0" to Semver("0.0.0"),
        "0.0" to Semver("0.0.0"),
        "0.0.0" to Semver("0.0.0"),
        "0.1" to Semver("0.1.0"),
        "0.0.1" to Semver("0.0.1"),
        "0.1.1" to Semver("0.1.1"),
        "1" to Semver("1.0.0"),
        "1.2" to Semver("1.2.0"),
        "1.2.3" to Semver("1.2.3"),
        "1.2.3.4" to Semver("1.2.3"),
        "13" to Semver("13.0.0"),
        "35.12" to Semver("35.12.0"),
        "35.12.18" to Semver("35.12.18"),
        "35.12.18.24" to Semver("35.12.18"),
        "v1" to Semver("1.0.0"),
        "v1.2" to Semver("1.2.0"),
        "v1.2.3" to Semver("1.2.3"),
        "v1.2.3.4" to Semver("1.2.3"),
        " 1" to Semver("1.0.0"),
        "1 " to Semver("1.0.0"),
        "1 0" to Semver("1.0.0"),
        "1 1" to Semver("1.0.0"),
        "1.1 1" to Semver("1.1.0"),
        "1.1-1" to Semver("1.1.0"),
        "a1" to Semver("1.0.0"),
        "a1a" to Semver("1.0.0"),
        "1a" to Semver("1.0.0"),
        "version 1" to Semver("1.0.0"),
        "version1" to Semver("1.0.0"),
        "version1.0" to Semver("1.0.0"),
        "version1.1" to Semver("1.1.0"),
        "42.6.7.9.3-alpha" to Semver("42.6.7"),
        "v2" to Semver("2.0.0"),
        "v3.4 replaces v3.3.1" to Semver("3.4.0"),
        "4.6.3.9.2-alpha2" to Semver("4.6.3"),
        "${"1".repeat(17)}.2" to Semver("2.0.0"),
        "${"1".repeat(17)}.2.3" to Semver("2.3.0"),
        "1.${"2".repeat(17)}.3" to Semver("1.0.0"),
        "1.2.${"3".repeat(17)}" to Semver("1.2.0"),
        "${"1".repeat(17)}.2.3.4" to Semver("2.3.4"),
        "1.${"2".repeat(17)}.3.4" to Semver("1.0.0"),
        "1.2.${"3".repeat(17)}.4" to Semver("1.2.0"),
        "10" to Semver("10.0.0"),
        "3.2.1-rc.2" to Semver("3.2.1-rc.2"),
        null to null,
        "INVALID" to null,
    ).forEach { (versionStr, expectedResult) ->
        test("It should coerce [$versionStr] to Semver [$expectedResult]".stripDots()) {
            Semver.coerce(versionStr) shouldBe expectedResult
        }
    }

    listOf(
        Semver(1, 0, 0) to true,
        Semver(0, 1, 0) to false,
        Semver(1, 0, 0, preRelease = listOf("PRE_RELEASE")) to false,
    ).forEach { (semver, expectedIsStable) ->
        test("isStable field for Semver [$semver] should equal $expectedIsStable".stripDots()) {
            semver.isStable shouldBe expectedIsStable
        }
    }

    test("Semver should allow for custom formatting".stripDots()) {
        val semver =
            Semver(
                major = 1,
                minor = 2,
                patch = 3,
                preRelease = listOf("alpha", "beta"),
                build = listOf("a1", "b2"),
            )
        val semverStr =
            semver.format { s ->
                "${s.major}:${s.minor}:${s.patch}|" +
                    "${s.preRelease.joinToString("&")}*${s.build.joinToString("&")}"
            }

        semverStr shouldBe "1:2:3|alpha&beta*a1&b2"
    }

    data class SatisfiesStrArgs(val version: String = "1.2.3", val rangeStr: String, val expectedSatisfies: Boolean)

    listOf(
        // Fully-qualified versions
        SatisfiesStrArgs(rangeStr = "1.2.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.2.4", expectedSatisfies = false),
        SatisfiesStrArgs(
            version = "1.0.0-setup-20220428123901",
            rangeStr = "1.0.0-setup-20220428123901",
            expectedSatisfies = true,
        ),
        // Minor versions
        SatisfiesStrArgs(rangeStr = "1.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.3", expectedSatisfies = false),
        // Major versions
        SatisfiesStrArgs(rangeStr = "1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "2", expectedSatisfies = false),
        // Hyphen ranges
        SatisfiesStrArgs(rangeStr = "1.2.3 - 2.3.4", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.0.0 - 2.0.0", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.0.0 - 1.2.3", expectedSatisfies = true),
        SatisfiesStrArgs(version = "1.2.3-alpha", rangeStr = "1.2.3 - 2.3.4", expectedSatisfies = false),
        SatisfiesStrArgs(version = "1.2.3-alpha", rangeStr = "1.0.0 - 1.2.3-beta", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "0.0.0 - 1.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "0.0.0 - 1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "0.0.0 - 1.2.X", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "0.0.0 - 1.X", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "0.0.0 - 1.0.0", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "0.0.0 - 1.0.0", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "2 - 2.3.4", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "1.3 - 2.3.4", expectedSatisfies = false),
        // Wildcard ranges
        SatisfiesStrArgs(rangeStr = "", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "*", expectedSatisfies = true),
        SatisfiesStrArgs(version = "1.2.3-alpha", rangeStr = "*", expectedSatisfies = false),
        SatisfiesStrArgs(version = "1.2.3-alpha", rangeStr = "1.2.X", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "1.2.X", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.2.*", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.X", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.*", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.3.X", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "1.3.*", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "2.X", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "2.*", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "1.2.+", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.+", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.3.+", expectedSatisfies = false),
        // Tilde ranges
        SatisfiesStrArgs(rangeStr = "~1.2.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "~1.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "~1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "~1.1.3", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "~1.1", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "~2", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "~> 1.2.2 ", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "~> 1.2.4 ", expectedSatisfies = false),
        SatisfiesStrArgs(version = "1.2.3-beta", rangeStr = "~1.2.3-alpha", expectedSatisfies = true),
        // Caret ranges:
        SatisfiesStrArgs(rangeStr = "^1.1.1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "^1.1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "^1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "^0.1.1", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "^0.1", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "^0", expectedSatisfies = false),
        // Comparators:
        SatisfiesStrArgs(rangeStr = "=1.2.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "=1.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "=1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "=1.2.4", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "=1.3", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "=2", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">1.2.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">1.1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">0", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">1.2.3", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">1.2", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">1", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">=1.2.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">=1.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">=1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">=1.2.4", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">=1.3", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">=2", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "<1.2.4", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "<1.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "<2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "<1.2.3", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "<1.2", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "<1", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "<=1.2.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "<=1.2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "<=1", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "<=1.2.2", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "<=1.1", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "<=0", expectedSatisfies = false),
        // AND ranges:
        SatisfiesStrArgs(rangeStr = ">1.0.0 <2.0.0", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">1.0 <2.0", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">0 <2", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = ">1 <3", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "1.2 <1.2.4", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.2 <1.2.3", expectedSatisfies = false),
        // OR ranges:
        SatisfiesStrArgs(rangeStr = "1.2.3 || 1.2.4", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.2.2 || 1.2.4", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = ">1.1 <1.3 || 1.2.4", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.2.4 || >1.1 <1.3", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "1.2.0 - 1.2.2 || 1.2.4 - 2.0.0", expectedSatisfies = false),
        // Ivy ranges:
        SatisfiesStrArgs(rangeStr = "[1.0,2.0]", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "[1.0,2.0[", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "]1.0,2.0]", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "]1.0,2.0[", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "[1.2.4,2.0]", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "]1.2.3,2.0]", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "[1.2.4,)", expectedSatisfies = false),
        SatisfiesStrArgs(rangeStr = "latest", expectedSatisfies = true),
        SatisfiesStrArgs(rangeStr = "latest.integration", expectedSatisfies = true),
    ).forEach { (version, rangeStr, expectedSatisfies) ->
        test(
            "Semver [$version] should return $expectedSatisfies on a satisfy check against string [$rangeStr]"
                .stripDots(),
        ) {
            Semver(version) satisfies rangeStr shouldBe expectedSatisfies
        }
    }

    test("Semver [1.2.3] should check satisfaction on range expression") {
        Semver("1.2.3") satisfies RangesExpression.greaterOrEqual("1.0.0") shouldBe true
    }

    test("Semver [1.2.3] should check satisfaction on ranges list") {
        val rangesList = RangesList(listOf(listOf(Range("1.0.0", Range.RangeOperator.GT))))
        Semver("1.2.3") satisfies rangesList shouldBe true
    }
})
