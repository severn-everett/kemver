package com.severett.kemver.processor

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CaretProcessorTest : FunSpec({
    listOf(
        "^1" to ">=1.0.0 <2.0.0",
        "^1.1" to ">=1.1.0 <2.0.0",
        "^1.1.1" to ">=1.1.1 <2.0.0",
        "^0.1" to ">=0.1.0 <0.2.0",
        "^0.0.1" to ">=0.0.1 <0.0.2",
        "^1.0.0-alpha.1" to ">=1.0.0-alpha.1 <2.0.0",
        "OTHER" to "OTHER",
    ).forEach { (range, expectedStr) ->
        test("Processing caret expression [$range] should produce string [$expectedStr]".stripDots()) {
            CaretProcessor.process(range) shouldBe expectedStr
        }
    }
})
