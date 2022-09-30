package org.spdx.testbed.generationTestCases;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GenerationRelationshipTestCaseTest {

    @Test
    public void correctInputShouldPassTest() throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var testCase = new GenerationRelationshipTestCase();
        var inputFilePath = "src/test/resources/testInput/generation/RelationshipTest.xml";

        int returnCode = testCase.test(new String[]{inputFilePath});
        assertThat(returnCode).isEqualTo(0);
    }
}
