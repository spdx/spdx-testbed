package org.spdx.toolsJavaSolver.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.license.LicenseInfoFactory;

import java.util.List;

public class GenerationMinimalTestCase {

    public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
        var document = GenerationUtil.createSpdxDocumentWithBasicInfo("Minimal test document");

        var sha1Checksum = GenerationUtil.createSha1Checksum(document.getModelStore(), document.getDocumentUri());
        var concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", concludedLicense, List.of(), "Copyright 2022 some guy", sha1Checksum).build();

        document.getDocumentDescribes().add(file);

        return document;
    }

}
