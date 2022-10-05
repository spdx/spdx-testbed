package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;

public class GenerationSnippetTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        SpdxDocument document = createSpdxDocumentWithBasicInfo("Snippet test document");

        InMemSpdxStore modelStore = (InMemSpdxStore) document.getModelStore();
        String documentUri = document.getDocumentUri();

        Annotation annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: Snippet Annotator")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("Snippet level annotation")
                .setAnnotationType(AnnotationType.OTHER);

        Checksum sha1Checksum = createSha1Checksum(modelStore, documentUri);

        AnyLicenseInfo lgpl2_0onlyANDLicenseRef_2 = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only AND LicenseRef-3");
        AnyLicenseInfo gpl2_0only = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");

        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", gpl2_0only,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        SpdxSnippet spdxSnippet = document.createSpdxSnippet("SPDXRef-somesnippet", "from linux kernel", gpl2_0only,
                        List.of(lgpl2_0onlyANDLicenseRef_2), "Copyright 2008-2010 John Smith", file, 100, 400)
                .addAnnotation(annotation)
                .setLineRange(30, 40)
                .setLicenseComments("snippy license comment")
                .setComment("snippy comment")
//                .addAttributionText("The GNU C Library is free software.  See the file COPYING.LIB for copying conditions, and LICENSES for notices about a few contributions that require these additional notices to be distributed.  License copyright years may be listed using range notation, e.g., 1996-2015, indicating that every year in the range, inclusive, is a copyrightable year that would otherwise be listed individually.")
                .build();

        document.getDocumentDescribes().add(spdxSnippet);

        return document;
    }
}
