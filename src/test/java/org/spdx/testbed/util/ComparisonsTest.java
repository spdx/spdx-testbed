package org.spdx.testbed.util;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.*;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.spdx.testbed.util.Comparisons.Tuple;
import static org.spdx.testbed.util.Comparisons.findDifferences;

public class ComparisonsTest {

    private static final String DOCUMENT_URI = "namespace";
    private ModelCopyManager copyManager;
    private IModelStore modelStore;

    @BeforeEach
    public void setup() {
        copyManager = new ModelCopyManager();
        modelStore = new InMemSpdxStore();
    }

    @Test
    public void detectDifferentClass() throws InvalidSPDXAnalysisException {
        var document = SpdxModelFactory.createSpdxDocument(modelStore, DOCUMENT_URI, copyManager);
        var file = new SpdxFile(modelStore, DOCUMENT_URI, "fileId", copyManager, true);

        var differences = findDifferences(document, file, true);

        assertThat(differences).containsExactly(Map.entry("class", new Tuple<>(SpdxDocument.class
                , SpdxFile.class)));
    }

    @Test
    public void detectExclusiveProperties() throws InvalidSPDXAnalysisException {
        var firstSnippet = new SpdxSnippet(modelStore, DOCUMENT_URI, "firstSnippetId",
                copyManager, true);
        var secondSnippet = new SpdxSnippet(modelStore, DOCUMENT_URI, "secondSnippetId",
                copyManager, true);
        var file = new SpdxFile(modelStore, DOCUMENT_URI, "fileId", copyManager, true);
        firstSnippet.setName("firstSnippetName");
        secondSnippet.setSnippetFromFile(file);

        var differences = findDifferences(firstSnippet, secondSnippet, true);

        assertExclusivePropertyInFirst(differences, "name", "firstSnippetName");
        assertExclusivePropertyInSecond(differences, "snippetFromFile", file);
        assertThat(differences.size()).isEqualTo(2);
    }

    @Test
    public void exclusivePropertiesEquivalentToNullAreIgnored() throws InvalidSPDXAnalysisException {
        var firstSnippet = new SpdxSnippet(modelStore, DOCUMENT_URI, "firstSnippetId",
                copyManager, true);
        var secondSnippet = new SpdxSnippet(modelStore, DOCUMENT_URI, "secondSnippetId",
                copyManager, true);
        firstSnippet.setName(SpdxConstants.NOASSERTION_VALUE);
        firstSnippet.setLicenseConcluded(new SpdxNoAssertionLicense());
        // Adding and removing the annotation results in an empty ModelCollection
        Annotation annotation = new Annotation(modelStore, DOCUMENT_URI, "annotationId",
                copyManager, true);
        firstSnippet.addAnnotation(annotation);
        firstSnippet.removeAnnotation(annotation);

        var differences = findDifferences(firstSnippet, secondSnippet, true);

        assertThat(differences).isEmpty();
    }

    @Test
    public void detectNestedDifference() throws InvalidSPDXAnalysisException {
        var secondDocumentUri = "secondDocumentUri";
        var annotation = new Annotation(modelStore, secondDocumentUri, "annotationId",
                copyManager, true)
                .setComment("annotationComment");
        var firstFile = new SpdxFile(modelStore, DOCUMENT_URI, "fileId", copyManager, true);
        firstFile.setAnnotations(List.of(annotation));
        var secondFile = new SpdxFile(modelStore, DOCUMENT_URI, "secondFileId", copyManager, true);
        var firstDocument = SpdxModelFactory.createSpdxDocument(modelStore, DOCUMENT_URI,
                        copyManager)
                .setDocumentDescribes(List.of(firstFile));
        var secondDocument = SpdxModelFactory.createSpdxDocument(modelStore, secondDocumentUri,
                        copyManager)
                .setDocumentDescribes(List.of(secondFile));

        var differences = findDifferences(firstDocument, secondDocument, true,
                Set.of(SpdxConstants.PROP_SPDX_CREATION_INFO));

        // TODO: Make validation more precise once the tools provide more precise results
        assertThat(differences).containsKey(SpdxConstants.PROP_RELATIONSHIP);
    }


    private void assertExclusivePropertyInFirst(Map<String, Tuple<?>> differences,
                                                String propertyName, Object propertyValue) {
        assertThat(differences).contains(Map.entry(propertyName, new Tuple<>(propertyValue, null)));
    }

    private void assertExclusivePropertyInSecond(Map<String, Tuple<?>> differences,
                                                 String propertyName, Object propertyValue) {
        assertThat(differences).contains(Map.entry(propertyName, new Tuple<>(null, propertyValue)));
    }

}
