package com.severett.kemver.processor

/**
 * Processor for pipeline translations.
 */
fun interface Processor {
    fun process(range: String): String
}
