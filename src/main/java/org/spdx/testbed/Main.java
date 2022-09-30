package org.spdx.testbed;

import org.apache.commons.lang3.ArrayUtils;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.generationTestCases.*;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class Main {
    //TODO: maybe exceptions should be caught?
    public static void main(String[] args) throws IOException, InvalidSPDXAnalysisException, InvalidFileNameException {
        if (args.length > 2) {
            var testCase = args[0];
            var remainingArgs = ArrayUtils.remove(args, 0);

            switch (testCase) {
                case "generationMinimalTest":
                    (new GenerationMinimalTestCase()).test(remainingArgs);
                case "generationDocumentTest":
                    (new GenerationDocumentTestCase()).test(remainingArgs);
                case "generationPackageTest":
                    (new GenerationPackageTestCase()).test(remainingArgs);
                case "generationFileTest":
                    (new GenerationFileTestCase()).test(remainingArgs);
                case "generationSnippetTest":
                    (new GenerationSnippetTestCase()).test(remainingArgs);
                case "generationLicenseTest":
                    (new GenerationLicenseTestCase()).test(remainingArgs);
                case "generationRelationshipTest":
                    (new GenerationRelationshipTestCase()).test(remainingArgs);
                case "generationExtractedLicenseInfoTest":
                    (new GenerationExtractedLicenseInfoTestCase()).test(remainingArgs);
            }
        }
    }
}
