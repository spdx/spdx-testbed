package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationExtractedLicenseInfoTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        SpdxDocument document = createSpdxDocumentWithBasicInfo("Extracted license information test document");

        InMemSpdxStore modelStore = (InMemSpdxStore) document.getModelStore();
        String documentUri = document.getDocumentUri();
        ModelCopyManager copyManager = document.getCopyManager();

        Checksum sha1Checksum = createSha1Checksum(modelStore, documentUri);

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
