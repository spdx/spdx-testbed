// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.collections4.IteratorUtils;
import org.spdx.library.SpdxConstants;

/**
 * Utility methods for comparing two Spdx documents serialized as json. At the moment, most methods
 * accept an optional second path. This is used when lists are in play: Since the ordering is not
 * relevant, the index of elements to compare (and therefore the path) may differ. The null-handling
 * is certainly not ideal and may be refactored in the future.
 */
public class JsonComparison {

  /**
   * Compares the values of two ValueNodes and returns a difference if detected. Note: The precise
   * value type does not matter, only the stringified versions are compared.
   *
   * @param referencePath optional path of the reference node in the document
   */
  public static Optional<Difference> findDifference(ValueNode actualNode,
      ValueNode expectedNode,
      String path,
      @Nullable String referencePath) {
    var actualValueAsString = normalizeString(actualNode.asText());
    var expectedValueAsString = normalizeString(expectedNode.asText());

    actualValueAsString = convertNoneUriValueToPlainString(actualValueAsString);
    expectedValueAsString = convertNoneUriValueToPlainString(expectedValueAsString);

    if (!Objects.equals(actualValueAsString, expectedValueAsString)) {
      return Optional.of(Difference.builder()
          .actualValue(actualNode)
          .expectedValue(expectedNode)
          .path(path)
          .pathInReferenceDoc(referencePath)
          .build());
    }
    return Optional.empty();
  }

  /**
   * Compares the two provided JsonNodes and returns a list of detected differences. The semantics
   * of the comparison depend on the type of the nodes.
   *
   * @param referencePathPrefix optional path of the reference node in the document
   */
  public static List<Difference> findDifferences(JsonNode actualNode,
      JsonNode expectedNode,
      String pathPrefix,
      @Nullable String referencePathPrefix) {
    var differences = new ArrayList<Difference>();

    if (isEquivalentToNull(actualNode) && isEquivalentToNull(expectedNode)) {
      return differences;
    } else if (isEquivalentToNull(actualNode) || isEquivalentToNull(expectedNode)) {
      differences.add(Difference.builder()
          .actualValue(actualNode)
          .expectedValue(expectedNode)
          .path(pathPrefix)
          .pathInReferenceDoc(referencePathPrefix)
          .build());
    } else if (actualNode.isValueNode() && expectedNode.isValueNode()) {
      findDifference((ValueNode) actualNode, (ValueNode) expectedNode, pathPrefix,
          referencePathPrefix).ifPresent(differences::add);
    } else if (actualNode instanceof ObjectNode && expectedNode instanceof ObjectNode) {
      differences.addAll(findDifferences((ObjectNode) actualNode, (ObjectNode) expectedNode,
          pathPrefix, referencePathPrefix));
    } else if (actualNode instanceof ArrayNode && expectedNode instanceof ArrayNode) {
      differences.addAll(findDifferences((ArrayNode) actualNode, (ArrayNode) expectedNode,
          pathPrefix, referencePathPrefix));
    } else {
      // The node types don't match and none of the nodes is equivalent to null
      // Anything smarter to do here?
      differences.add(Difference.builder()
          .actualValue(actualNode)
          .expectedValue(expectedNode)
          .path(pathPrefix)
          .pathInReferenceDoc(referencePathPrefix)
          .build());
    }

    return differences;
  }

  public static List<Difference> findDifferences(ObjectNode actualNode,
      ObjectNode expectedNode) {
    return findDifferences(actualNode, expectedNode, "", null);
  }

  /**
   * Compares the two provided ObjectNodes and returns a list of detected differences.
   *
   * @param referencePathPrefix optional path of the reference node in the document
   */
  public static List<Difference> findDifferences(ObjectNode actualNode,
      ObjectNode expectedNode,
      String pathPrefix,
      @Nullable String referencePathPrefix) {
    var differences = new ArrayList<Difference>();

    var actualNodeFieldNames = IteratorUtils.toList(actualNode.fieldNames());
    var expectedNodeFieldNames = IteratorUtils.toList(expectedNode.fieldNames());
    var commonFields = actualNodeFieldNames.stream().filter(expectedNodeFieldNames::contains)
        .collect(Collectors.toList());
    var actualNodeExclusiveFields = actualNodeFieldNames.stream()
        .filter(name -> !expectedNodeFieldNames.contains(name))
        .collect(Collectors.toList());
    var expectedNodeExclusiveFields = expectedNodeFieldNames.stream()
        .filter(name -> !actualNodeFieldNames.contains(name)).collect(Collectors.toList());

    for (var fieldName : commonFields) {
      // Reference type may be local to the document, so we skip it.
      // Compare https://github.com/spdx/Spdx-Java-Library/blob/06ffee5e3754400a36dbb2f652d814c92e228e87/src/main/java/org/spdx/library/model/ExternalRef.java#L329
      if (fieldName.equals("referenceType")) {
        continue;
      }

      var actualValue = actualNode.get(fieldName);
      var expectedValue = expectedNode.get(fieldName);

      var newPathPrefix = addPathComponent(pathPrefix, fieldName);
      var newReferencePathPrefix = referencePathPrefix == null ? null :
          addPathComponent(referencePathPrefix, fieldName);

      differences.addAll(findDifferences(actualValue, expectedValue, newPathPrefix,
          newReferencePathPrefix));
    }

    for (var fieldName : actualNodeExclusiveFields) {
      var value = actualNode.get(fieldName);
      var newPathPrefix = addPathComponent(pathPrefix, fieldName);
      var newReferencePathPrefix = referencePathPrefix == null ? null :
          addPathComponent(referencePathPrefix, fieldName);
      if (!isEquivalentToNull(value)) {
        differences.add(Difference.builder()
            .actualValue(value)
            .path(newPathPrefix)
            .pathInReferenceDoc(newReferencePathPrefix)
            .build());
      }
    }

    for (var fieldName : expectedNodeExclusiveFields) {
      var value = expectedNode.get(fieldName);
      var newPathPrefix = addPathComponent(pathPrefix, fieldName);
      var newReferencePathPrefix = referencePathPrefix == null ? null :
          addPathComponent(referencePathPrefix, fieldName);
      if (!isEquivalentToNull(value)) {
        differences.add(Difference.builder()
            .expectedValue(value)
            .path(newPathPrefix)
            .pathInReferenceDoc(newReferencePathPrefix)
            .build());
      }
    }

    return differences;
  }

  private static String addPathComponent(@Nullable String currentPath, String newComponent) {
    return currentPath + "/" + newComponent;
  }

  private static boolean isEquivalentToNull(JsonNode node) {
    // TODO: there may be edge cases here, like an array or object that only contains 
    //  NOASSERTIONS. Not sure whether such cases would be relevant
    if (node.isArray() || node.isObject()) {
      return node.isEmpty();
    }
    if (node.isMissingNode()) {
      return true;
    }
    if (node.isValueNode()) {
      return SpdxConstants.NOASSERTION_VALUE.equals(node.asText())
          || SpdxConstants.URI_VALUE_NOASSERTION.equals(node.asText());
    }
    return node.isNull();
  }

  /**
   * Compares the two provided ArrayNodes and returns a list of detected differences.
   *
   * @param referencePathPrefix optional path of the reference node in the document
   */
  public static List<Difference> findDifferences(ArrayNode actualNode,
      ArrayNode expectedNode,
      String pathPrefix,
      @Nullable String referencePathPrefix) {
    var differences = new ArrayList<Difference>();

    // TODO: Remove this temporary workaround once hasFiles is fixed. See https://github.com/spdx/spdx-java-jackson-store/issues/42.
    //  Should be included in the next release after 1.1.1.
    if (pathPrefix.endsWith("hasFiles")) {
      return differences;
    }

    var actualNodeElements =
        removeIrrelevantElements(IteratorUtils.toList(actualNode.elements()));
    var expectedNodeElements =
        removeIrrelevantElements(IteratorUtils.toList(expectedNode.elements()));

    // These will be modified while iterating
    var remainingActualNodeElements = new ArrayList<>(actualNodeElements);
    var remainingExpectedNodeElements = new ArrayList<>(expectedNodeElements);

    for (var currentActualNodeElement : actualNodeElements) {
      var exactMatchOptional = findExactMatch(expectedNodeElements, currentActualNodeElement);

      if (exactMatchOptional.isPresent()) {
        remainingActualNodeElements.remove(currentActualNodeElement);
        remainingExpectedNodeElements.remove(exactMatchOptional.get());
        continue;
      }

      var actualElementPath = addPathComponent(pathPrefix,
          Integer.toString(actualNodeElements.indexOf(currentActualNodeElement)));

      // Backup plan: If no exact match was found, try to find a unique match by id and 
      // compare

      var idMatches = findIdMatches(remainingExpectedNodeElements,
          currentActualNodeElement);
      var expectedListPath = referencePathPrefix == null ? pathPrefix : referencePathPrefix;

      if (idMatches.size() != 1) {
        var comment = idMatches.isEmpty() ? "No element in expected list with a matching " +
            "Spdx id or no Spdx id present." : "Multiple items in expected list with " +
            "the same Spdx id.";
        differences.add(Difference.builder()
            .actualValue(currentActualNodeElement)
            .path(actualElementPath)
            .pathInReferenceDoc(expectedListPath)
            .comment(comment)
            .build());
        continue;
      }

      var uniqueIdMatch = idMatches.get(0);
      var expectedElementIndexAsString =
          String.valueOf(expectedNodeElements.indexOf(uniqueIdMatch));
      var expectedElementPath = addPathComponent(expectedListPath,
          expectedElementIndexAsString);
      remainingActualNodeElements.remove(currentActualNodeElement);
      remainingExpectedNodeElements.remove(uniqueIdMatch);
      differences.addAll(findDifferences(currentActualNodeElement, uniqueIdMatch,
          actualElementPath, expectedElementPath));
    }

    for (var currentExpectedNodeElement : remainingExpectedNodeElements) {
      // There cannot be an exact match in the first list since it would have been found in
      // the previous loop

      var expectedElementIndexAsString =
          String.valueOf(expectedNodeElements.indexOf(currentExpectedNodeElement));
      var expectedListPath = referencePathPrefix == null ? pathPrefix : referencePathPrefix;
      var expectedElementPath = addPathComponent(expectedListPath,
          expectedElementIndexAsString);

      var idMatches = findIdMatches(remainingActualNodeElements,
          currentExpectedNodeElement);

      if (idMatches.size() != 1) {
        var comment = idMatches.isEmpty() ? "No element in actual list with a matching " +
            "Spdx id or no Spdx id present." : "Multiple items in actual list with " +
            "the" +
            " same Spdx id.";
        differences.add(Difference.builder()
            .expectedValue(currentExpectedNodeElement)
            .pathInReferenceDoc(expectedElementPath)
            .path(pathPrefix)
            .comment(comment)
            .build());
        continue;
      }

      var uniqueIdMatch = idMatches.get(0);
      var actualElementIndexAsString =
          String.valueOf(actualNodeElements.indexOf(uniqueIdMatch));
      var actualElementPath = addPathComponent(pathPrefix, actualElementIndexAsString);
      remainingActualNodeElements.remove(uniqueIdMatch);
      remainingExpectedNodeElements.remove(currentExpectedNodeElement);
      differences.addAll(findDifferences(uniqueIdMatch, currentExpectedNodeElement,
          actualElementPath, expectedElementPath));
    }

    return differences;
  }

  private static Optional<JsonNode> findExactMatch(List<JsonNode> elementsToCheck,
      JsonNode elementToFind) {
    return elementsToCheck.stream()
        .filter(loopElement -> findDifferences(loopElement, elementToFind, "", null).isEmpty())
        .findFirst();
  }

  private static List<JsonNode> findIdMatches(ArrayList<JsonNode> elementsToCheck,
      JsonNode elementToMatch) {
    return elementsToCheck.stream()
        .filter(jsonNode -> jsonNode.has(SpdxConstants.SPDX_IDENTIFIER))
        .filter(jsonNode -> jsonNode.get(SpdxConstants.SPDX_IDENTIFIER)
            .equals(elementToMatch.get(SpdxConstants.SPDX_IDENTIFIER)))
        .collect(Collectors.toList());
  }

  private static List<JsonNode> removeIrrelevantElements(List<JsonNode> list) {
    return list.stream()
        .filter(difference -> !isEquivalentToNull(difference))
        .collect(Collectors.toList());
  }

  private static String normalizeString(String s) {
    return s.replaceAll("\r\n", "\n").trim();
  }

  // None values are treated the same, regardless of whether it's the uri form or the simple 
  // form. We convert all to the simple form to make comparison easier.
  // Note that noassertion values (of both uri and plain string type) are treated like null 
  // anyway, so they don't require conversion.
  private static String convertNoneUriValueToPlainString(String input) {
    if (SpdxConstants.URI_VALUE_NONE.equals(input)) {
      return SpdxConstants.NONE_VALUE;
    }
    return input;
  }
}
