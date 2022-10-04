package org.spdx.testbed;

import org.apache.commons.lang3.ArrayUtils;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.generationTestCases.*;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class Main {
    //TODO: maybe exceptions should be caught?
    public static void main(String[] args) throws IOException, InvalidSPDXAnalysisException, InvalidFileNameException {
        //TODO: catch wrong number of arguments
        if (args.length > 1) {
            var testCase = args[0];
            var remainingArgs = ArrayUtils.remove(args, 0);

            switch (testCase) {
                case "generationMinimalTest":
                    (new GenerationMinimalTestCase()).test(remainingArgs);
                    break;
                case "generationDocumentTest":
                    (new GenerationDocumentTestCase()).test(remainingArgs);
                    break;
                case "generationPackageTest":
                    (new GenerationPackageTestCase()).test(remainingArgs);
                    break;
                case "generationFileTest":
                    (new GenerationFileTestCase()).test(remainingArgs);
                    break;
                case "generationSnippetTest":
                    (new GenerationSnippetTestCase()).test(remainingArgs);
                    break;
                case "generationLicenseTest":
                    (new GenerationLicenseTestCase()).test(remainingArgs);
                    break;
                case "generationRelationshipTest":
                    (new GenerationRelationshipTestCase()).test(remainingArgs);
                    break;
                case "generationExtractedLicenseInfoTest":
                    (new GenerationExtractedLicenseInfoTestCase()).test(remainingArgs);
                    break;
            }
        }
    }
}
