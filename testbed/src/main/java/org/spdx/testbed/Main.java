// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.util.TestCaseFinder;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidSPDXAnalysisException,
            InvalidFileNameException {
        // TODO: Add support for providing multiple files
        var options = new Options();
        options.addOption(Option.builder("t").longOpt("test_case")
                .desc("For possible values see the readme.").hasArg().argName("TESTCASE")
                .required().build());
        options.addOption(Option.builder("f").longOpt("input_files")
                .desc("The files to be processed").hasArgs().argName("FILES").required().build());
        options.addOption(Option.builder("h").longOpt("help").desc("Display usage").required(false)
                .build());

        var parser = new DefaultParser();

        try {
            var cmd = parser.parse(options, args);

            if (cmd.hasOption("h")) {
                printUsage(options);
                System.exit(0);
            }

            var testCase = cmd.getOptionValue("t");
            String[] files = cmd.getOptionValues("f");

            var testCaseName = TestCaseName.fromString(testCase);
            var testCaseFinder = new TestCaseFinder();
            var testCases = testCaseFinder.findTestCases(testCaseName);

            // TODO: do something with the test results
            for (var singleCase : testCases) {
                singleCase.test(files);
            }

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
