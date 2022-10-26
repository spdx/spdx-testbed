// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsJavaSolver.generationTestCases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;

public class GenerationSnippetTestCase {

  public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
    var document = GenerationUtil.createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var annotation = new Annotation(modelStore, documentUri,
        modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
        .setAnnotator("Person: Snippet Annotator")
        .setAnnotationDate("2022-01-01T00:00:00Z")
        .setComment("Snippet level annotation")
        .setAnnotationType(AnnotationType.OTHER);

    var sha1Checksum = GenerationUtil.createSha1Checksum(modelStore, documentUri);

    var gpl2_0only = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");

    var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", null,
            List.of(), null, sha1Checksum)
        .build();

    var spdxSnippet = document.createSpdxSnippet("SPDXRef-somesnippet", "snippet name", gpl2_0only,
            List.of(gpl2_0only), "Copyright 2022 Jane Doe", file, 100, 400)
        .addAnnotation(annotation)
        .setLineRange(30, 40)
        .setLicenseComments("snippet license comment")
        .setComment("snippet comment")
        .addAttributionText("snippet attribution")
        .build();

    document.getDocumentDescribes().add(spdxSnippet);

    return document;
  }
}
