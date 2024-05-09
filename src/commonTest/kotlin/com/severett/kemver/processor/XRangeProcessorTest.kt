package com.severett.kemver.processor

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class XRangeProcessorTest : FunSpec({
    listOf(
        ">1.X.X" to ">=2.0.0",
        ">1.2.X" to ">=1.3.0",
        "<=1.X.X" to "<2.0.0",
        "<=1.2.X" to "<1.3.0",
        ">=1.2.X" to ">=1.2.0",
        ">=1.X.X" to ">=1.0.0",
        "1.X" to ">=1.0.0 <2.0.0",
        "1.2.X" to ">=1.2.0 <1.3.0",
        "=1.2.X" to ">=1.2.0 <1.3.0",
        ">=1.2.3 <2.0.0" to ">=1.2.3 <2.0.0",
        "OTHER" to "OTHER",
    ).forEach { (range, expectedStr) ->
        test("Processing x-range expression [$range] should produce string [$expectedStr]".stripDots()) {
            XRangeProcessor.process(range) shouldBe expectedStr
        }
    }
})
