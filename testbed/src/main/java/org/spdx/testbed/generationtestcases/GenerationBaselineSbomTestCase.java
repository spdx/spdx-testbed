// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationtestcases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.testbed.util.testclassification.TestName;

/**
 * Test case covering baseline sbom requirements.
 */
@TestName("generationBaselineSbomTest")
public class GenerationBaselineSbomTestCase extends GenerationTestCase {

  @Override
  public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
    var document = createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var sha1Checksum = createSha1Checksum(modelStore, documentUri);

    var spdxPackage = document.createPackage("SPDXRef-somepackage", "package name", null,
            null, null)
        .setFilesAnalyzed(false)
        .setVersionInfo("2.2.1")
        .setSupplier("Person: Jane Doe (jane.doe@example.com)")
        .setChecksums(List.of(sha1Checksum))
        .build();

    document.getDocumentDescribes().add(spdxPackage);

    return document;
  }

  @Override
  public String getName() {
    return "generationBaselineSbomTest";
  }
}
