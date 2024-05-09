package com.severett.kemver.processor

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TildeProcessorTest : FunSpec({
    listOf(
        "~1.2.3" to ">=1.2.3 <1.3.0",
        "~1.2" to ">=1.2.0 <1.3.0",
        "~1" to ">=1.0.0 <2.0.0",
        "~1.2.3-alpha" to ">=1.2.3-alpha <1.3.0",
        "OTHER" to "OTHER",
    ).forEach { (range, expectedStr) ->
        test("Processing tilde expression [$range] should produce string [$expectedStr]".stripDots()) {
            TildeProcessor.process(range) shouldBe expectedStr
        }
    }
})
