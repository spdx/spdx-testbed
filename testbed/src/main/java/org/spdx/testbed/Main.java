// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.util.TestCaseSelector;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InvalidSPDXAnalysisException,
            InvalidFileNameException {
        var options = new Options();
        options.addOption(Option.builder("t").longOpt("test_cases")
                .desc("For possible values see the readme. At least one of -c or -t has to be " +
                        "specified")
                .hasArgs().argName("TEST_CASES")
                .build());
        options.addOption(Option.builder("c").longOpt("test_categories")
                .desc("For possible values see the readme. At least one of -c or -t has to be " +
                        "specified")
                .hasArg().argName("TEST_CATEGORIES")
                .build());
        options.addOption(Option.builder("f").longOpt("input_files")
                .desc("The files to be processed").hasArgs().argName("FILES").required().build());
        options.addOption(Option.builder("h").longOpt("help").desc("Display usage").required(false)
                .build());

        var parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());

            printUsage(options);
            System.exit(1);
            return;
        }

        if (cmd.hasOption("h")) {
            printUsage(options);
            System.exit(0);
        }

        if (!cmd.hasOption("t") && !cmd.hasOption("c")) {
            printUsage(options);
            System.exit(1);
        }

        var testCaseSelector = new TestCaseSelector();
        List<TestCase> selectedTestCases;
        try {
            selectedTestCases = testCaseSelector.selectTestCases(cmd.getOptionValues("t"),
                    cmd.getOptionValues("c"));
        } catch (IllegalArgumentException ex) {
            // Just a safety/compiler check, this should be covered above
            printUsage(options);
            System.exit(1);
            return;
        }

        var files = cmd.getOptionValues("f");

        if (selectedTestCases.size() != files.length) {
            System.err.println("The number of input files does not match the number of " +
                    "selected test cases. " + files.length + " input files were provided, but" +
                    " " + selectedTestCases.size() + " test cases were selected:");
            selectedTestCases.forEach(innerTestCase -> System.err.println(innerTestCase.getName()));
            System.exit(1);
        }

        // TODO: do something with the test results
        for (int i = 0; i < selectedTestCases.size(); i++) {
            var inputFile = files[i];
            selectedTestCases.get(i).test(inputFile);
        }
    }

    private static void printUsage(Options options) {
        var helper = new HelpFormatter();
        var helpHeader = "Test if the input files solve the specified test cases.\n\n";
        var helpFooter = "\n";
        helper.printHelp("spdx-tools-java-solver.jar", helpHeader, options, helpFooter, true);
    }
}
