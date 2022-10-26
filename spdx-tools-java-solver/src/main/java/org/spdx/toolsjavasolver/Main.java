// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsjavasolver;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.toolsjavasolver.generationtestcases.GenerationBaselineSbomTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationDocumentTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationFileTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationLicenseTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationMinimalTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationPackageTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationRelationshipTestCase;
import org.spdx.toolsjavasolver.generationtestcases.GenerationSnippetTestCase;

/**
 * CLI entrypoint class for the tools-java solver.
 */
public class Main {

  /**
   * Main entrypoint method.
   */
  public static void main(String[] args) throws InvalidSPDXAnalysisException, IOException {
    var options = new Options();
    options.addOption(
        Option.builder("t").longOpt("test_case").desc("For possible values see some website.")
            .hasArg().argName("TESTCASE").required().build());
    options.addOption(
        Option.builder("f").longOpt("output").desc("The output file path").hasArg().argName("PATH")
            .required().build());
    options.addOption(
        Option.builder("h").longOpt("help").desc("Display usage").required(false).build());

    var parser = new DefaultParser();

    try {
      var cmd = parser.parse(options, args);

      if (cmd.hasOption("h")) {
        printUsage(options);
        System.exit(0);
      }

      var testCase = cmd.getOptionValue("t");
      String outputPath = cmd.getOptionValue("f");

      SpdxDocument outputDoc;
      var testCaseName = TestCaseName.fromString(testCase);

      switch (testCaseName) {
        case GENERATION_MINIMAL:
          outputDoc = GenerationMinimalTestCase.buildDocument();
          break;
        case GENERATION_BASELINE_SBOM:
          outputDoc = GenerationBaselineSbomTestCase.buildDocument();
          break;
        case GENERATION_DOCUMENT:
          outputDoc = GenerationDocumentTestCase.buildDocument();
          break;
        case GENERATION_FILE:
          outputDoc = GenerationFileTestCase.buildDocument();
          break;
        case GENERATION_SNIPPET:
          outputDoc = GenerationSnippetTestCase.buildDocument();
          break;
        case GENERATION_PACKAGE:
          outputDoc = GenerationPackageTestCase.buildDocument();
          break;
        case GENERATION_LICENSE:
          outputDoc = GenerationLicenseTestCase.buildDocument();
          break;
        case GENERATION_RELATIONSHIP:
          outputDoc = GenerationRelationshipTestCase.buildDocument();
          break;
        default:
          //TODO: add info about supported test cases
          System.err.print("Error: " + testCase
              + " is an unrecognized or unimplemented test case. Here is a list of possible test " 
              + "cases: (work in progress)\n");
          System.exit(1);
          return;
      }

      var modelStore = (MultiFormatStore) outputDoc.getModelStore();
      modelStore.serialize(outputDoc.getDocumentUri(), new FileOutputStream(outputPath));

    } catch (ParseException e) {
      System.err.println(e.getMessage());

      printUsage(options);
      System.exit(1);
    }
  }

  private static void printUsage(Options options) {
    var helper = new HelpFormatter();
    var helpHeader = "Output a file that solves the specified SPDX-testbed test case.\n\n";
    var helpFooter = "\n";
    helper.printHelp("testbed-solver.jar", helpHeader, options, helpFooter, true);
  }
}
