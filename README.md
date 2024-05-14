# Kemver

[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/severn-everett/kemver/blob/main/LICENSE)

---

> ### This is a fork of the [semver4j](https://github.com/semver4j/semver4j) library created by [@piotrooo](https://github.com/piotrooo).

---

**Kemver** is a lightweight Kotlin library that handles versioning on multiple platforms.
It follows the rules of the [semantic versioning](http://semver.org) specification.

It also provides several range checking support: [node-semver](https://github.com/npm/node-semver),
[CocoaPods](https://guides.cocoapods.org/using/the-podfile.html)
and [Ivy](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html).

## Table of Contents

<!-- TOC -->

* [Usage](#usage)
    * [What is a version?](#what-is-a-version)
    * [The `Semver` object](#the-semver-object)
        * [Using string-based constructor](#using-string-based-constructor)
        * [Using argument-based constructor](#using-argument-based-constructor)
        * [Using `Semver.parse()` method](#using-semverparse-method)
        * [Using `Semver.coerce()` method](#using-semvercoerce-method)
    * [Is the version stable?](#is-the-version-stable)
    * [Comparing the versions](#comparing-the-versions)
    * [Versions diffs](#versions-diffs)
    * [Ranges](#ranges)
        * [External](#external)
        * [Internal](#internal)
    * [Modifying the version](#modifying-the-version)
    * [Formatting](#formatting)
* [Contributing](#contributing)

## Usage

### What is a version?

In **Kemver**, a version looks like: `1.2.3-beta.4+sha899d8g79f87`.

- `1` is the major part (required)
- `2` is the minor part (required)
- `3` is the patch part (required)
- `beta` and `4` are the pre-release version (optional)
- `sha899d8g79f87` is the build metadata (optional)

### The `Semver` object

You can create `Semver` object in number of ways.

#### Using string-based constructor

```kotlin
val version = Semver("1.2.3-beta.4+sha899d8g79f87")
```

#### Using argument-based constructor

```kotlin
val version = Semver(
    major = 1,
    minor = 2,
    patch = 3,
    preRelease = listOf("beta", "4"),
    build = listOf("sha899d8g79f87"),
)
```

#### Using `Semver.parse()` method

```kotlin
val version = Semver.parse("1.2.3-beta.4+sha899d8g79f87") // returns correct Semver object
val version = Semver.parse("invalid") // returns null, cannot parse this version
```

#### Using `Semver.coerce()` method

The library can help you create valid `Semver` instances when the version is not valid. This aims to provide a forgiving
translation from "not-semver" strings into a valid semver instance.

```kotlin
val version = Semver.coerce("..1") // produces the same result as new Semver("1.0.0")
val version = Semver.coerce("invalid") // returns null, cannot coerce this version
```

### Is the version stable?

You can check if you're working with a stable version by using `isStable()`.

A version is stable if its major number is _strictly_ positive, and it has no pre-release version.

Examples:

```kotlin
// true
Semver("1.2.3").isStable() // major is > 0 and has no pre-release version
Semver("1.2.3+sHa.0nSFGKjkjsdf").isStable() // major is > 0 and has only build metadata without pre-release version

// false
Semver("0.1.2").isStable() // major is < 1
Semver("0.1.2+sHa.0nSFGKjkjsdf").isStable() // major is < 1
Semver("1.2.3-BETA.11+sHa.0nSFGKjkjsdf").isStable() // major is > 0 but has pre-release version BETA.11
```

### Comparing the versions

- `isGreaterThan()` returns true if the version is strictly greater than the other one.

```kotlin
val version = Semver("1.2.3")
version.isGreaterThan("1.2.2") // true
version.isGreaterThan("1.2.4") // false
version.isGreaterThan("1.2.3") // false
```

- `isLowerThan()` returns true if the version is strictly lower than the other one.

```kotlin
val version = Semver("1.2.3")
version.isLowerThan("1.2.2") // false
version.isLowerThan("1.2.4") // true
version.isLowerThan("1.2.3") // false
```

- `isEqualTo()` returns true if the versions are exactly the same.

```kotlin
val version = Semver("1.2.3+sha123456789")
version.isEqualTo("1.2.3+sha123456789") // true
version.isEqualTo("1.2.3+shaABCDEFGHI") // false
```

- `isEquivalentTo()` returns true if the versions are the same (does not take the build metadata into account).

```kotlin
val version = Semver("1.2.3+sha123456789")
version.isEquivalentTo("1.2.3+sha123456789") // true
version.isEquivalentTo("1.2.3+shaABCDEFGHI") // true
```

### Versions diffs

If you want to know what is the main difference between 2 versions, use the `diff()` method.
It will return a `VersionDiff` enum value among: `NONE`, `MAJOR`, `MINOR`, `PATCH`, `PRE_RELEASE`, `BUILD`.

_It will always return the biggest difference._

```kotlin
val version = Semver("1.2.3-beta.4+sha899d8g79f87")
version.diff("1.2.3-beta.4+sha899d8g79f87") // NONE
version.diff("2.3.4-alpha.5+sha32iddfu987") // MAJOR
version.diff("1.3.4-alpha.5+sha32iddfu987") // MINOR
version.diff("1.2.4-alpha.5+sha32iddfu987") // PATCH
version.diff("1.2.3-alpha.5+sha32iddfu987") // PRE_RELEASE
version.diff("1.2.3-beta.4+sha32iddfu987")  // BUILD
```

### Ranges

#### External

If you want to check if a version satisfies a range, use the `satisfies()` method.

`Kemver` can interpret following range implementations:

- [NPM](https://github.com/npm/node-semver)
    - [Primitive ranges](https://github.com/npm/node-semver#ranges) `<`, `<=`, `>`, `>=` and `=`
    - [Hyphen ranges](https://github.com/npm/node-semver#hyphen-ranges-xyz---abc) `X.Y.Z - A.B.C`
    - [X-Ranges](https://github.com/npm/node-semver#x-ranges-12x-1x-12-) `1.2.x`, `1.X`, `1.2.*` and `*`
    - [Tilde ranges](https://github.com/npm/node-semver#tilde-ranges-123-12-1) `~1.2.3`, `~1.2` and `~1`
    - [Caret ranges](https://github.com/npm/node-semver#caret-ranges-123-025-004) `^1.2.3`, `^0.2.5` and `^0.0.4`
- [CocaPods](https://guides.cocoapods.org/using/the-podfile.html)
    - [Optimistic operator](https://guides.cocoapods.org/using/the-podfile.html#specifying-pod-versions) `~> 1.0`
- [Ivy](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html)
    - Version Range Matcher `[1.0,2.0]`, `[1.0,2.0[`, `]1.0,2.0]`, `]1.0,2.0[`, `[1.0,)`, `]1.0,)`, `(,2.0]`
      and `(,2.0[`

#### Internal

The internal ranges builds ranges using fluent interface.

```kotlin
val rangesExpression: RangesExpression = equal("1.0.0")
        .and(less("2.0.0"))
        .or(greaterOrEqual("3.0.0")) // (=1.0.0 and <2.0.0) or >=3.0.0
```

This can also be instantiated using builder lambdas.

```kotlin
val rangesExpression: RangesExpression = equal("1.0.0") {
    and { less("2.0.0") }
    or { greaterThanOrEqual("3.0.0") }
}
```

### Modifying the version

The `Semver` object is immutable. However, it provides a set of methods that will help you create new versions:

- `withIncMajor(increment: Int = 1)` returns a `Semver` object with the major part incremented
- `withIncMinor(increment: Int = 1)` returns a `Semver` object with the minor part incremented
- `withIncPatch(increment: Int = 1)` returns a `Semver` object with the patch part incremented
- `withClearedPreRelease()` returns a `Semver` object with no pre-release version
- `withClearedBuild()` returns a `Semver` object with no build metadata

You can also use built-in versioning methods such as:

- `nextMajor()`: `1.2.3-beta.4+sha32iddfu987 => 2.0.0`
- `nextMinor()`: `1.2.3-beta.4+sha32iddfu987 => 1.3.0`
- `nextPatch()`: `1.2.3-beta.4+sha32iddfu987 => 1.2.4`

### Formatting

Sometimes you want to format `Semver` using custom formatters. You can do this using
a `format(formatter: (Semver) -> String)` method from the `Semver` class:

```kotlin
val semver = Semver("1.2.3-alpha.1+sha.1234")
val customVersion: String = semver.format { "${it.major}:${it.minor}:${it.patch}" } // 1:2:2
```

## Contributing

Any pull request or bug report are welcome!
If you have any suggestion about new features, you can **open an issue**.
