package com.severett.kemver

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private data class WithCallbackTest(val title: String, val callback: (Semver) -> Semver, val expectedSemver: Semver)

class ModifierTest : FunSpec({
    listOf(
        Semver("1.0.0") to Semver(2, 0, 0),
        Semver("1.1.0-1") to Semver(2, 0, 0),
        Semver("1.0.1-1") to Semver(2, 0, 0),
        Semver("1.0.0-1") to Semver(1, 0, 0),
    ).forEach { (semver, expectedSemver) ->
        test("Invoking nextMajor() on Semver [$semver] should result in Semver [$expectedSemver]".stripDots()) {
            semver.nextMajor() shouldBe expectedSemver
        }
    }

    listOf(
        WithCallbackTest(
            title = "Semver should be able to increment major with a default value",
            callback = Semver::withIncMajor,
            expectedSemver = Semver(2, 0, 0),
        ),
        WithCallbackTest(
            title = "Semver should be able to increment major with a specified value",
            callback = { s -> s.withIncMajor(2) },
            expectedSemver = Semver(3, 0, 0),
        ),
    ).forEach { (title, callback, expectedSemver) ->
        test(title.stripDots()) {
            callback.invoke(Semver("1.0.0")) shouldBe expectedSemver
        }
    }

    listOf(
        Semver("1.0.0") to Semver(1, 1, 0),
        Semver("1.0.1-1") to Semver(1, 1, 0),
        Semver("1.0.0-1") to Semver(1, 0, 0),
    ).forEach { (semver, expectedSemver) ->
        test("Invoking nextMinor() on Semver [$semver] should result in Semver [$expectedSemver]".stripDots()) {
            semver.nextMinor() shouldBe expectedSemver
        }
    }

    listOf(
        WithCallbackTest(
            title = "Semver should be able to increment minor with a default value",
            callback = Semver::withIncMinor,
            expectedSemver = Semver(1, 1, 0),
        ),
        WithCallbackTest(
            title = "Semver should be able to increment minor with a specified value",
            callback = { s -> s.withIncMinor(2) },
            expectedSemver = Semver(1, 2, 0),
        ),
    ).forEach { (title, callback, expectedSemver) ->
        test(title.stripDots()) {
            callback.invoke(Semver("1.0.0")) shouldBe expectedSemver
        }
    }

    listOf(
        Semver("1.0.0") to Semver(1, 0, 1),
        Semver("1.0.0-1") to Semver(1, 0, 0),
    ).forEach { (semver, expectedSemver) ->
        test("Invoking nextPatch on Semver [$semver] should result in Semver [$expectedSemver]".stripDots()) {
            semver.nextPatch() shouldBe expectedSemver
        }
    }

    listOf(
        WithCallbackTest(
            title = "Semver should be able to increment patch with a default value",
            callback = Semver::withIncPatch,
            expectedSemver = Semver(1, 0, 1),
        ),
        WithCallbackTest(
            title = "Semver should be able to increment patch with a specified value",
            callback = { s -> s.withIncPatch(2) },
            expectedSemver = Semver(1, 0, 2),
        ),
    ).forEach { (title, callback, expectedSemver) ->
        test(title.stripDots()) {
            callback.invoke(Semver("1.0.0")) shouldBe expectedSemver
        }
    }

    listOf(
        "withPreRelease() should work with a string" to { semver: Semver -> semver.withPreRelease("alpha.beta") },
        "withPreRelease() should work with a list" to { semver: Semver -> semver.withPreRelease(listOf("alpha", "beta")) },
    ).forEach { (title, callback) ->
        test(title.stripDots()) {
            callback(Semver("1.0.0")) shouldBe Semver(1, 0, 0, preRelease = listOf("alpha", "beta"))
        }
    }

    listOf(
        "withBuild() should work with a string" to { semver: Semver -> semver.withBuild("alpha.beta") },
        "withBuild() should work with a list" to { semver: Semver -> semver.withBuild(listOf("alpha", "beta")) },
    ).forEach { (title, callback) ->
        test(title.stripDots()) {
            callback(Semver("1.0.0")) shouldBe Semver(1, 0, 0, build = listOf("alpha", "beta"))
        }
    }

    test("Semver should be able to clear prerelease".stripDots()) {
        Semver("1.0.0-alpha.beta").withClearedPreRelease() shouldBe Semver(1, 0, 0)
    }

    test("Semver should be able to clear build".stripDots()) {
        Semver("1.0.0+alpha.beta").withClearedBuild() shouldBe Semver(1, 0, 0)
    }

    test("Semver should be able to clear prerelease and build".stripDots()) {
        Semver("1.0.0-alpha.beta+alpha.beta").withClearedPreReleaseAndBuild() shouldBe Semver(1, 0, 0)
    }
})
