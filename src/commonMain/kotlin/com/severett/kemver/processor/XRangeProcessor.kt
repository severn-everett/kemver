package com.severett.kemver.processor

import com.severett.kemver.Range
import com.severett.kemver.processor.RangesUtils.isX
import com.severett.kemver.processor.RangesUtils.parseIntWithXSupport

/**
 * Processor for translating [X-Ranges](https://github.com/npm/node-semver#x-ranges-12x-1x-12-) into classic ranges.
 *
 * Translates:
 * * `>1.X.X` to `>=2.0.0`
 * * `>1.2.X` to `>=1.3.0`
 * * `<=1.X.X` to `<2.0.0`
 * * `<=1.2.X` to `<1.3.0`
 * * `>=1.X.X` to `>=1.0.0`
 * * `>=1.2.X` to `>=1.2.0`
 * * `1.X` to `>=1.0.0 <2.0.0`
 * * `1.2.X` to `>=1.2.0 <1.3.0`
 * * `=1.2.X` to `>=1.2.0 <1.3.0`
 */
object XRangeProcessor : Processor {
    private val spaceRegex = Regex("\\s+")
    private val xRangeRegex = Regex(
        "^([<>]?=?)\\s*[v=\\s]*(0|[1-9]\\d*|x|X|\\*|\\+)(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)" +
                "(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)" +
                "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*))?(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?$"
    )

    override fun process(range: String): String {
        val ranges = range.split(spaceRegex).mapNotNull { rangeVersion ->
            xRangeRegex.matchEntire(rangeVersion)?.let { matchResult ->
                val major = parseIntWithXSupport(matchResult.groupValues[2])
                val minor = parseIntWithXSupport(matchResult.groupValues[3])
                val patch = parseIntWithXSupport(matchResult.groupValues[4])
                val compareSign = matchResult.groupValues[1].takeUnless {
                    it == Range.RangeOperator.EQ.asString && patch.isX()
                } ?: ""

                when {
                    compareSign.isNotBlank() && patch.isX() -> {
                        when (compareSign) {
                            Range.RangeOperator.GT.asString -> {
                                if (minor.isX()) {
                                    "${Range.RangeOperator.GTE.asString}${major + 1}.0.0"
                                } else {
                                    "${Range.RangeOperator.GTE.asString}$major.${minor + 1}.0"
                                }
                            }

                            Range.RangeOperator.LTE.asString -> {
                                if (minor.isX()) {
                                    "${Range.RangeOperator.LT.asString}${major + 1}.0.0"
                                } else {
                                    "${Range.RangeOperator.LT.asString}$major.${minor + 1}.0"
                                }
                            }

                            else -> "$compareSign$major.${if (minor.isX()) 0 else minor}.0"
                        }
                    }

                    minor.isX() -> {
                        val from = "${Range.RangeOperator.GTE.asString}$major.0.0"
                        val to = "${Range.RangeOperator.LT.asString}${major + 1}.0.0"
                        "$from $to"
                    }

                    patch.isX() -> {
                        val from = "${Range.RangeOperator.GTE.asString}$major.$minor.0"
                        val to = "${Range.RangeOperator.LT.asString}$major.${minor + 1}.0"
                        "$from $to"
                    }

                    else -> matchResult.groupValues[0]
                }
            }
        }

        return if (ranges.isEmpty()) {
            range
        } else {
            ranges.joinToString(" ")
        }
    }
}
