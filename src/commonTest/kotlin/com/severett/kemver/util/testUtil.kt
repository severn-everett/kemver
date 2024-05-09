package com.severett.kemver.util

// TODO: Remove when KT-68073 is resolved.
fun String.stripDots() = replace(Regex("\\."), "_")
