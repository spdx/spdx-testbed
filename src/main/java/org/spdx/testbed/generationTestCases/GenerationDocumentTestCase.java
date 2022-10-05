package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;

import java.util.List;

public class GenerationDocumentTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo("Document test document");

        document.getCreationInfo().setComment("some creation comment").setLicenseListVersion("3.7");
        document.setComment("a document comment");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: Document Commenter (mail@mail.com)")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("Document level annotation")
                .setAnnotationType(AnnotationType.REVIEW);

        document.addAnnotation(annotation);

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);
        var externalDocumentRef = document.createExternalDocumentRef("DocumentRef-externaldocumentid", "some-external-uri", sha1Checksum);

        document.setExternalDocumentRefs(List.of(externalDocumentRef));

        var concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        var file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", concludedLicense,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
