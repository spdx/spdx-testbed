# Part 1: Generation of SPDX documents
## Task 1: generationMinimalTest
Generate an SPDX document with the following creation info:
- SPDX version: `SPDX-2.3`
- Data license: `CC0-1.0`
- SPDX identifier: `SPDXRef-DOCUMENT`
- Document name: `Minimal test document`
- SPDX document namespace: `https://some.namespace`
- Creator: arbitrary
- Created: arbitrary

Add one file, described by the document, with the following information:
- File name: `./foo.txt`
- File SPDX identifier: `SPDXRef-somefile`
- File checksum:
  - algorithm: `SHA1`
  - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`

[//]: # (TODO: specify an output path)
Write the file as `xml` to `some destination`. 