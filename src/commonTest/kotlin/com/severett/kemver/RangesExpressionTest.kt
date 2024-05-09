package com.severett.kemver

import com.severett.kemver.RangesExpression.Companion.equal
import com.severett.kemver.RangesExpression.Companion.greater
import com.severett.kemver.RangesExpression.Companion.greaterOrEqual
import com.severett.kemver.RangesExpression.Companion.less
import com.severett.kemver.RangesExpression.Companion.lessOrEqual
import com.severett.kemver.util.stripDots
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RangesExpressionTest : FunSpec({
    data class ExpressionArgs(val type: String, val expression: (String) -> RangesExpression, val expectedStr: String)

    listOf(
        ExpressionArgs("Equal", RangesExpression::equal, "=1.0.0"),
        ExpressionArgs("Greater", RangesExpression::greater, ">1.0.0"),
        ExpressionArgs("Greater Or Equal", RangesExpression::greaterOrEqual, ">=1.0.0"),
        ExpressionArgs("Less", RangesExpression::less, "<1.0.0"),
        ExpressionArgs("Less Or Equal", RangesExpression::lessOrEqual, "<=1.0.0"),
    ).forEach { (type, expression, expectedStr) ->
        val version = "1.0.0"
        test("An expression \"$type\" for version $version should produce RangesExpression [$expectedStr]".stripDots()) {
            val rangesExpression = expression.invoke(version)
            rangesExpression.get().toString() shouldBe expectedStr
        }
    }

    test("A complex range expression should be built".stripDots()) {
        val rangeExpressions = equal("1.0.0")
            .and(
                equal("2.0.0")
                    .and(equal("3.0.0"))
                    .or(greater("4.0.0").and(less("5.0.0")))
                    .or(equal("6.0.0"))
            ).and(greaterOrEqual("7.0.0"))
            .and(lessOrEqual("8.0.0"))
            .or(less("9.0.0"))

        rangeExpressions.get().toString() shouldBe "(=1.0.0 and =2.0.0 and =3.0.0) or (>4.0.0 and <5.0.0) or =6.0.0 or (>=7.0.0 and <=8.0.0) or <9.0.0"
    }

    test("A complex range expression should be built using lambdas".stripDots()) {
        val rangeExpressions = equal("1.0.0") {
            and {
                equal("2.0.0") {
                    and { equal("3.0.0") }
                    or {
                        greater("4.0.0") {
                            and { less("5.0.0") {} }
                        }
                    }
                    or { equal("6.0.0") }
                }
            }
            and { greaterOrEqual("7.0.0") {} }
            and { lessOrEqual("8.0.0") {} }
            or { less("9.0.0") }
        }

        rangeExpressions.get().toString() shouldBe "(=1.0.0 and =2.0.0 and =3.0.0) or (>4.0.0 and <5.0.0) or =6.0.0 or (>=7.0.0 and <=8.0.0) or <9.0.0"
    }
})
