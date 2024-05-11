package com.severett.kemver

/**
 * Thrown when something went wrong while parsing a semver string.
 */
class SemverException(message: String) : IllegalArgumentException(message)
