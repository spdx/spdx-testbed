// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationtestcases;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.TestUtilities;
import org.spdx.tools.InvalidFileNameException;

/**
 * Test.
 */
public class GenerationLicenseTestCaseTest {

  @Test
  public void correctInputShouldPassTest()
      throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
    var testCase = new GenerationLicenseTestCase();
    var inputFilePath = "src/test/resources/testInput/generation/LicenseTest.xml";

    TestUtilities.assertThatTestCaseReturnsSuccess(testCase, inputFilePath);
  }
}
