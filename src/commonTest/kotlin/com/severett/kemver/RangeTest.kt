package com.severett.kemver

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangeTest : FunSpec({
    listOf(
        Range(Semver.ZERO, Range.RangeOperator.GTE) to true,
        Range(Semver(major = 0, minor = 0, patch = 1), Range.RangeOperator.GTE) to false,
        Range(Semver.ZERO, Range.RangeOperator.GT) to false,
    ).forEach { (range, expectedResult) ->
        test("satisfiedByAny for Range [$range] should evaluate to $expectedResult".stripDots()) {
            range.isSatisfiedByAny shouldBe expectedResult
        }
    }

    data class EqualsArguments(val rangeVersion: String, val version: String, val expectedResult: Boolean)
    listOf(
        EqualsArguments("1.2.3", "1.2.3", true),
        EqualsArguments("1.2.3", "1.1.3", false),
        EqualsArguments("1.2.3", "0.2.3", false),
        EqualsArguments("1.2.3", "1.2.1", false),
        EqualsArguments("1.2.3-alpha", "1.2.3-alpha", true),
        EqualsArguments("1.2.3-alpha", "1.2.3", false),
        EqualsArguments("1.2.3-alpha", "1.2.3-beta", false),
    ).forEach { (rangeVersion, version, expectedResult) ->
        val range = Range(rangeVersion, Range.RangeOperator.EQ)
        test("isSatisfiedBy() for Range [$range] against Semver [$version] should evaluate to $expectedResult".stripDots()) {
             range isSatisfiedBy version shouldBe expectedResult
        }
    }

    listOf(
        "1.2.3" to false,
        "1.2.4" to false,
        "1.2.2" to true,
    ).forEach { (version, expectedResult) ->
        val range = Range("1.2.3", Range.RangeOperator.LT)
        test("isSatisfiedBy() for Range [$range] against Semver [$version] should evaluate to $expectedResult".stripDots()) {
            range isSatisfiedBy version shouldBe expectedResult
        }
    }

    listOf(
        "1.2.3" to true,
        "1.2.4" to false,
        "1.2.2" to true,
    ).forEach { (version, expectedResult) ->
        val range = Range("1.2.3", Range.RangeOperator.LTE)
        test("isSatisfiedBy() for Range [$range] against Semver [$version] should evaluate to $expectedResult".stripDots()) {
            range isSatisfiedBy version shouldBe expectedResult
        }
    }

    listOf(
        "1.2.3" to false,
        "1.2.4" to true,
        "1.2.2" to false,
    ).forEach { (version, expectedResult) ->
        val range = Range("1.2.3", Range.RangeOperator.GT)
        test("isSatisfiedBy() for Range [$range] against Semver [$version] should evaluate to $expectedResult".stripDots()) {
            range isSatisfiedBy version shouldBe expectedResult
        }
    }

    listOf(
        "1.2.3" to true,
        "1.2.4" to true,
        "1.2.2" to false,
    ).forEach { (version, expectedResult) ->
        val range = Range("1.2.3", Range.RangeOperator.GTE)
        test("isSatisfiedBy() for Range [$range] against Semver [$version] should evaluate to $expectedResult".stripDots()) {
            range isSatisfiedBy version shouldBe expectedResult
        }
    }

    listOf(
        Range.RangeOperator.EQ to "=1.2.3",
        Range.RangeOperator.LT to "<1.2.3",
        Range.RangeOperator.LTE to "<=1.2.3",
        Range.RangeOperator.GT to ">1.2.3",
        Range.RangeOperator.GTE to ">=1.2.3",
    ).forEach { (rangeOperator, expectedResult) ->
        test("toString() should properly display RangeOperator [$rangeOperator]".stripDots()) {
            val range = Range("1.2.3", rangeOperator)
            range.toString() shouldBe expectedResult
        }
    }
})
