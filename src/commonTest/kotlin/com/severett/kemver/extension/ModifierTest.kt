package com.severett.kemver.extension

import com.severett.kemver.Semver
import com.severett.kemver.extensions.Modifier.nextMajor
import com.severett.kemver.extensions.Modifier.nextMinor
import com.severett.kemver.extensions.Modifier.nextPatch
import com.severett.kemver.extensions.Modifier.withBuild
import com.severett.kemver.extensions.Modifier.withClearedBuild
import com.severett.kemver.extensions.Modifier.withClearedPreRelease
import com.severett.kemver.extensions.Modifier.withClearedPreReleaseAndBuild
import com.severett.kemver.extensions.Modifier.withIncMajor
import com.severett.kemver.extensions.Modifier.withIncMinor
import com.severett.kemver.extensions.Modifier.withIncPatch
import com.severett.kemver.extensions.Modifier.withPreRelease
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ModifierTest : FunSpec({
    listOf(
        Semver("1.0.0") to Semver(2, 0, 0),
        Semver("1.1.0-1") to Semver(2, 0, 0),
        Semver("1.0.1-1") to Semver(2, 0, 0),
        Semver("1.0.0-1") to Semver(1, 0, 0),
    ).forEach { (semver, expectedSemver) ->
        test("Invoking nextMajor() on Semver [$semver] should result in Semver [$expectedSemver]") {
            semver.nextMajor() shouldBe expectedSemver
        }
    }

    test("Semver should be able to increment major") {
        Semver("1.0.0").withIncMajor(1) shouldBe Semver(2, 0, 0)
    }

    listOf(
        Semver("1.0.0") to Semver(1, 1, 0),
        Semver("1.0.1-1") to Semver(1, 1, 0),
        Semver("1.0.0-1") to Semver(1, 0, 0),
    ).forEach { (semver, expectedSemver) ->
        test("Invoking nextMinor() on Semver [$semver] should result in Semver [$expectedSemver]") {
            semver.nextMinor() shouldBe expectedSemver
        }
    }

    test("Semver should be able to increment minor") {
        Semver("1.0.0").withIncMinor(1) shouldBe Semver(1, 1, 0)
    }

    listOf(
        Semver("1.0.0") to Semver(1, 0, 1),
        Semver("1.0.0-1") to Semver(1, 0, 0),
    ).forEach { (semver, expectedSemver) ->
        test("Invoking nextPatch on Semver [$semver] should result in Semver [$expectedSemver]") {
            semver.nextPatch() shouldBe expectedSemver
        }
    }

    test("Semver should be able to increment patch") {
        Semver("1.0.0").withIncPatch(1) shouldBe Semver(1, 0, 1)
    }

    listOf(
        "withPreRelease() should work with a string" to { semver: Semver -> semver.withPreRelease("alpha.beta") },
        "withPreRelease() should work with a list" to { semver: Semver -> semver.withPreRelease(listOf("alpha", "beta")) },
    ).forEach { (title, callback) ->
        test(title) {
            callback(Semver("1.0.0")) shouldBe Semver(1, 0, 0, preRelease = listOf("alpha", "beta"))
        }
    }

    listOf(
        "withBuild() should work with a string" to { semver: Semver -> semver.withBuild("alpha.beta") },
        "withBuild() should work with a list" to { semver: Semver -> semver.withBuild(listOf("alpha", "beta")) },
    ).forEach { (title, callback) ->
        test(title) {
            callback(Semver("1.0.0")) shouldBe Semver(1, 0, 0, build = listOf("alpha", "beta"))
        }
    }

    test("Semver should be able to clear prerelease") {
        Semver("1.0.0-alpha.beta").withClearedPreRelease() shouldBe Semver(1, 0, 0)
    }

    test("Semver should be able to clear build") {
        Semver("1.0.0+alpha.beta").withClearedBuild() shouldBe Semver(1, 0, 0)
    }

    test("Semver should be able to clear prerelease and build") {
        Semver("1.0.0-alpha.beta+alpha.beta").withClearedPreReleaseAndBuild() shouldBe Semver(1, 0, 0)
    }
})
