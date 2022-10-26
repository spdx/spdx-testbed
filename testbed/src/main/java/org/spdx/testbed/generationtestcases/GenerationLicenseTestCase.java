// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationtestcases;

import java.util.List;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.testbed.util.testclassification.TestName;

/**
 * Test case covering license properties.
 */
@TestName("generationLicenseTest")
public class GenerationLicenseTestCase extends GenerationTestCase {

  @Override
  public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
    var document = createSpdxDocumentWithBasicInfo();

    var modelStore = document.getModelStore();
    var documentUri = document.getDocumentUri();
    var copyManager = document.getCopyManager();

    var extractedLicenseInfo1 = new ExtractedLicenseInfo(modelStore, documentUri, "LicenseRef-1",
        copyManager, true);
    extractedLicenseInfo1.setExtractedText("extracted text");
    extractedLicenseInfo1.setName("extracted license info name");
    extractedLicenseInfo1.setComment("extracted license info comment");
    extractedLicenseInfo1.setSeeAlso(List.of("http://see.also", "http://extracted.license"));

    var extractedLicenseInfo2 = new ExtractedLicenseInfo(modelStore, documentUri, "LicenseRef-two",
        copyManager, true);
    extractedLicenseInfo2.setExtractedText("extracted text");
    extractedLicenseInfo2.setName("extracted license info name");
    extractedLicenseInfo2.setComment("extracted license info comment");
    extractedLicenseInfo2.setSeeAlso(List.of("http://another.license"));

    document.setExtractedLicenseInfos(List.of(extractedLicenseInfo1, extractedLicenseInfo2));

    var alladin = LicenseInfoFactory.parseSPDXLicenseString("Aladdin");
    var alladinWithException = LicenseInfoFactory.parseSPDXLicenseString(
        "Aladdin WITH Classpath-exception-2.0");
    var dldeby20 = LicenseInfoFactory.parseSPDXLicenseString("DL-DE-BY-2.0");
    var licenseRef1 = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-1", modelStore,
        documentUri, copyManager);
    var licenseRef1WithException = LicenseInfoFactory.parseSPDXLicenseString(
        "LicenseRef-1 WITH u-boot-exception-2.0", modelStore, documentUri, copyManager);
    var licenseRef2 = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-two", modelStore,
        documentUri, copyManager);
    var licenseRef1or2 = document.createDisjunctiveLicenseSet(List.of(licenseRef1,
        licenseRef2));
    var licenseRef1WithExceptionOr2 =
        document.createDisjunctiveLicenseSet(List.of(licenseRef1WithException,
            licenseRef2));
    var declaredLicense = document.createConjunctiveLicenseSet(List.of(licenseRef1or2,
        alladinWithException));
    var concludedLicense =
        document.createConjunctiveLicenseSet(List.of(licenseRef1WithExceptionOr2,
            alladinWithException));
    var sha1Checksum = createSha1Checksum(modelStore, documentUri);

    var fileA = document.createSpdxFile("SPDXRef-fileA", "./package/faa.txt", licenseRef1or2,
            List.of(licenseRef1, licenseRef2), null, sha1Checksum)
        .build();
    var fileB = document.createSpdxFile("SPDXRef-fileB", "./package/fbb.txt",
            alladinWithException,
            List.of(alladin, dldeby20), null, sha1Checksum)
        .build();

    var snippet = document.createSpdxSnippet("SPDXRef-somesnippet", "snippet name", alladin,
            List.of(alladin, dldeby20), null, fileB, 100, 200)
        .build();

    var spdxPackageVerificationCode = document.createPackageVerificationCode(
        "d6a770ba38583ed4bb4525bd96e50461655d2758", List.of());
    var spdxPackage = document.createPackage("SPDXRef-somepackage", "package name",
            concludedLicense,
            null, declaredLicense)
        .setLicenseInfosFromFile(List.of(licenseRef1or2, alladinWithException))
        .setPackageVerificationCode(spdxPackageVerificationCode)
        .setFiles(List.of(fileA, fileB))
        .build();
    document.getDocumentDescribes().add(spdxPackage);

    return document;
  }

  @Override
  public String getName() {
    return "generationLicenseTest";
  }
}
