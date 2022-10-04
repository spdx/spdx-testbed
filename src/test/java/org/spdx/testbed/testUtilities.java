package org.spdx.testbed;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class testUtilities {

    public static void assertThatTestCaseReturnsExitValueZero (TestCase testCase, String inputFilePath) throws IOException, InvalidSPDXAnalysisException, InvalidFileNameException {
        int returnCode = testCase.test(new String[]{inputFilePath});
        assertThat(returnCode).isEqualTo(0);
    }
}
