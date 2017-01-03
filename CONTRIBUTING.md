# Contributing


This repository is far from underwhelmed by requests so feel free to file an issue
for any of the cases mentioned below.

**Want to help?** Issues marked as [open](https://github.com/hmil/RosHTTP/issues?q=is%3Aissue+is%3Aopen+label%3A%22status%3A+open%22) are open to contributions. 
Post a comment stating that you would like to work on the issue and do feel free
to ask for more details and discuss possible implementations at any time.

## Branches

**Always base your pull requests on the latest release branch (ie. v2.x.x)**. 
master always reflects the latest published version of the library and therefore is
not a suitable target for pull requests.

## Reporting bugs

If you think you found a bug, please file an issue and try to provide a piece of
code that triggers the bug. Indicate in which environment this bug happens and give
some details on each affected environments (java version, jdk used, node version,
browser + version).

For bug fixes, file an issue as instructed above and create a pull request referencing
the issue. Always try to provide a test case with your bug fix. If for some reason you
can really not test your bug, let me know in your pull request comment.

## Feature requests

Feel free to file an issue to request a feature, or even just to start a conversation
related to the project. It is advised that you discuss any feature you would like
to implement before starting working on it.

New features **must be tested**.

## Development

This project is built with sbt. While any IDE with decent sbt support would work,
I recommend using _idea_ with the scala plugin.

The sbt project contains two subprojects: `scalaHttpJS` and `scalaHttpJVM`.
Run one of `sbt scalaHttpJS/console` or `sbt scalaHttpJVM/console` to start an interactive
console with library code in the classpath.

You can run tests in 4 different environment using the following commands:
```
sbt scalaHttpJVM/test # Runs on your current JVM (default environment for Scala)
sbt scalaHttpJS/test  # Runs in Node.js
sbt chrome:test # Run in chrome*
sbt firefox:test # Runs in firefox*
```
*Browser testing may require additional software on your computer. See
[https://github.com/scala-js/scala-js-env-selenium#scalajs-env-selenium] for more details.

The testing server needs to run on your machine while running the test commands.

## Testing

All features are tested in [HttpRequestSpec](https://github.com/hmil/RosHTTP/blob/master/shared/src/test/scala/fr/hmil/roshttp/client/HttpRequestSpec.scala).
In order to test this library, the testing server needs to run on the testing machine.
This server enables us to run tests in a reproducible environment and to test edge cases.

To start the testing server, go to `test/server` and install the dependencies with
`npm install`. This command must be run when there has been some updates in the
test server dependencies.  
Run the test server with `node index.js`.

If Node.js is not installed on your machine, do the following:
- For Windows and Mac, download the latest LTS release from the [official website](https://nodejs.org).
- For Linux users, I advise downloading Node.js through [NVM](https://github.com/creationix/nvm)

## Code style

The code in this repository should follow Scala.js
[coding style guide](https://github.com/scala-js/scala-js/blob/master/CODINGSTYLE.md)
as much as possible.

## Other things you need to know

Environment-specific code lives in jvm/ and js/. The `DriverTrait` is the
low-level interface between shared and specific code. Shared code is linked to the
correct `HttpDriver` class by the Scala.js compiler.

All code that gets merged in this code base falls under the terms of the MIT license.
By submitting a pull request to this repository, you consent that your code may be
used, copied, and distributed by anyone as per the terms of the MIT license and
that you are not responsible for the usage other parties may make of your code.
