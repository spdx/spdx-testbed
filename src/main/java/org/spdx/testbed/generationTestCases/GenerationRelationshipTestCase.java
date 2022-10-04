package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.RelationshipType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationRelationshipTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        SpdxDocument document = createSpdxDocumentWithBasicInfo("Relationship test document");

        InMemSpdxStore modelStore = (InMemSpdxStore) document.getModelStore();
        String documentUri = document.getDocumentUri();

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");

        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only");

        SpdxFile fileA = document.createSpdxFile("SPDXRef-fileA", "./fileA.c", concludedLicense,
                        List.of(), "Copyright 2022 some person", sha1Checksum)
                .build();

        SpdxFile fileB = document.createSpdxFile("SPDXRef-fileB", "./fileB.c", concludedLicense,
                        List.of(), "Copyright 2022 some person", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(fileA);
        document.getDocumentDescribes().add(fileB);

        for (RelationshipType relationshipType : RelationshipType.values()) {
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
