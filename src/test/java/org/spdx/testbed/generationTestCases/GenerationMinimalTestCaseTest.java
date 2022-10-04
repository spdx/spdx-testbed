package org.spdx.testbed.generationTestCases;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.testUtilities;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class GenerationMinimalTestCaseTest {

    @Test
    public void correctInputShouldPassTest() throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var testCase = new GenerationMinimalTestCase();
        var inputFilePath = "src/test/resources/testInput/generation/MinimalTest.xml";

        testUtilities.assertThatTestCaseReturnsExitValueZero(testCase, inputFilePath);
    }
}
