package com.severett.kemver.processor

import com.severett.kemver.Range
import com.severett.kemver.processor.RangesUtils.isX
import com.severett.kemver.processor.RangesUtils.parseIntWithXSupport

object HyphenProcessor : Processor {
    private val regex = Regex(
        "^\\s*([v=\\s]*(0|[1-9]\\d*|x|X|\\*|\\+)(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)" +
                "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*))?" +
                "(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?)\\s+-\\s+([v=\\s]*(0|[1-9]\\d*|x|X|\\*|\\+)" +
                "(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)(?:\\.(0|[1-9]\\d*|x|X|\\*|\\+)" +
                "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*))?" +
                "(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)?)?)\\s*$"
    )

    override fun process(range: String): String {
        val matchResult = regex.matchEntire(range) ?: return range

        return "${getRangeFrom(matchResult)} ${getRangeTo(matchResult)}"
    }

    private fun getRangeFrom(matchResult: MatchResult): String {
        val from = matchResult.groupValues[1]
        val fromMajor = parseIntWithXSupport(matchResult.groupValues[2])
        val fromMinor = parseIntWithXSupport(matchResult.groupValues[3])
        val fromPatch = parseIntWithXSupport(matchResult.groupValues[4])

        return when {
            fromMinor.isX() -> "${Range.RangeOperator.GTE.asString}$fromMajor.0.0"
            fromPatch.isX() -> "${Range.RangeOperator.GTE.asString}$fromMajor.$fromMinor.0"
            else -> "${Range.RangeOperator.GTE.asString}$from"
        }
    }

    private fun getRangeTo(matchResult: MatchResult): String {
        val to = matchResult.groupValues[7]
        val toMajor = parseIntWithXSupport(matchResult.groupValues[8])
        val toMinor = parseIntWithXSupport(matchResult.groupValues[9])
        val toPatch = parseIntWithXSupport(matchResult.groupValues[10])

        return when {
            toMinor.isX() -> "${Range.RangeOperator.LT.asString}${toMajor + 1}.0.0"
            toPatch.isX() -> "${Range.RangeOperator.LT.asString}$toMajor.${toMinor + 1}.0"
            else -> "${Range.RangeOperator.LTE.asString}$to"
        }
    }
}
