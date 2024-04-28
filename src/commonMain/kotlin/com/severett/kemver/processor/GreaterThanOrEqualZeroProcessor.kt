package com.severett.kemver.processor

import com.severett.kemver.Range
import com.severett.kemver.Semver
import com.severett.kemver.processor.RangesUtils.ASTERISK

object GreaterThanOrEqualZeroProcessor : Processor {
    private const val LATEST = "latest"
    private const val LATEST_INTEGRATION = "$LATEST.integration"
    private val ALL_RANGE = "${Range.RangeOperator.GTE.asString}${Semver.ZERO}"

    override fun process(range: String): String {
        return if (range.isEmpty() || range == LATEST || range == LATEST_INTEGRATION || range == ASTERISK) {
            ALL_RANGE
        } else {
            range
        }
    }
}