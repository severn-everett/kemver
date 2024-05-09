package com.severett.kemver

import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangesListFactoryTest : FunSpec({
    listOf(
        "*" to ">=0.0.0",
        "1.2" to ">=1.2.0 and <1.3.0",
        "=1.2" to ">=1.2.0 and <1.3.0",
        "<=   2.6.8 ||    >= 3.0.0 <= 3.0.1 || >5.0.0" to "<=2.6.8 or (>=3.0.0 and <=3.0.1) or >5.0.0",
        "^14.14.20 || ^16.0.0" to "(>=14.14.20 and <15.0.0) or (>=16.0.0 and <17.0.0)",
    ).forEach { (input, expectedStr) ->
        test("Parsing string [$input] should return RangesList[$expectedStr]".stripDots()) {
            RangesListFactory.create(input).toString() shouldBe expectedStr
        }
    }
})
