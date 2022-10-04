package org.spdx.testbed;

import org.apache.commons.cli.*;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.generationTestCases.*;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidSPDXAnalysisException, InvalidFileNameException {
        Options options = new Options();
        //TODO: better help text, add a help option
        options.addOption(Option.builder("t").longOpt("test_case").desc("For possible values see some website.").hasArg().argName("TESTCASE").required().build());
        options.addOption(Option.builder("f").longOpt("input_files").desc("The file to be processed").hasArgs().argName("FILE").required().build());

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            String testCase = cmd.getOptionValue("t");
            String[] files = {cmd.getOptionValue("f")};

            switch (testCase) {
                case "generationMinimalTest":
                    (new GenerationMinimalTestCase()).test(files);
                    break;
                case "generationDocumentTest":
                    (new GenerationDocumentTestCase()).test(files);
                    break;
                case "generationPackageTest":
                    (new GenerationPackageTestCase()).test(files);
                    break;
                case "generationFileTest":
                    (new GenerationFileTestCase()).test(files);
                    break;
                case "generationSnippetTest":
                    (new GenerationSnippetTestCase()).test(files);
                    break;
                case "generationLicenseTest":
                    (new GenerationLicenseTestCase()).test(files);
                    break;
                case "generationRelationshipTest":
                    (new GenerationRelationshipTestCase()).test(files);
                    break;
                case "generationExtractedLicenseInfoTest":
                    (new GenerationExtractedLicenseInfoTestCase()).test(files);
                    break;
                default:
                    //TODO: add testcase value source
                    System.err.print("Error: " + testCase + " is an unrecognized test case. Refer to some website for a list of possible test cases.\n");
                    System.exit(1);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());

            HelpFormatter helper = new HelpFormatter();
            String helpHeader = "Test if the input file solves the specified test case.\n\n";
            String helpFooter = "\nFor possible options for testCase";
            helper.printHelp("spdx-testbed.jar", helpHeader, options, helpFooter, true);
            System.exit(1);
        }
    }
}
