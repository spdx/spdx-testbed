package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.testbed.TestCase;
import org.spdx.testbed.util.Comparisons;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;

import java.io.File;
import java.io.IOException;

public abstract class GenerationTestCase implements TestCase {

    public SpdxDocument inputDoc;
    public SpdxDocument referenceDoc;

    public int test(String[] args) throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        parseArgsAndSetInputDoc(args);
        referenceDoc = buildReferenceDocument();
        var differences = Comparisons.findDifferences(referenceDoc, inputDoc, false);

        if (differences.isEmpty()) {
            return 0;
        }
        else {
            System.out.print("Test failure in "+this.getClass().getName()+". " +
                    "The input document did not meet the expectations. The following differences were detected:");
            System.out.print(differences);
            return 1;
        }
    }

    protected void parseArgsAndSetInputDoc(String[] args) throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 input file path, but got " + args.length);
        }
        try {
            inputDoc = SpdxToolsHelper.deserializeDocument(new File(args[0]));
        } catch (InvalidSPDXAnalysisException e) {
            throw new InvalidSPDXAnalysisException("The input file does not seem to be a valid SPDX document: " + e);
        }
    }

    public abstract SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException;
}