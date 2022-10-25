package org.spdx.testbed.util;

import org.spdx.testbed.TestCase;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TestCaseUtils {
    public static Consumer<List<TestCase>> filterForMatchingNames(List<TestCase> listToFilter) {
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
}
