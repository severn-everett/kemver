package com.severett.kemver.processor

internal object RangesUtils {
    private const val X_RANGE_MARKER = -1
    private const val X_CHAR = "x"
    private const val PLUS = "+"
    const val ASTERISK = "*"
    const val EMPTY = ""
    const val SPACE = " "

    fun parseIntWithXSupport(id: String): Int {
        return if (id.isBlank() || id.equals(X_CHAR, ignoreCase = true) || id == ASTERISK || id == PLUS) {
            X_RANGE_MARKER
        } else {
            id.toInt()
        }
    }

    fun Int.isX() = this == X_RANGE_MARKER
}
