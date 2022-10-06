package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;

import java.util.List;

public class GenerationMinimalTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo("Minimal test document");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);
        var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", null, List.of(), null, sha1Checksum).build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
