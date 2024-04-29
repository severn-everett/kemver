package com.severett.kemver.processor

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangeProcessorPipelineTest : FunSpec({
    val aProcessor = Processor { range -> "${range}_A" }
    val bProcessor = Processor { range -> "${range}_B" }

    listOf(
        Pair(
            first = "call chain",
            second = RangeProcessorPipeline.startWith(aProcessor).addProcessor(bProcessor),
        ),
        Pair(
            first = "build lambda",
            second = RangeProcessorPipeline.build {
                addProcessor(aProcessor)
                addProcessor(bProcessor)
            },
        ),
    ).forEach { (type, rangeProcessorPipeline) ->
        test("A RangeProcessorPipeline can be built via a $type") {
            rangeProcessorPipeline.process("RANGE") shouldBe "RANGE_A_B"
        }
    }
})
