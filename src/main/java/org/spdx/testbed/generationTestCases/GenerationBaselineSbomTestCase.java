package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxPackage;
import org.spdx.library.model.SpdxPackageVerificationCode;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationBaselineSbomTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        SpdxDocument document = createSpdxDocumentWithBasicInfo("Baseline SBOM test document");

        InMemSpdxStore modelStore = (InMemSpdxStore) document.getModelStore();
        String documentUri = document.getDocumentUri();

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        SpdxPackageVerificationCode spdxPackageVerificationCode = document.createPackageVerificationCode("d6a770ba38583ed4bb4525bd96e50461655d2758", List.of("./package.spdx"));

        AnyLicenseInfo lgpl3_0_only = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        SpdxPackage spdxPackage = document.createPackage("SPDXRef-somepackage", "some package", lgpl3_0_only,
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
