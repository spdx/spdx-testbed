package org.spdx.toolsJavaSolver.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;

import java.util.List;

public class GenerationBaselineSbomTestCase {

    public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
        var document = GenerationUtil.createSpdxDocumentWithBasicInfo();

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var sha1Checksum = GenerationUtil.createSha1Checksum(modelStore, documentUri);

        var spdxPackage = document.createPackage("SPDXRef-somepackage", "package name", null,
                        null, null)
                .setFilesAnalyzed(false)
                .setVersionInfo("2.2.1")
                .setSupplier("Person: Jane Doe (jane.doe@example.com)")
                .setChecksums(List.of(sha1Checksum))
                .build();

        document.getDocumentDescribes().add(spdxPackage);

        return document;
    }


}
