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
public class GenerationRelationshipTestCaseTest {

  @Test
  public void correctInputShouldPassTest()
      throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
    var testCase = new GenerationRelationshipTestCase();
    var inputFilePath = "src/test/resources/testInput/generation/RelationshipTest.xml";

    TestUtilities.assertThatTestCaseReturnsSuccess(testCase, inputFilePath);
  }
}
