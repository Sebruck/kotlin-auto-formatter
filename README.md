# Kotlin autoformatter

This is an automated and opinionated tool for reformatting Kotlin source files. Source files are
reformatted so that the code fits within a specified column limit (by default, 100 columns, as
specified by the
[Google Kotlin style guide](https://developer.android.com/kotlin/style-guide#line_wrapping)) and
line breaks occur at reasonable points.

This tool is still work in progress, but can already handle a wide variety of cases.

## Running the tool

The tool can be run as a standalone program, a simple Gradle plugin, or embedded in another program
as a library.

When run on a file, the formatter replaces the existing file content. Use a revision control system
such as Git to ensure that the original is not lost in case of bugs in the tool.

### Standalone

The Gradle target `installDist` installs the formatter under `build/install/kotlin-auto-formatter`.
To run it, use

```shell script
./build/install/kotlin-auto-format/bin/kotlin-auto-format <file to be formatted>
```

### As Gradle plugin

This formatter comes with a Gradle plugin which adds the task `formatKotlin`. This task runs the
formatter on all Kotlin source files.

### As a library

It is possible to invoke the formatter directly from other Java/Kotlin code. See the class
[KotlinFormatter](formatter/src/main/kotlin/org/kotlin/formatter/KotlinFormatter.kt) for
documentation.

## Configuration

Currently, it is only possible to configure the formatter when it is embedded as a library. See
[KotlinFormatter](formatter/src/main/kotlin/org/kotlin/formatter/KotlinFormatter.kt) for details.

## Comparison with other tools

### Kotlin autoformatter vs. [ktlint](https://github.com/pinterest/ktlint)

 * Can autoformat to fit in a (configurable) column limit; ktlint complains about a violated column
   limit but cannot reformat to fit.
 * Aggressively reformats ala Google Java formatter, whereas ktlint reformats only lines which
   violate linting rules.
 * Does not have all of the rules of ktlint, e.g. replacing `${variable}` with `$variable` in a
   string template.
 * Currently does not organize imports, though that feature is planned.

It should be fine to use both ktlint and this formatter in the same project.

### Kotlin autoformatter vs. [ktfmt](https://github.com/facebookincubator/ktfmt)

 * Output of Kotlin autoformatter is compliant with
   [Kotlin coding conventions](https://kotlinlang.org/docs/reference/coding-conventions.html);
   ktfmt output is not.
 * Indentation amount and column limit are configurable in Kotlin autoformatter.
 * Runs on JDK 8+; ktfmt requires JDK 11+.
 * (currently) Can't autoformat KDoc; KDoc is left as is.

### Kotlin autoformatter vs. IntelliJ

 * IntelliJ autoformatter can autoformat individual sections of a file, but doesn't always produce
   desirable output.
 * The Kotlin autoformatter can only format full files (unless used as a library) but produces
   generally better output.

## Upcoming work

 * Ensuring that the output does not conflict with the Kotlin coding conventions or the Google
   Kotlin style guide.
 * Organizing of imports and removal of unused imports.
 * Enforcing more horizontal whitespace rules from the Kotlin coding conventions.
 * Autoformatting KDoc.
 * Creation of an IntelliJ plugin.
 * Allow configuration via command line arguments and Gradle plugin.
 * Configuration of the maximum line length from `.editorconfig`.

## License

[GPL 3.0](LICENSE)
