package com.severett.kemver

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual

class SemverTest : FunSpec({
    listOf(
        "0.0.4" to Semver(0, 0, 4),
        "1.2.3" to Semver(1, 2, 3),
        "10.20.30" to Semver(10, 20, 30),
        "1.1.2-prerelease+meta" to Semver(1, 1, 2, listOf("prerelease"), listOf("meta")),
        "1.0.0-alpha" to Semver(1, 0, 0, preRelease = listOf("alpha")),
        "1.0.0-alpha-beta" to Semver(1, 0, 0, preRelease = listOf("alpha-beta")),
        "1.0.0-alpha.beta" to Semver(1, 0, 0, preRelease = listOf("alpha", "beta")),
        "1.0.0+alpha" to Semver(1, 0, 0, build = listOf("alpha")),
        "1.0.0+alpha-beta" to Semver(1, 0, 0, build = listOf("alpha-beta")),
        "1.0.0+alpha.beta" to Semver(1, 0, 0, build = listOf("alpha", "beta")),
    ).forEach { (input, expected) ->
        test("Test $input should be equal to $expected") {
            Semver(input) shouldBeEqual expected
        }
    }
})
