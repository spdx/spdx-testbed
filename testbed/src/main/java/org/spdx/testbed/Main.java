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

            TestResult testResult;
            switch (testCase) {
                case "generationMinimalTest":
                    testResult = (new GenerationMinimalTestCase()).test(files);
                    break;
                case "generationDocumentTest":
                    testResult = (new GenerationDocumentTestCase()).test(files);
                    break;
                case "generationPackageTest":
                    testResult = (new GenerationPackageTestCase()).test(files);
                    break;
                case "generationFileTest":
                    testResult = (new GenerationFileTestCase()).test(files);
                    break;
                case "generationSnippetTest":
                    testResult = (new GenerationSnippetTestCase()).test(files);
                    break;
                case "generationLicenseTest":
                    testResult = (new GenerationLicenseTestCase()).test(files);
                    break;
                case "generationRelationshipTest":
                    testResult = (new GenerationRelationshipTestCase()).test(files);
                    break;
                case "generationExtractedLicenseInfoTest":
                    testResult = (new GenerationExtractedLicenseInfoTestCase()).test(files);
                    break;
                default:
                    //TODO: add info about supported test cases
                    System.err.print("Error: " + testCase + " is an unrecognized test case. Here is a list of possible test cases: (work in progress)\n");
                    System.exit(1);
                    return;
            }

            System.out.print(testResult.outputMessage);

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
        helper.printHelp("spdx-testbed.jar", helpHeader, options, helpFooter, true);
    }
}
