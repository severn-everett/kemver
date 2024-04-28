package com.severett.kemver.processor

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private const val ALL_RANGE_STR = ">=0.0.0"

class GreaterThanOrEqualZeroProcessorTest : FunSpec({
    listOf(
        "" to ALL_RANGE_STR,
        "latest" to ALL_RANGE_STR,
        "latest.integration" to ALL_RANGE_STR,
        "*" to ALL_RANGE_STR,
        "OTHER" to "OTHER"
    ).forEach { (range, expectedStr) ->
        test("Processing expression [$range] should produce string [$expectedStr]") {
            GreaterThanOrEqualZeroProcessor.process(range) shouldBe expectedStr
        }
    }
})
