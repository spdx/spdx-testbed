// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed;

import org.apache.commons.cli.*;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.generationTestCases.*;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidSPDXAnalysisException, InvalidFileNameException {
        var options = new Options();
        options.addOption(Option.builder("t").longOpt("test_case").desc("For possible values see some website.").hasArg().argName("TESTCASE").required().build());
        options.addOption(Option.builder("f").longOpt("input_files").desc("The files to be processed").hasArgs().argName("FILES").required().build());
        options.addOption(Option.builder("h").longOpt("help").desc("Display usage").required(false).build());

        var parser = new DefaultParser();

        try {
            var cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }

            var testCase = cmd.getOptionValue("t");
            String[] files = cmd.getOptionValues("f");

            TestResult testResult = null;
            var testCaseName = TestCaseName.fromString(testCase);

            switch (testCaseName) {
                case GENERATION_MINIMAL:
                    testResult = (new GenerationMinimalTestCase()).test(files);
                    break;
                case GENERATION_BASELINE_SBOM:
                    testResult = (new GenerationBaselineSbomTestCase()).test(files);
                    break;
                case GENERATION_DOCUMENT:
                    testResult = (new GenerationDocumentTestCase()).test(files);
                    break;
                case GENERATION_PACKAGE:
                    testResult = (new GenerationPackageTestCase()).test(files);
                    break;
                case GENERATION_FILE:
                    testResult = (new GenerationFileTestCase()).test(files);
                    break;
                case GENERATION_SNIPPET:
                    testResult = (new GenerationSnippetTestCase()).test(files);
                    break;
                case GENERATION_LICENSE:
                    testResult = (new GenerationLicenseTestCase()).test(files);
                    break;
                case GENERATION_RELATIONSHIP:
                    testResult = (new GenerationRelationshipTestCase()).test(files);
                    break;
            }

            //TODO: do something with the testResult

        } catch (ParseException e) {
            System.err.println(e.getMessage());

            printUsage(options);
            System.exit(1);
        }
    }

    private static void printUsage(Options options) {
        var helper = new HelpFormatter();
        var helpHeader = "Test if the input files solve the specified test case.\n\n";
        var helpFooter = "\n";
        helper.printHelp("spdx-tools-java-solver.jar", helpHeader, options, helpFooter, true);
    }
}
