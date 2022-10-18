package org.spdx.testbed.util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.apache.commons.collections4.IteratorUtils;
import org.spdx.library.SpdxConstants;
import org.spdx.testbed.util.Difference;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility methods for comparing two Spdx documents serialized as json.
 * At the moment, most methods accept an optional second path. This is used when lists are in
 * play: Since the ordering is not relevant, the index of elements to compare (and therefore the
 * path) may differ. The null-handling is certainly not ideal and may be refactored in the future.
 */
public class JsonComparison {

    /**
     * Compares the values of two ValueNodes and returns a difference if detected.
     * Note: The precise value type does not matter, only the stringified versions are compared.
     *
     * @param secondPath optional path of the second node in the document
     */
    public static Optional<Difference> findDifference(ValueNode firstNode,
                                                      ValueNode secondNode,
                                                      String firstPath,
                                                      @Nullable String secondPath) {
        var firstValueAsString = normalizeString(firstNode.asText());
        var secondValueAsString = normalizeString(secondNode.asText());

        firstValueAsString = convertNoneUriValueToPlainString(firstValueAsString);
        secondValueAsString = convertNoneUriValueToPlainString(secondValueAsString);

        if (!Objects.equals(firstValueAsString, secondValueAsString)) {
            return Optional.of(Difference.builder()
                    .firstValue(firstNode)
                    .secondValue(secondNode)
                    .path(firstPath)
                    .secondPath(secondPath)
                    .build());
        }
        return Optional.empty();
    }

    /**
     * Compares the two provided JsonNodes and returns a list of detected differences. The
     * semantics of the comparison depend on the type of the nodes.
     *
     * @param secondPathPrefix optional path of the second node in the document
     */
    public static List<Difference> findDifferences(JsonNode firstNode,
                                                   JsonNode secondNode,
                                                   String firstPathPrefix,
                                                   @Nullable String secondPathPrefix) {
        var differences = new ArrayList<Difference>();

        if (isEquivalentToNull(firstNode) && isEquivalentToNull(secondNode)) {
            return differences;
        } else if (isEquivalentToNull(firstNode) || isEquivalentToNull(secondNode)) {
            differences.add(Difference.builder()
                    .firstValue(firstNode)
                    .secondValue(secondNode)
                    .path(firstPathPrefix)
                    .secondPath(secondPathPrefix)
                    .build());
        } else if (firstNode.isValueNode() && secondNode.isValueNode()) {
            findDifference((ValueNode) firstNode, (ValueNode) secondNode, firstPathPrefix,
                    secondPathPrefix).ifPresent(differences::add);
        } else if (firstNode instanceof ObjectNode && secondNode instanceof ObjectNode) {
            differences.addAll(findDifferences((ObjectNode) firstNode, (ObjectNode) secondNode,
                    firstPathPrefix, secondPathPrefix));
        } else if (firstNode instanceof ArrayNode && secondNode instanceof ArrayNode) {
            differences.addAll(findDifferences((ArrayNode) firstNode, (ArrayNode) secondNode,
                    firstPathPrefix, secondPathPrefix));
        } else {
            // The node types don't match and none of the nodes is equivalent to null
            // Anything smarter to do here?
            differences.add(Difference.builder()
                    .firstValue(firstNode)
                    .secondValue(secondNode)
                    .path(firstPathPrefix)
                    .secondPath(secondPathPrefix)
                    .build());
        }

        return differences;
    }

    public static List<Difference> findDifferences(ObjectNode firstNode,
                                                   ObjectNode secondNode) {
        return findDifferences(firstNode, secondNode, "", null);
    }

    /**
     * Compares the two provided ObjectNodes and returns a list of detected differences.
     *
     * @param secondPathPrefix optional path of the second node in the document
     */
    public static List<Difference> findDifferences(ObjectNode firstNode,
                                                   ObjectNode secondNode,
                                                   String firstPathPrefix,
                                                   @Nullable String secondPathPrefix) {
        var differences = new ArrayList<Difference>();

        var firstNodeFieldNames = IteratorUtils.toList(firstNode.fieldNames());
        var secondNodeFieldNames = IteratorUtils.toList(secondNode.fieldNames());
        var commonFields = firstNodeFieldNames.stream().filter(secondNodeFieldNames::contains)
                .collect(Collectors.toList());
        var firstNodeExclusiveFields = firstNodeFieldNames.stream()
                .filter(name -> !secondNodeFieldNames.contains(name)).collect(Collectors.toList());
        var secondNodeExclusiveFields = secondNodeFieldNames.stream()
                .filter(name -> !firstNodeFieldNames.contains(name)).collect(Collectors.toList());

        for (var fieldName : commonFields) {
            // Reference type may be local to the document, so we skip it.
            // Compare https://github.com/spdx/Spdx-Java-Library/blob/06ffee5e3754400a36dbb2f652d814c92e228e87/src/main/java/org/spdx/library/model/ExternalRef.java#L329
            if (fieldName.equals("referenceType")) {
                continue;
            }

            var firstValue = firstNode.get(fieldName);
            var secondValue = secondNode.get(fieldName);

            var newFirstPathPrefix = addPathComponent(firstPathPrefix, fieldName);
            var newSecondPathPrefix = secondPathPrefix == null ? null :
                    addPathComponent(secondPathPrefix, fieldName);

            differences.addAll(findDifferences(firstValue, secondValue, newFirstPathPrefix,
                    newSecondPathPrefix));
        }

        for (var fieldName : firstNodeExclusiveFields) {
            var value = firstNode.get(fieldName);
            var newFirstPathPrefix = addPathComponent(firstPathPrefix, fieldName);
            var newSecondPathPrefix = secondPathPrefix == null ? null :
                    addPathComponent(secondPathPrefix, fieldName);
            if (!isEquivalentToNull(value)) {
                differences.add(Difference.builder()
                        .firstValue(value)
                        .path(newFirstPathPrefix)
                        .secondPath(newSecondPathPrefix)
                        .build());
            }
        }

        for (var fieldName : secondNodeExclusiveFields) {
            var value = secondNode.get(fieldName);
            var newFirstPathPrefix = addPathComponent(firstPathPrefix, fieldName);
            var newSecondPathPrefix = secondPathPrefix == null ? null :
                    addPathComponent(secondPathPrefix, fieldName);
            if (!isEquivalentToNull(value)) {
                differences.add(Difference.builder()
                        .secondValue(value)
                        .path(newFirstPathPrefix)
                        .secondPath(newSecondPathPrefix)
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
            return SpdxConstants.NOASSERTION_VALUE.equals(node.asText()) || SpdxConstants.URI_VALUE_NOASSERTION.equals(node.asText());
        }
        return node.isNull();
    }

    /**
     * Compares the two provided ArrayNodes and returns a list of detected differences.
     *
     * @param secondPathPrefix optional path of the second node in the document
     */
    public static List<Difference> findDifferences(ArrayNode firstNode,
                                                   ArrayNode secondNode,
                                                   String firstPathPrefix,
                                                   @Nullable String secondPathPrefix) {
        var differences = new ArrayList<Difference>();

        // TODO: Remove this temporary workaround once hasFiles is fixed. See https://github.com/spdx/spdx-java-jackson-store/issues/42.
        //  Should be included in the next release after 1.1.1.
        if (firstPathPrefix.endsWith("hasFiles")) {
            return differences;
        }

        var firstNodeElements =
                removeIrrelevantElements(IteratorUtils.toList(firstNode.elements()));
        var secondNodeElements =
                removeIrrelevantElements(IteratorUtils.toList(secondNode.elements()));

        // These will be modified while iterating
        var remainingFirstNodeElements = new ArrayList<>(firstNodeElements);
        var remainingSecondNodeElements = new ArrayList<>(secondNodeElements);

        for (var currentFirstNodeElement : firstNodeElements) {
            var exactMatchOptional = findExactMatch(secondNodeElements, currentFirstNodeElement);

            if (exactMatchOptional.isPresent()) {
                remainingFirstNodeElements.remove(currentFirstNodeElement);
                remainingSecondNodeElements.remove(exactMatchOptional.get());
                continue;
            }

            var firstElementPath = addPathComponent(firstPathPrefix,
                    Integer.toString(firstNodeElements.indexOf(currentFirstNodeElement)));

            // Backup plan: If no exact match was found, try to find a unique match by id and 
            // compare

            var idMatches = findIdMatches(remainingSecondNodeElements,
                    currentFirstNodeElement);
            var secondListPath = secondPathPrefix == null ? firstPathPrefix : secondPathPrefix;

            if (idMatches.size() != 1) {
                var comment = idMatches.isEmpty() ? "No element in second list with a matching " +
                        "Spdx id or no Spdx id present." : "Multiple items in second list with " +
                        "the same Spdx id.";
                differences.add(Difference.builder()
                        .firstValue(currentFirstNodeElement)
                        .path(firstElementPath)
                        .secondPath(secondListPath)
                        .comment(comment)
                        .build());
                continue;
            }

            var uniqueIdMatch = idMatches.get(0);
            var secondElementIndexAsString =
                    String.valueOf(secondNodeElements.indexOf(uniqueIdMatch));
            var secondElementPath = addPathComponent(secondListPath, secondElementIndexAsString);
            remainingFirstNodeElements.remove(currentFirstNodeElement);
            remainingSecondNodeElements.remove(uniqueIdMatch);
            differences.addAll(findDifferences(currentFirstNodeElement, uniqueIdMatch,
                    firstElementPath, secondElementPath));
        }

        for (var currentSecondNodeElement : remainingSecondNodeElements) {
            // There cannot be an exact match in the first list since it would have been found in
            // the previous loop

            var secondElementIndexAsString =
                    String.valueOf(secondNodeElements.indexOf(currentSecondNodeElement));
            var secondListPath = secondPathPrefix == null ? firstPathPrefix : secondPathPrefix;
            var secondElementPath = addPathComponent(secondListPath, secondElementIndexAsString);

            var idMatches = findIdMatches(remainingFirstNodeElements,
                    currentSecondNodeElement);

            if (idMatches.size() != 1) {
                var comment = idMatches.isEmpty() ? "No element in first list with a matching " +
                        "Spdx id or no Spdx id present." : "Multiple items in first list with the" +
                        " same Spdx id.";
                differences.add(Difference.builder()
                        .secondValue(currentSecondNodeElement)
                        .secondPath(secondElementPath)
                        .path(firstPathPrefix)
                        .comment(comment)
                        .build());
                continue;
            }

            var uniqueIdMatch = idMatches.get(0);
            var firstElementIndexAsString =
                    String.valueOf(firstNodeElements.indexOf(uniqueIdMatch));
            var firstElementPath = addPathComponent(firstPathPrefix, firstElementIndexAsString);
            remainingFirstNodeElements.remove(uniqueIdMatch);
            remainingSecondNodeElements.remove(currentSecondNodeElement);
            differences.addAll(findDifferences(uniqueIdMatch, currentSecondNodeElement,
                    firstElementPath, secondElementPath));
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
