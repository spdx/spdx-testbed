// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class testUtilities {

    public static void assertThatTestCaseReturnsSuccess(TestCase testCase, String inputFilePath) throws IOException, InvalidSPDXAnalysisException, InvalidFileNameException {
        var testResult = testCase.test(inputFilePath);
        assertThat(testResult.success).isTrue();
    }
}
