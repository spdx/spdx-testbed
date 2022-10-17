package org.spdx.testbed.util;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.SpdxSnippet;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.ExternalExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.spdx.testbed.util.Comparisons.Tuple;
import static org.spdx.testbed.util.Comparisons.findDifferences;
import static org.spdx.testbed.util.Comparisons.findDifferencesInSerializedJson;

public class ComparisonsTest {

    private static final String DOCUMENT_URI = "namespace";
    private static final ObjectMapper MAPPER = new ObjectMapper();
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
    public void detectRootLevelDifference() throws InvalidSPDXAnalysisException {
        var minimalDocument = buildMinimalDocumentWithFile();
        var secondDocument = buildMinimalDocumentWithFile();
        secondDocument.setName("newName");
        var expectedNameDifference = new Difference(new TextNode(minimalDocument.getName()
                .get()), new TextNode(secondDocument.getName().get()), "/name", "");

        var differences = findDifferencesInSerializedJson(minimalDocument, secondDocument);

        assertThat(differences).containsExactly(expectedNameDifference);
    }

    @Test
    public void detectNestedDifferenceInList() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        var firstFile = (SpdxFile) firstDoc.getDocumentDescribes().stream().findFirst().get();
        firstFile.getFileContributors().add("fileContributor");
        var secondFile = (SpdxFile) secondDoc.getDocumentDescribes().stream().findFirst().get();
        secondFile.getFileContributors().add("newContributor");

        var firstExpectedDifference = new Difference(new TextNode("fileContributor"), null,
                "/files/0/fileContributors/0", "No matching element in second list and no id to " +
                "determine a candidate.");
        var secondExpectedDifference = new Difference(null, new TextNode("newContributor"),
                "/files/0/fileContributors/0", "No matching element in first list and no id to " +
                "determine a candidate.");

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).containsExactlyInAnyOrder(firstExpectedDifference,
                secondExpectedDifference);
    }

    @Test
    public void showDifferencesOfListElementsMatchedById() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        var sha1Checksum = Checksum.create(modelStore, firstDoc.getDocumentUri(),
                ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        var file = firstDoc.createSpdxFile("SPDXRef-different", "./foo.txt", null,
                        List.of(), null, sha1Checksum)
                .build();
        var fileWithSameIdButDifferentProperties = secondDoc.createSpdxFile("SPDXRef-different",
                        "./bar.txt", null, List.of(), null, sha1Checksum)
                .build();
        var identicalFileInBothDocs = firstDoc.createSpdxFile("SPDXRef-identical", "./foo.txt",
                null, List.of(), null, sha1Checksum).build();

        // It looks like these are reordered somewhere in the serialization process that we use 
        // during comparison, so putting them in reverse order doesn't really matter at the moment.
        firstDoc.setDocumentDescribes(List.of(file, identicalFileInBothDocs));
        secondDoc.setDocumentDescribes(List.of(identicalFileInBothDocs,
                fileWithSameIdButDifferentProperties));

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences.size()).isEqualTo(1);
        var difference = differences.get(0);
        assertThat(difference.getFirstValue()).isEqualTo(new TextNode("./foo.txt"));
        assertThat(difference.getSecondValue()).isEqualTo(new TextNode("./bar.txt"));
        // Because of the reordering mentioned above, we avoid asserting on the exact index in 
        // the list
        assertThat(difference.getPath()).startsWith("/files/");
        assertThat(difference.getPath()).endsWith("fileName");
    }

    @Test
    public void detectNestedDifference() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        firstDoc.getCreationInfo().setComment("firstComment");
        secondDoc.getCreationInfo().setComment("secondComment");

        var expectedDifference = new Difference(new TextNode("firstComment"), new TextNode(
                "secondComment"), "/" + SpdxConstants.PROP_SPDX_CREATION_INFO + "/comment", "");

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).containsExactly(expectedDifference);
    }

    @Test
    public void detectAdditionalProperty() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();
        var annotationComment = "Completely new annotation!";
        var annotation = new Annotation("annotationId").setComment(annotationComment);
        firstDoc.addAnnotation(annotation);

        var expectedAnnotationsNode = MAPPER.createArrayNode();
        var annotationNode = MAPPER.createObjectNode();
        annotationNode.put("comment", annotationComment);
        expectedAnnotationsNode.add(annotationNode);

        var expectedDifference = new Difference(expectedAnnotationsNode, null, "/annotations", "");

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).containsExactly(expectedDifference);
    }

    @Test
    public void detectMissingProperty() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();
        var annotationComment = "Completely new annotation!";
        var annotation = new Annotation("annotationId").setComment(annotationComment);
        secondDoc.addAnnotation(annotation);

        var expectedAnnotationsNode = MAPPER.createArrayNode();
        var annotationNode = MAPPER.createObjectNode();
        annotationNode.put("comment", annotationComment);
        expectedAnnotationsNode.add(annotationNode);
        var expectedDifference = new Difference(null, expectedAnnotationsNode, "/annotations", "");

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).containsExactly(expectedDifference);
    }

    @Test
    public void ignoreReorderedLists() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        var firstAnnotation = new Annotation("firstAnnotationId").setComment("first annotation");
        var secondAnnotation = new Annotation("secondAnnotationId").setComment("second annotation");

        // annotations are added in different orders
        firstDoc.addAnnotation(firstAnnotation);
        firstDoc.addAnnotation(secondAnnotation);
        secondDoc.addAnnotation(secondAnnotation);
        secondDoc.addAnnotation(firstAnnotation);

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).isEmpty();
    }

    @Test
    public void ignoreNoAssertionLicense() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        var firstFile = (SpdxFile) firstDoc.getDocumentDescribes().stream().findFirst().get();
        firstFile.setLicenseConcluded(new SpdxNoAssertionLicense());
        var secondFile = (SpdxFile) secondDoc.getDocumentDescribes().stream().findFirst().get();
        secondFile.setLicenseConcluded(null);

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).isEmpty();
    }

    @Test
    public void ignoreNoAssertionValue() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        // a no assertion comment doesn't make that much sense, but it's the easiest optional 
        // property...
        firstDoc.setComment(SpdxConstants.NOASSERTION_VALUE);

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).isEmpty();
    }

    @Test
    public void ignoreEmptyList() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        // Adding and removing a value leaves an empty list
        var sha1Checksum = Checksum.create(secondDoc.getModelStore(), secondDoc.getDocumentUri(),
                ChecksumAlgorithm.SHA1,
                "d6a770ba38583ed4bb4525bd96e50461655d2758");
        var externalDocumentRef = secondDoc.createExternalDocumentRef("DocumentRef-1", "uri",
                sha1Checksum);
        secondDoc.setExternalDocumentRefs(List.of(externalDocumentRef));
        secondDoc.getExternalDocumentRefs().remove(externalDocumentRef);

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).isEmpty();
    }

    @Test
    public void stringsAreNormalizedBeforeComparison() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        firstDoc.setName(" " + firstDoc.getName() + "\r\n" + " ");
        secondDoc.setName(secondDoc.getName() + "\n");

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).isEmpty();
    }

    @Test
    public void noneAndNoAssertionIsEquivalentForStringAndUri() throws InvalidSPDXAnalysisException {
        var firstDoc = buildMinimalDocumentWithFile();
        var secondDoc = buildMinimalDocumentWithFile();

        // While putting these values in comments does not really make sense, handling 
        // should be identical for all string-valued fields, so it shouldn't matter.
        firstDoc.setComment(SpdxConstants.URI_VALUE_NONE);
        secondDoc.setComment(SpdxConstants.NONE_VALUE);
        firstDoc.getCreationInfo().setComment(SpdxConstants.URI_VALUE_NOASSERTION);
        secondDoc.getCreationInfo().setComment(SpdxConstants.NOASSERTION_VALUE);

        var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

        assertThat(differences).isEmpty();
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
        var annotation = new Annotation(modelStore, DOCUMENT_URI, "annotationId",
                copyManager, true);
        firstSnippet.addAnnotation(annotation);
        firstSnippet.removeAnnotation(annotation);

        var differences = findDifferences(firstSnippet, secondSnippet, true);

        assertThat(differences).isEmpty();
    }

    @Test
    public void oldDetectNestedDifference() throws InvalidSPDXAnalysisException {
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

    private static ObjectNode getReplaceNode(String path, JsonNode fromValue, JsonNode value) {
        return JsonPatchDiff.builder()
                .operation(Operation.REPLACE)
                .path(path)
                .fromValue(fromValue)
                .value(value)
                .build().toObjectNode(MAPPER);
    }

    private static ObjectNode getAddNode(String path, JsonNode addedValue) {
        return JsonPatchDiff.builder()
                .operation(Operation.ADD)
                .path(path)
                .value(addedValue)
                .build().toObjectNode(MAPPER);
    }

    private static ObjectNode getRemoveNode(String path, JsonNode removedValue) {
        return JsonPatchDiff.builder()
                .operation(Operation.REMOVE)
                .path(path)
                .value(removedValue)
                .build().toObjectNode(MAPPER);
    }

    private static SpdxDocument buildMinimalDocumentWithFile() throws InvalidSPDXAnalysisException {
        var modelStore = new InMemSpdxStore();
        var documentUri = "documentUri";
        var copyManager = new ModelCopyManager();

        var document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setCreationInfo(document.createCreationInfo(List.of("Tool: spdx-testbed"), "2022" +
                "-01-01T00:00:00Z"));
        document.setName("SPDX-test-doc");

        var sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1,
                "d6a770ba38583ed4bb4525bd96e50461655d2758");
        var concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        var file = document.createSpdxFile("SPDXRef-file", "./foo.txt", concludedLicense,
                        List.of(), "Copyright 2022 Anonymous Developer", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
