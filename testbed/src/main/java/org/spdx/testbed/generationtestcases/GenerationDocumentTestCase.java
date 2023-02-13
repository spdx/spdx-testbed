// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationtestcases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.storage.IModelStore;
import org.spdx.testbed.util.testclassification.TestName;

/**
 * Test case covering document properties.
 */
@TestName("generationDocumentTest")
public class GenerationDocumentTestCase extends GenerationTestCase {

  @Override
  public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
    var document = createSpdxDocumentWithBasicInfo();

    document.getCreationInfo().getCreators().add("Person: Jane Doe (jane.doe@example.com)");

    document.getCreationInfo()
        .setComment("creation comment")
        .setLicenseListVersion("3.7");
    document.setComment("document comment");

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var annotation = new Annotation(modelStore, documentUri,
        modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
        .setAnnotator("Person: Document Reviewer (mail@mail.com)")
        .setAnnotationDate("2022-01-01T00:00:00Z")
        .setComment("Document level annotation")
        .setAnnotationType(AnnotationType.REVIEW);

    document.addAnnotation(annotation);

    var sha1Checksum = createSha1Checksum(modelStore, documentUri);
    var externalDocumentRef = document.createExternalDocumentRef("DocumentRef-externaldocumentid",
        "http://external.uri", sha1Checksum);

    document.setExternalDocumentRefs(List.of(externalDocumentRef));

    var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", null,
            List.of(), null, sha1Checksum)
        .build();

    document.getDocumentDescribes().add(file);

    return document;
  }

  @Override
  public String getName() {
    return "generationDocumentTest";
  }
}
