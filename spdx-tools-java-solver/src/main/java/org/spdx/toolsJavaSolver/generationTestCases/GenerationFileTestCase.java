// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsJavaSolver.generationTestCases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.FileType;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;

public class GenerationFileTestCase {

  public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
    var document = GenerationUtil.createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var annotation = new Annotation(modelStore, documentUri,
        modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
        .setAnnotator("Person: File Annotator")
        .setAnnotationDate("2022-01-01T00:00:00Z")
        .setComment("File level annotation")
        .setAnnotationType(AnnotationType.OTHER);

    var sha1Checksum = GenerationUtil.createSha1Checksum(modelStore, documentUri);
    var md5Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.MD5,
        "624c1abb3664f4b35547e7c73864ad24");

    var license = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");

    var file = document.createSpdxFile("SPDXRef-somefile", "./package/foo.c", license,
            List.of(license), "Copyright 2022 Jane Doe", sha1Checksum)
        .addAnnotation(annotation)
        .addFileType(FileType.SOURCE)
        .addChecksum(md5Checksum)
        .setLicenseComments("license comment in file")
        .setComment("file comment")
        .setNoticeText("notice text")
        .setFileContributors(List.of("file contributor"))
        .addAttributionText("file attribution")
        .build();

    document.getDocumentDescribes().add(file);

    return document;
  }
}
