package org.spdx.testbed.util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.apache.commons.collections4.IteratorUtils;
import org.spdx.library.SpdxConstants;
import org.spdx.testbed.util.Difference;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonComparison {

    public static Optional<Difference> findDifference(@Nonnull ValueNode firstNode,
                                                      @Nonnull ValueNode secondNode, String path) {
        var firstValueAsString = firstNode.asText();
        var secondValueAsString = secondNode.asText();
        if (!Objects.equals(firstValueAsString, secondValueAsString)) {
            return Optional.of(new Difference(firstNode, secondNode, path, ""));
        }
        return Optional.empty();
    }

    public static List<Difference> findDifferences(@Nonnull JsonNode firstNode,
                                                   @Nonnull JsonNode secondNode,
                                                   String pathPrefix) {
        var differences = new ArrayList<Difference>();

        if (isEquivalentToNull(firstNode) && isEquivalentToNull(secondNode)) {
            return differences;
        } else if (isEquivalentToNull(firstNode) || isEquivalentToNull(secondNode)) {
            differences.add(new Difference(firstNode, secondNode, pathPrefix, ""));
        } else if (firstNode.isValueNode() && secondNode.isValueNode()) {
            findDifference((ValueNode) firstNode, (ValueNode) secondNode, pathPrefix).ifPresent(differences::add);
        } else if (firstNode instanceof ObjectNode && secondNode instanceof ObjectNode) {
            differences.addAll(findDifferences((ObjectNode) firstNode, (ObjectNode) secondNode,
                    pathPrefix));
        } else if (firstNode instanceof ArrayNode && secondNode instanceof ArrayNode) {
            differences.addAll(findDifferences((ArrayNode) firstNode, (ArrayNode) secondNode,
                    pathPrefix));
        } else {
            // The node types don't match and none of the nodes is equivalent to null
            // Anything smarter to do here?
            differences.add(new Difference(firstNode, secondNode, pathPrefix, ""));
        }

        return differences;
    }

    public static List<Difference> findDifferences(@Nonnull ObjectNode firstNode,
                                                   @Nonnull ObjectNode secondNode) {
        return findDifferences(firstNode, secondNode, "");
    }

    public static List<Difference> findDifferences(@Nonnull ObjectNode firstNode,
                                                   @Nonnull ObjectNode secondNode,
                                                   String pathPrefix) {
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

            // TODO: reference type looks different in different formats, so for now we ignore 
            //  it. See https://github.com/spdx/tools-java/issues/80 
            if (fieldName.equals("referenceType")) {
                continue;
            }

            var firstValue = firstNode.get(fieldName);
            var secondValue = secondNode.get(fieldName);

            var newPathPrefix = pathPrefix + "/" + fieldName;

            differences.addAll(findDifferences(firstValue, secondValue, newPathPrefix));
        }

        for (var fieldName : firstNodeExclusiveFields) {
            var value = firstNode.get(fieldName);
            var newPathPrefix = pathPrefix + "/" + fieldName;
            if (!isEquivalentToNull(value)) {
                differences.add(new Difference(value, null, newPathPrefix, ""));
            }
        }

        for (var fieldName : secondNodeExclusiveFields) {
            var value = secondNode.get(fieldName);
            var newPathPrefix = pathPrefix + "/" + fieldName;
            if (!isEquivalentToNull(value)) {
                differences.add(new Difference(null, value, newPathPrefix, ""));
            }
        }

        return differences;
    }

    public static boolean isEquivalentToNull(JsonNode node) {
        // TODO: there may be edge cases here, like an array or object that only contains 
        //  NOASSERTIONS. Not sure whether such cases would be relevant
        if (node.isArray() || node.isObject()) {
            return node.isEmpty();
        }
        if (node.isMissingNode()) {
            return true;
        }
        if (node.isValueNode()) {
            return SpdxConstants.NOASSERTION_VALUE.equals(node.asText());
        }
        return node.isNull();
    }

    // TODO: One could consider different matching strategies here, like matching by id. But that
    //  would come with its range of exceptions (like anonymous ids, or duplicate ids), and it's 
    //  unclear whether it would be really helpful in many situations.
    public static List<Difference> findDifferences(@Nonnull ArrayNode firstNode,
                                                   @Nonnull ArrayNode secondNode,
                                                   String pathPrefix) {
        var differences = new ArrayList<Difference>();

        // TODO: Remove this temporary workaround once hasFiles is fixed. See https://github.com/spdx/spdx-java-jackson-store/issues/42
        if (pathPrefix.endsWith("hasFiles")) {
            return differences;
        }

        var firstNodeElements =
                removeIrrelevantElements(IteratorUtils.toList(firstNode.elements()));
        var secondNodeElements =
                removeIrrelevantElements(IteratorUtils.toList(secondNode.elements()));

        for (var element : firstNodeElements) {
            var matchingElementOptional = secondNodeElements.stream()
                    .filter(loopElement -> findDifferences(loopElement, element, pathPrefix).isEmpty())
                    .findFirst();

            // TODO: Improve Difference class to make the information more precise
            matchingElementOptional.ifPresentOrElse(secondNodeElements::remove,
                    () -> differences.add(new Difference(element, null,
                            pathPrefix + "/" + firstNodeElements.indexOf(element), "Could not " +
                            "find a matching element in the second list")));
        }

        // Elements remaining in the second list were not matched
        for (var element : secondNodeElements) {
            // TODO: The index is wrong since some elements were removed earlier
            differences.add(new Difference(null, element,
                    pathPrefix + "/" + secondNodeElements.indexOf(element), "Could not find a " +
                    "matching element in the first list"));
        }

        return differences;
    }

    private static List<JsonNode> removeIrrelevantElements(List<JsonNode> list) {
        return list.stream()
                .filter(difference -> !isEquivalentToNull(difference))
                .collect(Collectors.toList());
    }
}
