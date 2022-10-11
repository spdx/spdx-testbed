package org.spdx.toolsJavaSolver.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.license.LicenseInfoFactory;

import java.util.List;

public class GenerationRelationshipTestCase {

    public static SpdxDocument buildDocument() throws InvalidSPDXAnalysisException {
        var document = GenerationUtil.createSpdxDocumentWithBasicInfo("Relationship test document");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var sha1Checksum = GenerationUtil.createSha1Checksum(modelStore, documentUri);

        var concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only");

        var fileA = document.createSpdxFile("SPDXRef-fileA", "./fileA.c", concludedLicense,
                        List.of(), "Copyright 2022 some person", sha1Checksum)
                .build();

        var fileB = document.createSpdxFile("SPDXRef-fileB", "./fileB.c", concludedLicense,
                        List.of(), "Copyright 2022 some person", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(fileA);
        document.getDocumentDescribes().add(fileB);

        for (var relationshipType : RelationshipType.values()) {
            if (relationshipType == RelationshipType.MISSING) {
                continue;
            }
            fileB.addRelationship(
                    document.createRelationship(
                            fileA, relationshipType, String.format("comment on %s", relationshipType.name())));

        }

        return document;
    }

}
