package org.spdx.testbed.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.spdx.testbed.TestCase;

/**
 * Common utilities related to test cases.
 */
public class TestCaseUtils {

  /**
   * Intersect two lists of TestCases, keeping only common ones. TestCases are identified by name.
   *
   * @param listToFilter list that will be modified
   * @return {@link Consumer} accepting a list to be used for determining "allowed" names
   */
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
