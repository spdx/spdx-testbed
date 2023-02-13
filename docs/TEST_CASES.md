# Test categories

The following test categories are currently available:

* [generation](#generation): Covers all test cases concerned with the generation of valid SPDX
  documents.

## <a id="generation"></a> Part 1: Generation of SPDX documents (category: `generation`)

### Minimal information

The following tasks will contain some repeating information that is necessary overhead for a valid
SPDX document. These cases are marked with the keyphrase "minimal information".

A document with minimal information contains the following:

- SPDX version: `SPDX-2.3`
- Data license: `CC0-1.0`
- SPDX identifier: `SPDXRef-DOCUMENT`
- Document name: `document name`
- SPDX document namespace: `https://some.namespace`
- Creator: `Tool: test-tool`
- Created: `2022-01-01T00:00:00Z`

An SPDX document always requires at least one SPDX element to describe. If this is not the focus of
the test case, it refers to using files with "minimal information".
A file with minimal information contains the following:

- File name: `./foo.txt`
- File SPDX identifier: `SPDXRef-somefile` (except for where noted differently)
- File checksum:
    - algorithm: `SHA1`
    - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`

### Task 1: `generationMinimalTest`

Generate an SPDX document with the minimum requirements for a valid document. That is, it should
contain a document with minimal information describing a file with minimal information.

### Task 2: `generationBaselineSbomTest`

Generate an SPDX document containing the baseline requirements for an SBOM as specified by NTIA.
That is, it should contain a document with minimal information,
describing one package with the following information:

- Package SPDX identifier: `SPDXRef-somepackage`
- Package version: `2.2.1`
- Package name: `package name`
- Package supplier: `Person: Jane Doe (jane.doe@example.com)`
- Files Analyzed: `false`
- Package checksum:
    - algorithm: `SHA1`
    - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`

### Task 3: `generationDocumentTest`

Generate an SPDX document with all the specification fields. That is, the document should contain
the following information:

- SPDX version: `SPDX-2.3`
- Data license: `CC0-1.0`
- SPDX identifier: `SPDXRef-DOCUMENT`
- Document name: `document name`
- SPDX document namespace: `https://some.namespace`
- External document references:
    - ID: `DocumentRef-externaldocumentid`
    - URI: `http://external.uri`
    - checksum:
        - algorithm: `SHA1`
        - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
- License list version: `3.7`
- Creator: `Tool: test-tool`
- Creator: `Person: Jane Doe (jane.doe@example.com)`
- Created: `2022-01-01T00:00:00Z`
- Creator comment: `creation comment`
- Document comment: `document comment`
- Annotation:
    - Annotator: `Person: Document Reviewer (mail@mail.com)`
    - Annotation date: `2022-01-01T00:00:00Z`
    - Annotation type: `REVIEW`
    - Annotation comment: `Document level annotation`

Add one file with minimal information, described by the document.

### Task 4: `generationFileTest`

Generate an SPDX document with minimal information, describing a file with all the fields from the
specification. That is, the file should contain the following information:

- File name: `./package/foo.c`
- File SPDX identifier: `SPDXRef-somefile`
- File type: `SOURCE`
- File checksum:
    - algorithm: `SHA1`
        - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
    - algorithm: `MD5`
        - value: `624c1abb3664f4b35547e7c73864ad24`
- Concluded license: `GPL-2.0-only`
- License information in file: `GPL-2.0-only`
- Comments on license: `license comment in file`
- Copyright text: `Copyright 2022 Jane Doe`
- File comment: `file comment`
- File notice: `notice text`
- File contributor: `file contributor`
- File attribution: `file attribution`
- Annotation:
    - Annotator: `Person: File Annotator`
    - Annotation date: `2022-01-01T00:00:00Z`
    - Annotation type: `OTHER`
    - Annotation comment: `File level annotation`

### Task 5: `generationPackageTest`

Generate an SPDX document with minimal information, describing a package with all the fields from
the specification. That is, the package should contain the following information:

- Package name: `package name`
- Package SPDX identifier: `SPDXRef-somepackage`
- Package version: `2.2.1`
- Package file name: `./foo.bar`
- Package supplier: `Person: Jane Doe (jane.doe@example.com)`
- Package originator: `Organization: some organization (contact@example.com)`
- Package download location: `http://download.com`
- Files analyzed: `true`
- Package verification code:
    - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
    - excluded files: `./some.file`
- Package checksum:
    - algorithm: `SHA1`
        - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
    - algorithm: `MD5`
        - value: `624c1abb3664f4b35547e7c73864ad24`
- Package home page: `http://home.page`
- Source information: `source information`
- Concluded license: `GPL-2.0-only`
- All licenses information from files: `GPL-2.0-only`
- Declared license: `GPL-2.0-only`
- Comments on license: `license comment`
- Copyright text: `Copyright 2022 Jane Doe`
- Package summary description: `package summary`
- Package detailed description: `package description`
- Package comment: `package comment`
- External reference:
    - category: `OTHER`
    - type: `http://reference.type`
    - locator: `reference/locator`
    - comment: `external reference comment`
- Package attribution: `package attribution`
- Primary package purpose: `LIBRARY`
- Release date: `2015-01-01T00:00:00Z`
- Built date: `2014-01-01T00:00:00Z`
- Valid until date: `2022-01-01T00:00:00Z`
- Annotation:
    - Annotator: `Person: Package Annotator`
    - Annotation date: `2022-01-01T00:00:00Z`
    - Annotation type: `OTHER`
    - Annotation comment: `Package level annotation`

The package should contain a file `SPDXRef-somefile` with minimal information.

### Task 6: `generationSnippetTest`

Generate an SPDX document with minimal information, describing a snippet with all the fields from
the specification. That is, the snippet should contain the following information:

- Snippet SPDX identifier: `SPDXRef-somesnippet`
- Snippet from file SPDX identifier: `SPDXRef-somefile`
- Snippet byte range: `100:400`
- Snippet line range: `30:40`
- Snippet concluded license: `GPL-2.0-only`
- License information in snippet: `GPL-2.0-only`
- Snippet comments on license: `snippet license comment`
- Snippet copyright text: `Copyright 2022 Jane Doe`
- Snippet comment: `snippet comment`
- Snippet name: `snippet name`
- Snippet attribution text: `snippet attribution`
- Annotation:
    - Annotator: `Person: Snippet Annotator`
    - Annotation date: `2022-01-01T00:00:00Z`
    - Annotation type: `OTHER`
    - Annotation comment: `Snippet level annotation`

Add a file `SPDXRef-somefile` with minimal information.

### Task 7: `generationRelationshipTest`

Generate an SPDX document with minimal information, describing two files `SPDXRef-fileA` (name: `./fileA.c`)
and `SPDXRef-fileB` (name: `./fileB.c`) with minimal information each.  
Create the following relationships:

- `SPDXRef-fileA` DESCRIBED_BY `SPDXRef-Document`, comment: `comment on DESCRIBED_BY`
- `SPDXRef-fileA` DEPENDS_ON `SPDXRef-fileB`
- `SPDXRef-fileB` DESCRIBED_BY `SPDXRef-Document`
- `SPDXRef-fileB` DEPENDENCY_OF `SPDXRef-fileA`, comment: `comment on DEPENDENCY_OF`

### Task 8: `generationLicenseTest`

Generate an SPDX document with minimal information and add two external references:

- License identifier: `LicenseRef-1`
    - extracted text: `extracted license text`
    - license name: `extracted license 1`
    - license cross reference: `http://extracted.license` and `http://see.also`
    - license comment: `extracted license info comment`
- License identifier: `LicenseRef-two`
    - extracted text: `extracted license text`
    - license name: `extracted license 2`
    - license cross reference: `http://another.license`
    - license comment: `extracted license info comment`

Add a package, described by the document, with the following information:

- Package name: `package name`
- Package SPDX identifier: `SPDXRef-somepackage`
- Files analyzed: `true`
- Package verification code:
    - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
- Concluded
  License: `((LicenseRef-1 WITH u-boot-exception-2.0) OR LicenseRef-two) AND (Aladdin WITH Classpath-exception-2.0)`
- All licenses information from files: `LicenseRef-1 OR LicenseRef-two`
  and `Aladdin WITH Classpath-exception-2.0`
- Declared License: `(LicenseRef-1 OR LicenseRef-two) AND (Aladdin WITH Classpath-exception-2.0)`

Let the package contain the following two files:

- File SPDX identifier: `SPDXRef-fileA`
    - File name: `./faa.txt`
    - File checksum:
        - algorithm: `SHA1`
        - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
    - Concluded license: `LicenseRef-1 OR LicenseRef-two`
    - License information in file: `LicenseRef-1` and `LicenseRef-two`
- File SPDX identifier: `SPDXRef-fileB`
    - File name: `./fbb.txt`
    - File checksum:
        - algorithm: `SHA1`
        - value: `d6a770ba38583ed4bb4525bd96e50461655d2758`
    - Concluded license: `Aladdin WITH Classpath-exception-2.0`
    - License information in file: `Aladdin` and `DL-DE-BY-2.0`

Add a snippet with the following information:

- Snippet SPDX identifier: `SPDXRef-somesnippet`
- Snippet from file SPDX identifier: `SPDXRef-fileB`
- Snippet byte range: `100:200`
- Snippet concluded license: `Aladdin`
- License information in snippet: `Aladdin` and `DL-DE-BY-2.0`
