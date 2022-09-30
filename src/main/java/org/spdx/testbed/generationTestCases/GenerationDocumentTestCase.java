package org.spdx.testbed.generationTestCases;

import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationDocumentTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        SpdxCreatorInformation creationInfo = document.createCreationInfo(
                        List.of("Tool: test-tool"), "2022-01-01T00:00:00Z")
                .setComment("some creation comment")
                .setLicenseListVersion("3.7");

        document.setCreationInfo(creationInfo);
        document.setName("SPDX-tool-test");
        document.setComment("a document comment");

        Annotation annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: Document Commenter (mail@mail.com)")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("File level annotation")
                .setAnnotationType(AnnotationType.REVIEW);

        document.addAnnotation(annotation);

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        var externalDocumentRef = document.createExternalDocumentRef("DocumentRef-externaldocumentid", "some-external-uri", sha1Checksum);

        document.setExternalDocumentRefs(List.of(externalDocumentRef));

        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", concludedLicense,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
