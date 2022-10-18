// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsJavaSolver.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;

import java.util.List;

public class GenerationMinimalTestCase {

    public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
        var document = GenerationUtil.createSpdxDocumentWithBasicInfo();

        var sha1Checksum = GenerationUtil.createSha1Checksum(document.getModelStore(), document.getDocumentUri());
        var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", null, List.of(), null, sha1Checksum).build();

        document.getDocumentDescribes().add(file);

        return document;
    }

}
