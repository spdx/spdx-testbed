package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationMinimalTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        SpdxDocument document = createSpdxDocumentWithBasicInfo("Minimal test document");

        InMemSpdxStore modelStore = (InMemSpdxStore) document.getModelStore();
        String documentUri = document.getDocumentUri();

        Checksum sha1Checksum = createSha1Checksum(modelStore, documentUri);
        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", concludedLicense, List.of(), "Copyright 2022 some guy", sha1Checksum).build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
