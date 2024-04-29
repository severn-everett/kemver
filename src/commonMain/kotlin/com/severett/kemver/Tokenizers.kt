package com.severett.kemver

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
    private const val MAIN_VERSION = "($NUMERIC_IDENTIFIER)\\.($NUMERIC_IDENTIFIER)\\.($NUMERIC_IDENTIFIER)"

    private const val PRERELEASE_IDENTIFIER = "(?:$NUMERIC_IDENTIFIER|$NON_NUMERIC_IDENTIFIER)"

    /**
     * Prerelease identifier forwarded by hyphen with appended series of dot-separated identifiers.
     *
     * See [semver.org#spec-item-9](https://semver.org/#spec-item-9)
     */
    private const val PRERELEASE = "(?:-($PRERELEASE_IDENTIFIER(?:\\.$PRERELEASE_IDENTIFIER)*))"

    private const val BUILD_IDENTIFIER = "[0-9A-Za-z-]+"

    private const val BUILD = "(?:\\+($BUILD_IDENTIFIER(?:\\.$BUILD_IDENTIFIER)*))"

    private const val STRICT_PLAIN = "v?$MAIN_VERSION$PRERELEASE?$BUILD?"

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
    const val STRICT = "^$STRICT_PLAIN$"

    /**
     * `x`, `X`, and `*` are for X-Ranges.
     *
     * `+` is for Ivy ranges.
     */
    private const val XRANGE_IDENTIFIER = "$NUMERIC_IDENTIFIER|x|X|\\*|\\+"

    private const val XRANGE_PLAIN = "[v=\\s]*($XRANGE_IDENTIFIER)(?:\\.($XRANGE_IDENTIFIER)(?:\\.($XRANGE_IDENTIFIER)(?:$PRERELEASE)?$BUILD?)?)?"

    private const val LONE_CARET = "(?:\\^)"

    const val CARET = "^$LONE_CARET$XRANGE_PLAIN$"

    const val HYPHEN = "^\\s*($XRANGE_PLAIN)\\s+-\\s+($XRANGE_PLAIN)\\s*$"

    const val IVY = "^(\\[|\\]|\\()([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?\\,([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?(\\]|\\[|\\))$"

    const val TILDE = "^(?:~>?)$XRANGE_PLAIN$"

    private const val GLTL = "((?:<|>)?=?)"

    const val XRANGE = "^$GLTL\\s*$XRANGE_PLAIN$"

    const val COMPARATOR = "^$GLTL\\s*($STRICT_PLAIN)$|^$"
}