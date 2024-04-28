package com.severett.kemver.processor

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class HyphenProcessorTest : FunSpec({
    listOf(
        "1.2.3 - 2.3.4" to ">=1.2.3 <=2.3.4",
        "1.2 - 2.3.4" to ">=1.2.0 <=2.3.4",
        "1 - 2.3.4" to ">=1.0.0 <=2.3.4",
        "1.2.3 - 2.3" to ">=1.2.3 <2.4.0",
        "1.2.3 - 2" to ">=1.2.3 <3.0.0",
        "OTHER" to "OTHER",
    ).forEach { (range, expectedStr) ->
        test("Processing hyphen expression [$range] should produce string [$expectedStr]") {
            HyphenProcessor.process(range) shouldBe expectedStr
        }
    }
})
