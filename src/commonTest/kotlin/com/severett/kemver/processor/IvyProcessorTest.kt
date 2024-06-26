package com.severett.kemver.processor

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IvyProcessorTest : FunSpec({
    listOf(
        "[1.2.3,2.3.4]" to ">=1.2.3 <=2.3.4",
        "[1.2,2.3.4]" to ">=1.2.0 <=2.3.4",
        "[1,2.3.4]" to ">=1.0.0 <=2.3.4",
        "[1.2.3,2.3]" to ">=1.2.3 <=2.3.0",
        "[1.2.3,2]" to ">=1.2.3 <=2.0.0",
        "]1.2.3,2.3.4]" to ">1.2.3 <=2.3.4",
        "[1.2.3,2.3.4[" to ">=1.2.3 <2.3.4",
        "[1.2.3,)" to ">=1.2.3",
        "]1.2.3,)" to ">1.2.3",
        "(,2.3.4]" to "<=2.3.4",
        "(,2.3.4[" to "<2.3.4",
        "OTHER" to "OTHER",
    ).forEach { (range, expectedStr) ->
        test("Processing Ivy expression [$range] should produce string [$expectedStr]".stripDots()) {
            IvyProcessor.process(range) shouldBe expectedStr
        }
    }
})
