// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsjavasolver.generationtestcases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.RelationshipType;

/**
 * Test case covering several types of relationships.
 */
public class GenerationRelationshipTestCase {

  /**
   * Construct a document solving the test case.
   */
  public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
    var document = GenerationUtil.createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var sha1Checksum = GenerationUtil.createSha1Checksum(modelStore, documentUri);

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

}
