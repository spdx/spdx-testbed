package org.spdx.testbed.generationTestCases;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerationPackageTestCaseTest {

    @Test
    public void correctInputShouldPassTest() throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var testCase = new GenerationPackageTestCase();
        var inputFilePath = "src/test/resources/testInput/generation/PackageTest.xml";

        int returnCode = testCase.test(new String[]{inputFilePath});
        assertThat(returnCode).isEqualTo(0);
    }
}
