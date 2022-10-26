// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsJavaSolver.generationTestCases;

import java.util.List;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

public class GenerationUtil {

  static Checksum createSha1Checksum(IModelStore modelStore, String documentUri)
      throws InvalidSPDXAnalysisException {
    return Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1,
        "d6a770ba38583ed4bb4525bd96e50461655d2758");
  }

  static SpdxDocument createSpdxDocumentWithBasicInfo() throws InvalidSPDXAnalysisException {
    var modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML,
        MultiFormatStore.Verbose.COMPACT);
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
}
