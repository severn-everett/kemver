package com.severett.kemver

import com.severett.kemver.processor.CaretProcessor
import com.severett.kemver.processor.GreaterThanOrEqualZeroProcessor
import com.severett.kemver.processor.HyphenProcessor
import com.severett.kemver.processor.IvyProcessor
import com.severett.kemver.processor.RangeProcessorPipeline
import com.severett.kemver.processor.TildeProcessor
import com.severett.kemver.processor.XRangeProcessor

object RangesListFactory {
    private val splitterRegex = Regex("(\\s*)([<>]?=?)\\s*")
    private val comparatorRegex = Regex(
        "^([<>]?=?)\\s*(v?(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
                "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][a-zA-Z0-9-]*))*))?" +
                "(?:\\+([0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*))?)$|^$"
    )
    private val sectionsRegex = Regex("\\|\\|")
    private val spacesRegex = Regex("\\s+")
    private val processors = RangeProcessorPipeline.build {
        addProcessor(GreaterThanOrEqualZeroProcessor)
        addProcessor(IvyProcessor)
        addProcessor(HyphenProcessor)
        addProcessor(CaretProcessor)
        addProcessor(TildeProcessor)
        addProcessor(XRangeProcessor)
    }

    fun create(rangesExpression: RangesExpression) = rangesExpression.get()

    fun create(range: String): RangesList {
        val rangesList = range.trim()
            .split(sectionsRegex)
            .map { rangeSection ->
                val trimmedRangeSection = splitterRegex.replace(rangeSection) {
                    "${it.groupValues[1]}${it.groupValues[2]}"
                }.trim()
                val processedRangesSection = processors.process(trimmedRangeSection)
                createRanges(processedRangesSection)
            }
        return RangesList(rangesList)
    }

    private fun createRanges(range: String): List<Range> = range.split(spacesRegex)
        .mapNotNull { parsedRange ->
            comparatorRegex.matchEntire(parsedRange)?.let { matchResult ->
                Range(
                    rangeVersion = matchResult.groupValues[2],
                    rangeOperator = Range.RangeOperator.value(matchResult.groupValues[1])
                )
            }
        }
}
