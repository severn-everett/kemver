package com.severett.kemver

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangesListTest : FunSpec({
    listOf(
        listOf(listOf(Range("0.0.0", Range.RangeOperator.GTE))) to true,
        listOf(listOf(Range("0.0.0", Range.RangeOperator.GT))) to false,
    ).forEach { (rangeLists, expectedResult) ->
        test("isSatisfiedByAny for RangesList that contains $rangeLists should evaluate to $expectedResult".stripDots()) {
            RangesList(rangeLists).isSatisfiedByAny shouldBe expectedResult
        }
    }

    listOf(
        "1.2.3" to true,
        "0.9.9" to false,
        "3.0.0-beta" to true,
        "3.0.1-alpha" to false,
    ).forEach { (version, expectedResult) ->
        val rangesList = RangesList(
            listOf(
                listOf(
                    Range("1.0.0", Range.RangeOperator.GTE),
                    Range("2.0.0", Range.RangeOperator.LTE),
                ),
                listOf(Range("3.0.0-alpha", Range.RangeOperator.GTE)),
            ),
        )
        test("isSatisfiedBy on Semver[$version] for RangesList[$rangesList] should evaluate to $expectedResult".stripDots()) {
            rangesList isSatisfiedBy version shouldBe expectedResult
        }
    }

    listOf(
        Pair(
            first = listOf(
                listOf(Range("2.6.8", Range.RangeOperator.LTE)),
                listOf(
                    Range("3.0.0", Range.RangeOperator.GTE),
                    Range("3.0.1", Range.RangeOperator.LTE)
                ),
            ),
            second = "<=2.6.8 or (>=3.0.0 and <=3.0.1)",
        ),
        Pair(
            first = listOf(
                listOf(
                    Range("3.0.0", Range.RangeOperator.GTE),
                    Range("3.0.1", Range.RangeOperator.LTE)
                ),
            ),
            second = ">=3.0.0 and <=3.0.1",
        ),
    ).forEach { (rangeLists, expectedStr) ->
        test("RangesList that contains $rangeLists should produce a string of \"$expectedStr\"".stripDots()) {
            RangesList(rangeLists).toString() shouldBe expectedStr
        }
    }
})
