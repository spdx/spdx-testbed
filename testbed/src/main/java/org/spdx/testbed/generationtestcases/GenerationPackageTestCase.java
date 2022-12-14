// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationtestcases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.ReferenceType;
import org.spdx.library.model.SimpleUriValue;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.Purpose;
import org.spdx.library.model.enumerations.ReferenceCategory;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.testbed.util.testclassification.TestName;

/**
 * Test case covering package properties.
 */
@TestName("generationPackageTest")
public class GenerationPackageTestCase extends GenerationTestCase {

  @Override
  public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
    var document = createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();

    var annotation = new Annotation(modelStore, documentUri,
        modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
        .setAnnotator("Person: Package Annotator")
        .setAnnotationDate("2022-01-01T00:00:00Z")
        .setComment("Package level annotation")
        .setAnnotationType(AnnotationType.OTHER);

    var sha1Checksum = createSha1Checksum(modelStore, documentUri);
    var md5Checksum = document.createChecksum(ChecksumAlgorithm.MD5,
        "624c1abb3664f4b35547e7c73864ad24");

    var gpl20Only = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");

    var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", null,
            List.of(), null, sha1Checksum)
        .build();

    var externalRef = document.createExternalRef(ReferenceCategory.OTHER,
        new ReferenceType(new SimpleUriValue("http://reference.type")),
        "reference/locator", "external reference comment");

    var spdxPackageVerificationCode = document.createPackageVerificationCode(
        "d6a770ba38583ed4bb4525bd96e50461655d2758", List.of("./some.file"));

    var spdxPackage = document.createPackage("SPDXRef-somepackage", "package name", gpl20Only,
            "Copyright 2022 Jane Doe", gpl20Only)
        .addAnnotation(annotation)
        .setVersionInfo("2.2.1")
        .setPackageFileName("./foo.bar")
        .setSupplier("Person: Jane Doe (jane.doe@example.com)")
        .setOriginator("Organization: some organization (contact@example.com)")
        .setDownloadLocation("http://download.com")
        .setFilesAnalyzed(true)
        .setFiles(List.of(file))
        .setPackageVerificationCode(spdxPackageVerificationCode)
        .setChecksums(List.of(sha1Checksum, md5Checksum))
        .setHomepage("http://home.page")
        .setSourceInfo("source information")
        .setLicenseInfosFromFile(List.of(gpl20Only))
        .setLicenseComments("license comment")
        .setSummary("package summary")
        .setDescription("package description")
        .setComment("package comment")
        .setExternalRefs(List.of(externalRef))
        .addAttributionText("package attribution")
        .setPrimaryPurpose(Purpose.LIBRARY)
        .setReleaseDate("2015-01-01T00:00:00Z")
        .setBuiltDate("2014-01-01T00:00:00Z")
        .setValidUntilDate("2022-01-01T00:00:00Z")
        .build();

    document.getDocumentDescribes().add(spdxPackage);

    return document;
  }

  @Override
  public String getName() {
    return "generationPackageTest";
  }
}
