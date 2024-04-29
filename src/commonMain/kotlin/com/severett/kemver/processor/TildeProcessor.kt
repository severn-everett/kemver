package com.severett.kemver.processor

import com.severett.kemver.Range
import com.severett.kemver.processor.RangesUtils.isX
import com.severett.kemver.processor.RangesUtils.parseIntWithXSupport

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
