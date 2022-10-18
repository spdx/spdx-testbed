// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.generationTestCases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestResult;
import org.spdx.testbed.util.Comparisons;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class GenerationTestCase implements TestCase {

    public TestResult test(String[] args) throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        var inputDoc = parseArgsAndGetInputDoc(args);
        var referenceDoc = buildReferenceDocument();
        var differences = Comparisons.findDifferencesInSerializedJson(inputDoc, referenceDoc);

        if (differences.isEmpty()) {
            System.out.print(this.getClass().getSimpleName() + " succeeded!\n");
            return TestResult.builder().success(true).build();
        } else {
            System.out.println("Test failure in " + this.getClass().getSimpleName() + ". " +
                    "The input document did not meet the expectations. The following differences were detected:");
            var objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(objectMapper.writeValueAsString(differences));
            return TestResult.builder().success(false).differences(differences).build();
        }
    }

    protected SpdxDocument parseArgsAndGetInputDoc(String[] args) throws IOException, InvalidFileNameException, InvalidSPDXAnalysisException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expected 1 input file path, but got " + args.length);
        }
        try {
            return SpdxToolsHelper.deserializeDocument(new File(args[0]));
        } catch (InvalidSPDXAnalysisException e) {
            throw new InvalidSPDXAnalysisException("The input file does not seem to be a valid SPDX document: " + e.getMessage(), e);
        }
    }

    public abstract SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException;

    SpdxDocument createSpdxDocumentWithBasicInfo() throws InvalidSPDXAnalysisException {
        var modelStore = new InMemSpdxStore();
        var documentUri = "https://some.namespace";
        var copyManager = new ModelCopyManager();

        var document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        var creationInfo = document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z");

        document.setCreationInfo(creationInfo);
        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setName("document name");

        return document;
    }

    Checksum createSha1Checksum(IModelStore modelStore, String documentUri) throws InvalidSPDXAnalysisException {
        return Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
    }
}
