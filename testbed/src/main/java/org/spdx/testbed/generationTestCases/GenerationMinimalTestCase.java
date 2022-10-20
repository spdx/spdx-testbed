// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.testbed.TestCaseName;
import org.spdx.testbed.util.testClassification.TestName;

import java.util.List;

@TestName("generationMinimalTest")
public class GenerationMinimalTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo();

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);
        var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", null, List.of(), null
                        , sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }

    @Override
    public String getName() {
        return TestCaseName.GENERATION_MINIMAL.getFullName();
    }
}
