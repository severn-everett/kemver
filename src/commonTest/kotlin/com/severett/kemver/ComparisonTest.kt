package com.severett.kemver

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

private val SEMVER_PAIRS =
    listOf(
        Semver("2.0.0") to Semver("1.0.0"),
        Semver("1.1.0") to Semver("1.0.0"),
        Semver("1.0.1") to Semver("1.0.0"),
        Semver("1.0.0") to Semver("1.0.0-alpha"),
        Semver("1.0.0-alpha.1") to Semver("1.0.0-alpha"),
        Semver("1.0.0-beta") to Semver("1.0.0-alpha"),
        Semver("1.0.0-alpha2") to Semver("1.0.0-alpha1"),
        Semver("1.0.0-alpha1a") to Semver("1.0.0-alpha"),
        Semver("1.0.0-alpha1b") to Semver("1.0.0-alpha1a"),
        Semver("1.0.0-20240111143214") to Semver("1.0.0-20240111143213"),
        Semver("1.0.0-a1a1b") to Semver("1.0.0-a1a1a"),
    )

class ComparisonTest : FunSpec({
    test("Should sort versions") {
        val semvers =
            listOf(
                Semver("1.2.3"),
                Semver("1.2.3-rc3"),
                Semver("1.2.3-rc2"),
                Semver("1.2.3-rc1"),
                Semver("1.2.2"),
                Semver("1.2.2-rc2"),
                Semver("1.2.2-rc1"),
                Semver("1.2.0"),
                Semver("1.2.2-beta.1"),
            ).sorted()

        semvers.map(Semver::version) shouldBe
            listOf(
                "1.2.0",
                "1.2.2-beta.1",
                "1.2.2-rc1",
                "1.2.2-rc2",
                "1.2.2",
                "1.2.3-rc1",
                "1.2.3-rc2",
                "1.2.3-rc3",
                "1.2.3",
            )
    }

    SEMVER_PAIRS.forEach { (semver, otherSemver) ->
        test("Semver [$semver] should be greater than Semver [$otherSemver]".stripDots()) {
            (semver isGreaterThan otherSemver).shouldBeTrue()
        }

        test("Semver [$semver] should be greater than or equal to Semver [$otherSemver]".stripDots()) {
            (semver isGreaterThanOrEqualTo otherSemver).shouldBeTrue()
        }

        test("Semver [$semver] should not be lower than or equal to Semver [$otherSemver]".stripDots()) {
            (semver isLowerThanOrEqualTo otherSemver).shouldBeFalse()
        }

        test("Semver [$semver] should not be lower than Semver [$otherSemver]".stripDots()) {
            (semver isLowerThan otherSemver).shouldBeFalse()
        }
    }

    test("Semver [1.0.0-alpha.1] should be greater than string [1.0.0-alpha]".stripDots()) {
        (Semver("1.0.0-alpha.1") isGreaterThan "1.0.0-alpha").shouldBeTrue()
    }

    test("Semver [1.0.0-alpha.1] should be greater than or equal to string [1.0.0-alpha]".stripDots()) {
        (Semver("1.0.0-alpha.1") isGreaterThanOrEqualTo "1.0.0-alpha").shouldBeTrue()
    }

    test("Semver [1.0.0-alpha.1] should not be be lower than or equal to string [1.0.0-alpha]".stripDots()) {
        (Semver("1.0.0-alpha.1") isLowerThanOrEqualTo "1.0.0-alpha").shouldBeFalse()
    }

    test("Semver [1.0.0-alpha.1] should not be lower than string [1.0.0-alpha]".stripDots()) {
        (Semver("1.0.0-alpha.1") isLowerThan "1.0.0-alpha").shouldBeFalse()
    }

    listOf(
        Semver("1.0.0+alpha") to Semver("1.0.0"),
        Semver("1.0.0+alpha") to Semver("1.0.0+beta"),
    ).forEach { (semver, otherSemver) ->
        test("Semver [$semver] should be equivalent to Semver [$otherSemver]".stripDots()) {
            (semver isEquivalentTo otherSemver).shouldBeTrue()
        }
    }

    listOf(
        Semver("1.0.0-alpha") to Semver("1.0.0"),
        Semver("1.0.0-alpha") to Semver("1.0.0-beta"),
    ).forEach { (semver, otherSemver) ->
        test("Semver [$semver] should not be equivalent to Semver [$otherSemver]".stripDots()) {
            (semver isEquivalentTo otherSemver).shouldBeFalse()
        }
    }

    test("Semver [1.0.0+alpha] should be equivalent to string [1.0.0]".stripDots()) {
        (Semver("1.0.0+alpha") isEquivalentTo "1.0.0").shouldBeTrue()
    }

    test("Semver [1.0.0+alpha] should be equal to string [1.0.0+alpha]".stripDots()) {
        (Semver("1.0.0+alpha") isEqualTo "1.0.0+alpha").shouldBeTrue()
    }

    test("Semver [1.0.0+alpha] should not be equal to Semver [1.0.0+beta]".stripDots()) {
        (Semver("1.0.0+alpha") isEqualTo Semver("1.0.0+beta")).shouldBeFalse()
    }
})
