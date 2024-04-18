package com.severett.kemver

import java.util.*

internal object Tokenizers {
    /**
     * Not negative integers without leading zeroes.
     *
     * See [semver.org#spec-item-2](https://semver.org/#spec-item-2)
     */
    private const val NUMERIC_IDENTIFIER = "0|[1-9]\\d*"

    private const val NON_NUMERIC_IDENTIFIER = "\\d*[a-zA-Z-][a-zA-Z0-9-]*"

    /**
     * Dot separated numeric version identifiers (**MAJOR**.**MINOR**.**PATCH**).
     *
     * See [semver.org#summary](https://semver.org/#summary)
     */
    private val MAIN_VERSION = String.format(
        Locale.ROOT,
        "(%s)\\.(%s)\\.(%s)",
        NUMERIC_IDENTIFIER,
        NUMERIC_IDENTIFIER,
        NUMERIC_IDENTIFIER,
    )

    private val PRERELEASE_IDENTIFIER = String.format(
        Locale.ROOT,
        "(?:%s|%s)",
        NUMERIC_IDENTIFIER,
        NON_NUMERIC_IDENTIFIER,
    )

    /**
     * Prerelease identifier forwarded by hyphen with appended series of dot-separated identifiers.
     *
     * See [semver.org#spec-item-9](https://semver.org/#spec-item-9)
     */
    private val PRERELEASE = String.format(
        Locale.ROOT,
        "(?:-(%s(?:\\.%s)*))",
        PRERELEASE_IDENTIFIER,
        PRERELEASE_IDENTIFIER,
    )

    private const val BUILD_IDENTIFIER = "[0-9A-Za-z-]+"

    private val BUILD = String.format(Locale.ROOT, "(?:\\+(%s(?:\\.%s)*))", BUILD_IDENTIFIER, BUILD_IDENTIFIER)

    private val STRICT_PLAIN = String.format(Locale.ROOT, "v?%s%s?%s?", MAIN_VERSION, PRERELEASE, BUILD)

    /**
     * Fully, completely valid semver value.
     *
     * Required section is:
     *
     * ```
     * X.Y.Z
     * ```
     *
     * Example with not required sections:
     *
     * ```
     * X.Y.X-beta.1+sha1234
     * ```
     */
    val STRICT = String.format(Locale.ROOT, "^%s$", STRICT_PLAIN)

    /**
     * `x`, `X`, and `*` are for X-Ranges.
     *
     * `+` is for Ivy ranges.
     */
    private val XRANGE_IDENTIFIER = String.format(Locale.ROOT, "%s|x|X|\\*|\\+", NUMERIC_IDENTIFIER)

    private val XRANGE_PLAIN = String.format(
        Locale.ROOT,
        "[v=\\s]*(%s)(?:\\.(%s)(?:\\.(%s)(?:%s)?%s?)?)?",
        XRANGE_IDENTIFIER,
        XRANGE_IDENTIFIER,
        XRANGE_IDENTIFIER,
        PRERELEASE,
        BUILD,
    )

    private const val LONE_CARET = "(?:\\^)"

    val CARET = String.format(Locale.ROOT, "^%s%s$", LONE_CARET, XRANGE_PLAIN)

    val HYPHEN = String.format(Locale.ROOT, "^\\s*(%s)\\s+-\\s+(%s)\\s*$", XRANGE_PLAIN, XRANGE_PLAIN)

    const val IVY = "^(\\[|\\]|\\()([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?\\,([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?(\\]|\\[|\\))$"

    val TILDE = String.format(Locale.ROOT, "^(?:~>?)%s$", XRANGE_PLAIN)

    private const val GLTL = "((?:<|>)?=?)"

    val XRANGE = String.format(Locale.ROOT, "^%s\\s*%s$", GLTL, XRANGE_PLAIN)

    val COMPARATOR = String.format(Locale.ROOT, "^%s\\s*(%s)$|^$", GLTL, STRICT_PLAIN)
}