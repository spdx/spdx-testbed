package org.spdx.testbed.generationTestCases;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.testUtilities;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class GenerationLicenseTestCaseTest {

    @Test
    public void correctInputShouldPassTest() throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var testCase = new GenerationLicenseTestCase();
        var inputFilePath = "src/test/resources/testInput/generation/LicenseTest.xml";

        testUtilities.assertThatTestCaseReturnsSuccess(testCase, inputFilePath);
    }
}
