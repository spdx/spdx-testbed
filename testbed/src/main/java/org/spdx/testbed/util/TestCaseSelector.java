package org.spdx.testbed.util;

import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestCaseCategory;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TestCaseSelector {
    private final TestCaseFinder testCaseFinder;

    public TestCaseSelector() {
        this.testCaseFinder = new TestCaseFinder();
    }

    // Currently used only for testing
    public TestCaseSelector(TestCaseFinder testCaseFinder) {
        this.testCaseFinder = testCaseFinder;
    }

    public List<TestCase> selectTestCases(String[] nameParameters, String[] categoriesParameters) {
        var casesByNamesOptional = Optional.ofNullable(nameParameters)
                .map(this::arrayToList)
                .map(testCaseFinder::findTestCasesByNames);
        var casesByCategoriesOptional = Optional.ofNullable(categoriesParameters)
                .map(inputArray -> arrayToList(inputArray, TestCaseCategory::fromString))
                .map(testCaseFinder::findTestCasesByCategories);

        List<TestCase> selectedTestCases;
        if (casesByNamesOptional.isPresent()) {
            // If test cases are specified by name, we retain the provided ordering
            selectedTestCases = casesByNamesOptional.get();
            casesByCategoriesOptional.ifPresent(filterForMatchingNames(selectedTestCases));
        } else if (casesByCategoriesOptional.isPresent()) {
            selectedTestCases = casesByCategoriesOptional.get();
            // If selecting only by category, sort alphabetically
            selectedTestCases.sort(TestCase::compareTo);
        } else {
            throw new IllegalArgumentException("Must provide either names or categories or both");
        }
        return selectedTestCases;
    }

    private Consumer<List<TestCase>> filterForMatchingNames(List<TestCase> listToFilter) {
        return filterList -> {
            var allowedNames = filterList.stream()
                    .map(TestCase::getName)
                    .collect(Collectors.toList());
            var allowedElements = listToFilter.stream()
                    .filter(testCase -> allowedNames.contains(testCase.getName()))
                    .collect(Collectors.toList());
            listToFilter.retainAll(allowedElements);
        };
    }

    private <T> List<T> arrayToList(T[] inputArray) {
        return arrayToList(inputArray, Function.identity());
    }

    private <I, O> List<O> arrayToList(I[] inputArray, Function<I, O> conversionFunction) {
        return Arrays.stream(inputArray)
                .map(conversionFunction)
                .collect(Collectors.toList());
    }
}