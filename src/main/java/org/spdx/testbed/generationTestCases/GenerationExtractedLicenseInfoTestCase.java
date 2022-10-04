package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationExtractedLicenseInfoTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        SpdxDocument document = createSpdxDocumentWithBasicInfo("Extracted license information test document");

        InMemSpdxStore modelStore = (InMemSpdxStore) document.getModelStore();
        String documentUri = document.getDocumentUri();
        ModelCopyManager copyManager = document.getCopyManager();

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");

        ExtractedLicenseInfo extractedLicenseInfo = new ExtractedLicenseInfo(modelStore, documentUri, "LicenseRef-1", copyManager, true);
        extractedLicenseInfo.setExtractedText("some extracted text");
        extractedLicenseInfo.setName("some extracted license info name");
        extractedLicenseInfo.setComment("extracted license info comment");
        extractedLicenseInfo.setSeeAlso(List.of("some (cross reference/see also) url", "another url"));

        document.addExtractedLicenseInfos(extractedLicenseInfo);

        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", extractedLicenseInfo,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
