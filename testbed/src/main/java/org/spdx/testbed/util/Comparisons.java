package org.spdx.testbed.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.flipkart.zjsonpatch.DiffFlags;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.spdx.jacksonstore.JacksonSerializer;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.IndividualUriValue;
import org.spdx.library.model.ModelCollection;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SimpleUriValue;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.TypedValue;
import org.spdx.library.model.license.SpdxNoAssertionLicense;
import org.spdx.storage.IModelStore;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Comparisons {
    private static final String CLASS_KEY = "class";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static ArrayNode findDifferencesAsJsonPatch(@Nonnull ModelObject firstObject,
                                                       @Nonnull ModelObject secondObject
            , Collection<String> ignoredPaths) throws InvalidSPDXAnalysisException,
            JsonProcessingException {
        var firstJson = asJson(firstObject);
        var secondJson = asJson(secondObject);
        var differences = (ArrayNode) JsonDiff.asJson(firstJson, secondJson,
                EnumSet.of(DiffFlags.ADD_ORIGINAL_VALUE_ON_REPLACE, DiffFlags.OMIT_COPY_OPERATION));
        omitIrrelevantDifferences(differences, ignoredPaths);
        return differences;
    }

    public static ArrayNode findDifferencesAsJsonPatch(ModelObject firstObject,
                                                       ModelObject secondObject) throws JsonProcessingException, InvalidSPDXAnalysisException {
        return findDifferencesAsJsonPatch(firstObject, secondObject, Collections.emptySet());
    }

    public static ObjectNode asJson(ModelObject modelObject) throws InvalidSPDXAnalysisException {
        var serializer = new JacksonSerializer(new ObjectMapper(),
                MultiFormatStore.Format.JSON_PRETTY, MultiFormatStore.Verbose.COMPACT,
                modelObject.getModelStore());
        return serializer.docToJsonNode(modelObject.getDocumentUri());
    }

    private static void omitIrrelevantDifferences(ArrayNode differencesNode,
                                                  Collection<String> ignoredPaths) throws JsonProcessingException {
        List<Integer> indicesToRemove = new ArrayList<>();
        var nodeIterator = differencesNode.iterator();
        var currentIndex = -1;
        while (nodeIterator.hasNext()) {
            currentIndex++;
            var currentDiff = OBJECT_MAPPER.treeToValue(nodeIterator.next(), JsonPatchDiff.class);
            if (isMoveOperationWithSameBasePath(currentDiff) ||
                    shouldBeIgnored(currentDiff.getPath(), ignoredPaths) ||
                    isAddOrRemoveWithEmptyArray(currentDiff) ||
                    isNoAssertionVsNull(currentDiff)) {
                // list is created in reverse order so the subsequent remove operations are 
                // executed from largest to smallest index and don't interfere with each other
                indicesToRemove.add(0, currentIndex);
            }
        }
        indicesToRemove.forEach(differencesNode::remove);
    }

    private static boolean shouldBeIgnored(String path, Collection<String> ignoredPaths) {
        return ignoredPaths.stream()
                .anyMatch(path::startsWith);
    }

    private static boolean isMoveOperationWithSameBasePath(JsonPatchDiff jsonPatchDiff) {
        if (!Operation.MOVE.equals(jsonPatchDiff.getOperation())) {
            return false;
        }
        return describeElementsOfSameList(jsonPatchDiff.getFrom(), jsonPatchDiff.getPath());
    }

    private static boolean isAddOrRemoveWithEmptyArray(JsonPatchDiff jsonPatchDiff) {
        var relevantOperations = EnumSet.of(Operation.ADD, Operation.REMOVE);
        if (!relevantOperations.contains(jsonPatchDiff.getOperation())) {
            return false;
        }
        return jsonPatchDiff.getValue() == null && isEmptyArrayNode(jsonPatchDiff.getFromValue()) ||
                isEmptyArrayNode(jsonPatchDiff.getValue()) && jsonPatchDiff.getFromValue() == null;
    }

    private static boolean isEmptyArrayNode(JsonNode jsonNode) {
        return jsonNode instanceof ArrayNode && jsonNode.isEmpty();
    }

    private static boolean isNoAssertionVsNull(JsonPatchDiff jsonPatchDiff) {
        var relevantOperations = EnumSet.of(Operation.ADD, Operation.REMOVE);
        if (!relevantOperations.contains(jsonPatchDiff.getOperation())) {
            return false;
        }
        return jsonPatchDiff.getValue() == null && isNoAssertion(jsonPatchDiff.getFromValue()) ||
                isNoAssertion(jsonPatchDiff.getValue()) && jsonPatchDiff.getFromValue() == null;
    }

    private static boolean isNoAssertion(JsonNode jsonNode) {
        return jsonNode instanceof TextNode && jsonNode.asText()
                .equals(SpdxConstants.NOASSERTION_VALUE);
    }

    private static boolean describeElementsOfSameList(String firstPath, String secondPath) {
        if (!describesListElement(firstPath) || !describesListElement(secondPath)) {
            return false;
        }
        // Check that paths agree up to the index in the list
        return firstPath.substring(0, firstPath.length() - 1)
                .equals(secondPath.substring(0, secondPath.length() - 1));
    }

    private static boolean describesListElement(String path) {
        // Check that path ends with "/x", where x is any nonnegative number
        var regex = Pattern.compile(".*/[0-9]+$");
        return regex.matcher(path).matches();
    }

    public static Map<String, Tuple<?>> findDifferences(ModelObject firstObject,
                                                        ModelObject secondObject,
                                                        boolean ignoreRelatedElements,
                                                        Collection<String> ignoreProperties) throws InvalidSPDXAnalysisException {
        var differences = findDifferences(firstObject, secondObject, ignoreRelatedElements);
        ignoreProperties.forEach(differences::remove);
        return differences;
    }

    public static Map<String, Tuple<?>> findDifferences(ModelObject firstObject,
                                                        ModelObject secondObject,
                                                        boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
        var differences = new HashMap<String, Tuple<?>>();

        var classDifferenceOptional = detectClassDifference(firstObject, secondObject);
        if (classDifferenceOptional.isPresent()) {
            differences.put(CLASS_KEY, classDifferenceOptional.get());
            return differences;
        }

        // TODO: It should be okay to filter out properties equivalent to null at this point. 
        //  That might simplify logic later on.
        var firstObjectProperties = ignoreRelatedElements ?
                getNonEmptyPropertiesExceptRelatedElement(firstObject) :
                getNonEmptyProperties(firstObject);
        var secondObjectProperties = ignoreRelatedElements ?
                getNonEmptyPropertiesExceptRelatedElement(secondObject) :
                getNonEmptyProperties(secondObject);

        var commonProperties = Sets.intersection(firstObjectProperties, secondObjectProperties);
        var firstObjectExclusiveProperties = Sets.difference(firstObjectProperties,
                secondObjectProperties);
        var secondObjectExclusiveProperties = Sets.difference(secondObjectProperties,
                firstObjectProperties);

        for (var property : commonProperties) {
            var firstValueOptional = getObjectPropertyValue(firstObject, property);
            var secondValueOptional = getObjectPropertyValue(secondObject, property);

            if (firstValueOptional.isEmpty() && secondValueOptional.isEmpty()) {
                continue;
            } else if (firstValueOptional.isEmpty()) {
                if (!isEquivalentToNull(secondValueOptional.get())) {
                    differences.put(property, new Tuple<>(null, secondValueOptional.get()));
                }
                continue;
            } else if (secondValueOptional.isEmpty()) {
                if (!isEquivalentToNull(firstValueOptional.get())) {
                    differences.put(property, new Tuple<>(firstValueOptional.get(), null));
                }
                continue;
            }

            var firstValue = firstValueOptional.get();
            var secondValue = secondValueOptional.get();

            // TODO: So far, this will only track differences on the top level of properties. To 
            //  give more detailed information, we will have to pass the differences map to the 
            //  propertyValuesEquivalent function at this stage, or return map that we can embed 
            //  as a submap
            if (!propertyValuesEquivalent(property, firstValue, secondValue,
                    ignoreRelatedElements)) {
                differences.put(property, new Tuple<>(firstValue, secondValue));
            }
        }

        var firstObjectExclusivePropertyDifferences = getPropertiesNotEquivalentToNull(firstObject
                , firstObjectExclusiveProperties)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new Tuple<>(entry.getValue(), null)));
        differences.putAll(firstObjectExclusivePropertyDifferences);
        var secondObjectExclusivePropertyDifferences = getPropertiesNotEquivalentToNull(secondObject
                , secondObjectExclusiveProperties)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new Tuple<>(null, entry.getValue())));
        differences.putAll(secondObjectExclusivePropertyDifferences);

        return differences;
    }

    private static Map<String, Object> getPropertiesNotEquivalentToNull(ModelObject modelObject,
                                                                        Set<String> propertyNames) throws InvalidSPDXAnalysisException {
        var propertyMap = new HashMap<String, Object>();
        for (var propertyName : propertyNames) {
            var valueOptional = getObjectPropertyValue(modelObject, propertyName);
            if (valueOptional.isEmpty()) {
                continue;
            }
            var value = valueOptional.get();
            if (isEquivalentToNull(value)) {
                continue;
            }
            propertyMap.put(propertyName, value);
        }
        return propertyMap;
    }

    private static boolean propertyValuesEquivalent(String propertyName, Object firstValue,
                                                    Object secondValue,
                                                    boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
        if (firstValue instanceof ModelCollection && secondValue instanceof ModelCollection) {
            List<?> firstList = ((ModelCollection<?>) firstValue).toImmutableList();
            List<?> secondList = ((ModelCollection<?>) secondValue).toImmutableList();
            return areEquivalent(firstList, secondList, ignoreRelatedElements);
        } else if (firstValue instanceof List && secondValue instanceof List) {
            return areEquivalent((List<?>) firstValue, (List<?>) secondValue,
                    ignoreRelatedElements);
        } else if (firstValue instanceof IndividualUriValue && secondValue instanceof IndividualUriValue) {
            return Objects.equals(((IndividualUriValue) firstValue).getIndividualURI(),
                    ((IndividualUriValue) secondValue).getIndividualURI());
            // Note: we must check the IndividualValue before the ModelObject types since the 
            // IndividualValue takes precedence
        } else if (firstValue instanceof ModelObject && secondValue instanceof ModelObject) {
            // TODO: Recursion happens here, do we have to do anything special? 
            // TODO: The ignoreRelatedElements part is taken from the library, but doesn't seem 
            //  to make sense
            return ((ModelObject) firstValue).equivalent(((ModelObject) secondValue),
                    SpdxConstants.PROP_RELATED_SPDX_ELEMENT.equals(propertyName) || ignoreRelatedElements);
            // Present, not a list, and not a TypedValue
        } else {
            return areEquivalentValues(firstValue, secondValue);
        }
    }

    private static boolean areEquivalentValues(Object valueA, Object valueB) {
        if (Objects.equals(valueA, valueB)) {
            return true;
        }
        if (valueA instanceof IndividualUriValue && valueB instanceof String) {
            return areEquivalent((IndividualUriValue) valueA, (String) valueB);
        }
        if (valueB instanceof IndividualUriValue && valueA instanceof String) {
            return areEquivalent((IndividualUriValue) valueB, (String) valueA);
        }
        if (valueA instanceof String && valueB instanceof String) {
            return normalizeString((String) valueA).equals(normalizeString((String) valueB));
        }
        return false;
    }

    private static boolean areEquivalent(IndividualUriValue uriValue, String string) {
        String individualURI = uriValue.getIndividualURI();
        return SpdxConstants.URI_VALUE_NONE.equals(individualURI) && SpdxConstants.NONE_VALUE.equals(string)
                || SpdxConstants.URI_VALUE_NOASSERTION.equals(individualURI) && SpdxConstants.NOASSERTION_VALUE.equals(string);
    }

    private static String normalizeString(String s) {
        return s.replaceAll("\r\n", "\n").trim();
    }


    private static boolean areEquivalent(List<?> firstList, List<?> secondList,
                                         boolean ignoreRelatedElements) throws InvalidSPDXAnalysisException {
        if (firstList.size() != secondList.size()) {
            return false;
        }
        for (Object item : firstList) {
            if (!containsEquivalentItem(secondList, item)) {
                return false;
            }
        }
        for (Object item : secondList) {
            if (!containsEquivalentItem(firstList, item)) {
                return false;
            }
        }
        return true;
    }

    private static boolean containsEquivalentItem(List<?> list, Object itemToFind) throws InvalidSPDXAnalysisException {
        if (list.contains(itemToFind)) {
            return true;
        } else if (itemToFind instanceof IndividualUriValue && list.contains(new SimpleUriValue((IndividualUriValue) itemToFind))) {
            // TODO: This condition doesn't seem to make sense, see https://github.com/spdx/Spdx-Java-Library/issues/108
            return true;
        }

        if (!(itemToFind instanceof ModelObject)) {
            return false;
        }

        var objectToFind = (ModelObject) itemToFind;
        // TODO: Introduce custom CompareException to use upstream
        ThrowingFunction<ModelObject, Boolean> throwingEquivalenceChecker =
                (otherObject) -> otherObject.equivalent(objectToFind);
        return list.stream()
                .filter(otherItem -> otherItem instanceof ModelObject)
                .map(otherItem -> (ModelObject) otherItem)
                .anyMatch(throwingEquivalenceChecker::apply);
    }

    private static Optional<Object> getObjectPropertyValue(ModelObject modelObject,
                                                           String propertyName) throws InvalidSPDXAnalysisException {
        Optional<Object> valueOptional = getObjectPropertyValue(modelObject.getModelStore(),
                modelObject.getDocumentUri(), modelObject.getId(), propertyName,
                modelObject.getCopyManager());
        if (valueOptional.isPresent() && valueOptional.get() instanceof ModelObject && !modelObject.isStrict()) {
            ((ModelObject) valueOptional.get()).setStrict(false);
        }
        return valueOptional;
    }

    private static Optional<Object> getObjectPropertyValue(IModelStore modelStore,
                                                           String documentUri,
                                                           String objectId, String propertyName,
                                                           ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
        if (!modelStore.exists(documentUri, objectId)) {
            return Optional.empty();
        } else if (modelStore.isCollectionProperty(documentUri, objectId, propertyName)) {
            return Optional.of(new ModelCollection<>(modelStore, documentUri, objectId,
                    propertyName, copyManager, null));
        } else {
            var valueOptional = modelStore.getValue(documentUri, objectId, propertyName);
            // TODO: Introduce custom CompareException to use upstream
            ThrowingFunction<Object, Object> throwingObjectRetrievingFunction =
                    (value) -> storedObjectToModelObject(value, documentUri, modelStore,
                            copyManager);
            return valueOptional.map(throwingObjectRetrievingFunction);
        }
    }

    private static Object storedObjectToModelObject(Object value, String documentUri,
                                                    IModelStore modelStore,
                                                    ModelCopyManager copyManager) throws InvalidSPDXAnalysisException {
        if (value instanceof IndividualUriValue) {
            return new SimpleUriValue((IndividualUriValue) value).toModelObject(modelStore,
                    documentUri, copyManager);
        } else if (value instanceof TypedValue) {
            TypedValue tv = (TypedValue) value;
            return SpdxModelFactory.createModelObject(modelStore, documentUri,
                    tv.getId(), tv.getType(), copyManager);
        } else {
            // TODO: Is this required? Does it make sense?
            return value;
        }
    }

    private static boolean isEquivalentToNull(Object propertyValue) {
        if (propertyValue instanceof ModelCollection) {
            return ((ModelCollection<?>) propertyValue).size() == 0;
        } else {
            return isNoAssertion(propertyValue);
        }
    }

    private static boolean isNoAssertion(Object propertyValue) {
        return propertyValue instanceof SpdxNoAssertionLicense ||
                propertyValue.equals(SpdxConstants.NOASSERTION_VALUE);
    }

    private static Optional<Tuple<Class<?>>> detectClassDifference(Object firstObject,
                                                                   Object secondObject) {
        if (firstObject == null && secondObject == null) {
            return Optional.empty();
        } else if (firstObject == null) {
            return Optional.of(new Tuple<>(null, secondObject.getClass()));
        } else if (secondObject == null) {
            return Optional.of(new Tuple<>(firstObject.getClass(), null));
        } else if (!firstObject.getClass().equals(secondObject.getClass())) {
            return Optional.of(new Tuple<>(firstObject.getClass(), secondObject.getClass()));
        }
        return Optional.empty();
    }

    private static Set<String> getNonEmptyProperties(ModelObject object) throws InvalidSPDXAnalysisException {
        return new HashSet<>(object.getModelStore()
                .getPropertyValueNames(object.getDocumentUri(), object.getId()));
    }

    private static Set<String> getNonEmptyPropertiesExceptRelatedElement(ModelObject object) throws InvalidSPDXAnalysisException {
        var nonEmptyPropertiesWithoutRelatedElement = getNonEmptyProperties(object);
        nonEmptyPropertiesWithoutRelatedElement.remove(SpdxConstants.PROP_RELATED_SPDX_ELEMENT);
        return nonEmptyPropertiesWithoutRelatedElement;
    }

    /**
     * Rethrows all checked exceptions as runtime exception. Useful when using a function that
     * throws a checked exception in a lambda (but don't forget to handle the exception if
     * relevant!).
     *
     * @param <T> input type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R> extends Function<T, R> {
        @Override
        default R apply(T t) {
            try {
                return throwingApply(t);
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }

        R throwingApply(T t) throws Throwable;
    }

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static class Tuple<T> {
        T first;
        T second;
    }
}
