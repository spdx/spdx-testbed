package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.license.LicenseInfoFactory;

import java.util.List;

public class GenerationLicenseTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo("License test document");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);

        var concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only OR LicenseRef-2");
        var firstLicense = LicenseInfoFactory.parseSPDXLicenseString("GPL-3.0-only WITH GPL-3.0-linking-exception AND LicenseRef-1");
        var secondLicense = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-1 AND LicenseRef-2 OR LicenseRef-1 WITH another-exception");
        var thirdLicense = LicenseInfoFactory.parseSPDXLicenseString("CERN-OHL-1.2 WITH u-boot-exception-2.0 AND LicenseRef-2 OR LicenseRef-1 WITH another-exception");

        var file = document.createSpdxFile("SPDXRef-somefile", "./package/foo.c", concludedLicense, List.of(firstLicense, secondLicense, thirdLicense), "Copyright 2008-2010 John Smith", sha1Checksum).build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
