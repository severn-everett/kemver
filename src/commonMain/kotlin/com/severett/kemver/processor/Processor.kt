package com.severett.kemver.processor

fun interface Processor {
    fun process(range: String): String
}
