// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.util;


import static org.assertj.core.api.Assertions.assertThat;
import static org.spdx.testbed.util.Comparisons.findDifferencesInSerializedJson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.storage.IModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.testbed.util.json.Difference;

/**
 * Tests for methods comparing SPDX documents.
 */
public class ComparisonsTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private IModelStore modelStore;

  @BeforeEach
  public void setup() {
    modelStore = new InMemSpdxStore();
  }

  @Test
  public void detectRootLevelDifference() throws InvalidSPDXAnalysisException {
    var minimalDocument = buildMinimalDocumentWithFile();
    var secondDocument = buildMinimalDocumentWithFile();
    secondDocument.setName("newName");
    var expectedNameDifference = Difference.builder()
        .actualValue(new TextNode(minimalDocument.getName().get()))
        .expectedValue(new TextNode(secondDocument.getName().get()))
        .path("/name")
        .build();

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

    var firstExpectedDifference = Difference.builder()
        .actualValue(new TextNode("fileContributor"))
        .path("/files/0/fileContributors/0")
        .pathInReferenceDoc("/files/0/fileContributors")
        .comment("No element in expected list with a matching Spdx id or no Spdx id present.")
        .build();
    var secondExpectedDifference = Difference.builder()
        .expectedValue(new TextNode("newContributor"))
        .pathInReferenceDoc("/files/0/fileContributors/0")
        .path("/files/0/fileContributors")
        .comment("No element in actual list with a matching Spdx id or no Spdx id present.")
        .build();

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
    assertThat(difference.getActualValue()).isEqualTo(new TextNode("./foo.txt"));
    assertThat(difference.getExpectedValue()).isEqualTo(new TextNode("./bar.txt"));
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

    var expectedDifference = Difference.builder()
        .actualValue(new TextNode("firstComment"))
        .expectedValue(new TextNode("secondComment"))
        .path("/" + SpdxConstants.PROP_SPDX_CREATION_INFO + "/comment")
        .build();

    var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

    assertThat(differences).containsExactly(expectedDifference);
  }

  @Test
  public void detectAdditionalProperty() throws InvalidSPDXAnalysisException {
    final var firstDoc = buildMinimalDocumentWithFile();
    final var secondDoc = buildMinimalDocumentWithFile();
    var annotationComment = "Completely new annotation!";
    var annotation = new Annotation("annotationId").setComment(annotationComment);
    firstDoc.addAnnotation(annotation);

    var expectedAnnotationsNode = MAPPER.createArrayNode();
    var annotationNode = MAPPER.createObjectNode();
    annotationNode.put("comment", annotationComment);
    expectedAnnotationsNode.add(annotationNode);

    var expectedDifference = Difference.builder()
        .actualValue(expectedAnnotationsNode)
        .path("/annotations")
        .build();

    var differences = findDifferencesInSerializedJson(firstDoc, secondDoc);

    assertThat(differences).containsExactly(expectedDifference);
  }

  @Test
  public void detectMissingProperty() throws InvalidSPDXAnalysisException {
    final var firstDoc = buildMinimalDocumentWithFile();
    final var secondDoc = buildMinimalDocumentWithFile();
    var annotationComment = "Completely new annotation!";
    var annotation = new Annotation("annotationId").setComment(annotationComment);
    secondDoc.addAnnotation(annotation);

    var expectedAnnotationsNode = MAPPER.createArrayNode();
    var annotationNode = MAPPER.createObjectNode();
    annotationNode.put("comment", annotationComment);
    expectedAnnotationsNode.add(annotationNode);
    var expectedDifference = Difference.builder()
        .expectedValue(expectedAnnotationsNode)
        .path("/annotations")
        .build();

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

  private static SpdxDocument buildMinimalDocumentWithFile() throws InvalidSPDXAnalysisException {
    var modelStore = new InMemSpdxStore();
    var documentUri = "documentUri";
    var copyManager = new ModelCopyManager();

    var document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

    document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
    document.setCreationInfo(
        document.createCreationInfo(List.of("Tool: spdx-testbed"), "2022-01-01T00:00:00Z"));
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
