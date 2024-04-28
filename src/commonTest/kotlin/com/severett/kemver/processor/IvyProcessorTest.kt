package com.severett.kemver.processor

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object IvyProcessorTest : FunSpec({
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
        test("Processing Ivy expression [$range] should produce string [$expectedStr]") {
            IvyProcessor.process(range) shouldBe expectedStr
        }
    }
})
