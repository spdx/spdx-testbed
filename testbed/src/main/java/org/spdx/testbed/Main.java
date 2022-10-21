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
import org.spdx.testbed.util.TestCaseFinder;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        var testCaseFinder = new TestCaseFinder();

        var testCasesByNamesOptional = Optional.ofNullable(cmd.getOptionValues("t"))
                .map(namesArray -> Arrays.stream(namesArray)
                        .collect(Collectors.toList()))
                .map(testCaseFinder::findTestCasesByNames);
        var testCasesByCategoriesOptional = Optional.ofNullable(cmd.getOptionValues("c"))
                .map(categoriesArray -> Arrays.stream(categoriesArray)
                        .map(TestCaseCategory::fromString)
                        .collect(Collectors.toList()))
                .map(testCaseFinder::findTestCasesByCategories);
        var files = cmd.getOptionValues("f");

        List<TestCase> selectedTestCases;
        if (testCasesByNamesOptional.isPresent()) {
            // If test cases are specified by name, we retain the provided ordering
            selectedTestCases = testCasesByNamesOptional.get();
            testCasesByCategoriesOptional.ifPresent(selectedTestCases::retainAll);
        } else if (testCasesByCategoriesOptional.isPresent()) {
            selectedTestCases = testCasesByCategoriesOptional.get();
            // If selecting only by category, sort alphabetically
            selectedTestCases.sort(TestCase::compareTo);
        } else {
            // Just a safety/compiler check, this case is already covered above
            printUsage(options);
            System.exit(1);
            return;
        }

        if (selectedTestCases.size() != files.length) {
            System.err.println("The number of input files does not match the number of " +
                    "selected test cases. " + files.length + " input files were provided, but" +
                    " " + selectedTestCases.size() + " test cases were selected:");
            selectedTestCases.forEach(innerTestCase -> System.err.println(innerTestCase.getName()));
            System.exit(1);
        }

        // TODO: do something with the test results
        for (int i = 0; i < selectedTestCases.size(); i++) {
            var inputFile = new String[]{files[i]};
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
