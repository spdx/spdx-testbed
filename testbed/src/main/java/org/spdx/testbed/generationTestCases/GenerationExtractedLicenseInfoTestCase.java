package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.license.ExtractedLicenseInfo;

import java.util.List;

public class GenerationExtractedLicenseInfoTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo("Extracted license information test document");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();
        var copyManager = document.getCopyManager();

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);

        var extractedLicenseInfo = new ExtractedLicenseInfo(modelStore, documentUri, "LicenseRef-1", copyManager, true);
        extractedLicenseInfo.setExtractedText("some extracted text");
        extractedLicenseInfo.setName("some extracted license info name");
        extractedLicenseInfo.setComment("extracted license info comment");
        extractedLicenseInfo.setSeeAlso(List.of("some (cross reference/see also) url", "another url"));

        document.addExtractedLicenseInfos(extractedLicenseInfo);

        var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", extractedLicenseInfo,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
