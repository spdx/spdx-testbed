<!--
SPDX-FileCopyrightText: TNG Technology Consulting GmbH

SPDX-License-Identifier: Apache-2.0
-->

# SPDX testbed

The SPDX testbed provides tasks that are meant to be solved by external SPDX tools/libraries to prove their capability
in handling SPDX documents.

## State

This is currently under development and not yet stable.

## How to build

You can build the tool locally using `./gradlew :testbed:shadowJar` and then find `testbed-*-all.jar`
in `testbed/build/libs/`.

## How to use

The testbed is a CLI application. After building the executable jar (see above), you can check its usage via

```
java -jar testbed-*-all.jar -h
```

The most important parameters are `-t` and `-c` for selecting which test cases to run, and `-f` for specifying the input
files provided by the external SPDX tool. For example,

```
java -jar testbed-*-all.jar -t generationMinimalTest generationDocumentTest -f minimalFile.xml documentFile.xml
```

will execute `generationMinimalTest` with `minimalFile.xml` as input and `generationDocumentTest`
with `documentFile.xml` as input. The input files are expected to be created using your tool.

A list of available test cases can be found [here](docs/TEST_CASES.md).

Currently, the following categories are available:

## How to integrate

In order to submit a tool, a GitHub Actions workflow should be added at `.github/workflows/[name of your tool].yaml`.
This workflow should download and use your tool in order to generate the desired solution, and then check it via the
testbed application.

As an example, have a look at `.github/workflows/tools-java.yaml` that uses the `spdx-tools-java-solver` to generate an
output file `generationMinimalResult.xml` that should solve the test `generationMinimalTest`.

## Contributing

Contributions are very welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for instructions on how to contribute to the
codebase.

# License

This Projet is licensed under [Apache-2.0](LICENSE)
