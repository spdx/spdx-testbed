// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationTestCases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.testbed.util.testClassification.TestName;

@TestName("generationRelationshipTest")
public class GenerationRelationshipTestCase extends GenerationTestCase {

  public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
    var document = createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var sha1Checksum = createSha1Checksum(modelStore, documentUri);

    var fileA = document.createSpdxFile("SPDXRef-fileA", "./fileA.c", null,
            List.of(), null, sha1Checksum)
        .build();

    var fileB = document.createSpdxFile("SPDXRef-fileB", "./fileB.c", null,
            List.of(), null, sha1Checksum)
        .build();

    document.getDocumentDescribes().addAll(List.of(fileA, fileB));

    fileA.addRelationship(
        document.createRelationship(
            document, RelationshipType.DESCRIBED_BY, "comment on DESCRIBED_BY"
        )
    );

    fileB.addRelationship(
        document.createRelationship(
            document, RelationshipType.DESCRIBED_BY, null
        )
    );

    fileA.addRelationship(
        document.createRelationship(
            fileB, RelationshipType.DEPENDS_ON, null
        )
    );

    fileB.addRelationship(
        document.createRelationship(
            fileA, RelationshipType.DEPENDENCY_OF, "comment on DEPENDENCY_OF"
        )
    );

    return document;
  }

  @Override
  public String getName() {
    return "generationRelationshipTest";
  }
}
