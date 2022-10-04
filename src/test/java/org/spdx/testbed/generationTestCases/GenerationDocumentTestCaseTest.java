package org.spdx.testbed.generationTestCases;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.testUtilities;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class GenerationDocumentTestCaseTest {

    @Test
    public void correctInputShouldPassTest() throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var testCase = new GenerationDocumentTestCase();
        var inputFilePath = "src/test/resources/testInput/generation/DocumentTest.xml";

        testUtilities.assertThatTestCaseReturnsExitValueZero(testCase, inputFilePath);
    }
}
