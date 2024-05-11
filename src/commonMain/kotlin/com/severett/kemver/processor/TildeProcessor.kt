package com.severett.kemver.processor

import com.severett.kemver.Range
import com.severett.kemver.processor.RangesUtils.isX
import com.severett.kemver.processor.RangesUtils.parseIntWithXSupport

/**
 * Processor for translating [tilde ranges](https://github.com/npm/node-semver#tilde-ranges-123-12-1)
 * into a classic range.
 *
 * Translates:
 * * `~1.2.3` to `>=1.2.3 <1.3.0`
 * * `~1.2` to `>=1.2.0 <1.3.0`
 * * `~1` to `>=1.0.0 <2.0.0`
 * * `~0.2.3` to `>=0.2.3 <0.3.0`
 * * `~0.2` to `>=0.2.0 <0.3.0`
 * * `~0` to `>=0.0.0 <1.0.0`
 */
object TildeProcessor : Processor {
    private val regex = Regex(
        "^~>?[v=\\s]*(0|[1-9]\\d*|x|X|\\*|\\+)(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)" +
                "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*))?" +
                "(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?$"
    )

    override fun process(range: String): String {
        val matchResult = regex.matchEntire(range) ?: return range

        val major = parseIntWithXSupport(matchResult.groupValues[1])
        val minor = parseIntWithXSupport(matchResult.groupValues[2])
        val patch = parseIntWithXSupport(matchResult.groupValues[3])
        val preRelease = matchResult.groupValues[4]

        val from: String
        val to: String

        when {
            minor.isX() -> {
                from = "${Range.RangeOperator.GTE.asString}$major.0.0"
                to = "${Range.RangeOperator.LT.asString}${major + 1}.0.0"
            }

            patch.isX() -> {
                from = "${Range.RangeOperator.GTE.asString}$major.$minor.0"
                to = "${Range.RangeOperator.LT.asString}$major.${minor + 1}.0"
            }

            else -> {
                from = if (preRelease.isNotBlank()) {
                    "${Range.RangeOperator.GTE.asString}$major.$minor.$patch-$preRelease"
                } else {
                    "${Range.RangeOperator.GTE.asString}$major.$minor.$patch"
                }
                to = "${Range.RangeOperator.LT.asString}$major.${minor + 1}.0"
            }
        }

        return "$from $to"
    }
}
