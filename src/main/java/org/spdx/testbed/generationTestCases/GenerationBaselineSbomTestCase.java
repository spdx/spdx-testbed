package org.spdx.testbed.generationTestCases;

import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationBaselineSbomTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        SpdxCreatorInformation creationInfo = document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z");

        document.setCreationInfo(creationInfo);
        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setName("SPDX-tool-test");

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
