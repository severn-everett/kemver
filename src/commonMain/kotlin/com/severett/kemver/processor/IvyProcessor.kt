package com.severett.kemver.processor

import com.severett.kemver.Range
import com.severett.kemver.processor.RangesUtils.isX
import com.severett.kemver.processor.RangesUtils.parseIntWithXSupport

/**
 * Processor for translating [Ivy ranges](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html)
 * into a classic range.
 *
 * Translates:
 * * `[1.0,2.0]` to `>=1.0.0 <=2.0.0`
 * * `[1.2,2.0[` to `>=1.0.0 <2.0.0`
 * * `]1.0,2.0]` to `>1.0.0 <=2.0.0`
 * * `]1.0,2.0[` to `>1.0.0 <2.0.0`
 * * `]1.0.1,2.0.1[` to `>1.0.1 <2.0.1`
 * * `[1.0,)` to `>=1.0.0`
 * * `]1.0,)` to `>1.0.0`
 * * `(,2.0]` to `<=2.0.0`
 * * `(,2.0[` to `<2.0.0`
 */
object IvyProcessor : Processor {
    private const val LEFT_BRACKET = "["
    private const val RIGHT_BRACKET = "]"
    private const val LEFT_PARENTHESIS = "("
    private const val RIGHT_PARENTHESIS = ")"

    // Need to escape the final right square bracket due to it being an illegal
    // character in the unicode-enabled Regex object on the JS platform
    @Suppress("RegExpRedundantEscape")
    private val regex =
        Regex("^([\\[\\](])([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?,([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?([\\]\\[)])$")
    private val rangeBrackets = listOf(LEFT_BRACKET, RIGHT_BRACKET)

    override fun process(range: String): String {
        val matchResult = regex.matchEntire(range) ?: return range

        val openSign = matchResult.groupValues[1]

        val fromMajor = parseIntWithXSupport(matchResult.groupValues[2])
        val fromMinor = parseIntWithXSupport(matchResult.groupValues[3]).takeUnless { it.isX() } ?: 0
        val fromPatch = parseIntWithXSupport(matchResult.groupValues[4]).takeUnless { it.isX() } ?: 0

        val toMajor = parseIntWithXSupport(matchResult.groupValues[5])
        val toMinor = parseIntWithXSupport(matchResult.groupValues[6]).takeUnless { it.isX() } ?: 0
        val toPatch = parseIntWithXSupport(matchResult.groupValues[7]).takeUnless { it.isX() } ?: 0

        val closeSign = matchResult.groupValues[8]

        if (openSign.isInclusiveRange()) {
            val fromOperator = if (openSign == LEFT_BRACKET) Range.RangeOperator.GTE else Range.RangeOperator.GT
            if (closeSign.isInclusiveRange()) {
                val toOperator = if (closeSign == RIGHT_BRACKET) Range.RangeOperator.LTE else Range.RangeOperator.LT
                return "${fromOperator.asString}$fromMajor.$fromMinor.$fromPatch " +
                    "${toOperator.asString}$toMajor.$toMinor.$toPatch"
            } else if (closeSign == RIGHT_PARENTHESIS) {
                return "${fromOperator.asString}$fromMajor.$fromMinor.$fromPatch"
            }
        } else if (closeSign.isInclusiveRange() && openSign == LEFT_PARENTHESIS) {
            val toOperator = if (closeSign == RIGHT_BRACKET) Range.RangeOperator.LTE else Range.RangeOperator.LT
            return "${toOperator.asString}$toMajor.$toMinor.$toPatch"
        }

        return range
    }

    private fun String.isInclusiveRange() = this in rangeBrackets
}
