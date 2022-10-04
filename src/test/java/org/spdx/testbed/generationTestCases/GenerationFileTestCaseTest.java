package org.spdx.testbed.generationTestCases;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.testUtilities;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class GenerationFileTestCaseTest {

    @Test
    public void correctInputShouldPassTest() throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var testCase = new GenerationFileTestCase();
        var inputFilePath = "src/test/resources/testInput/generation/FileTest.xml";

        testUtilities.assertThatTestCaseReturnsExitValueZero(testCase, inputFilePath);
    }
}
