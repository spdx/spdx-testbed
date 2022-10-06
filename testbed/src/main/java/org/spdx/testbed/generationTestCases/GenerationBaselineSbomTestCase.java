package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.license.LicenseInfoFactory;

import java.util.List;

public class GenerationBaselineSbomTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo("Baseline SBOM test document");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);
        var spdxPackageVerificationCode = document.createPackageVerificationCode("d6a770ba38583ed4bb4525bd96e50461655d2758", List.of("./package.spdx"));

        var lgpl3_0_only = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        var spdxPackage = document.createPackage("SPDXRef-somepackage", "some package", lgpl3_0_only,
                        "Copyright 2008-2010 John Smith", lgpl3_0_only)
                .setVersionInfo("2.2.1")
                .setSupplier("Person: Jane Doe (jane.doe@example.com)")
                .setPackageVerificationCode(spdxPackageVerificationCode)
                .setChecksums(List.of(sha1Checksum))
                .build();

        document.getDocumentDescribes().add(spdxPackage);

        return document;
    }


}
