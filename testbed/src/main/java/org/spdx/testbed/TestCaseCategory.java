package org.spdx.testbed;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.spdx.testbed.util.testclassification.GenerationTest;

/**
 * Lists all available test categories, and encapsulates the mapping between the CLI names and the
 * corresponding annotation class that is used internally to mark matching test cases.
 */
public enum TestCaseCategory {
  GENERATION("generation", GenerationTest.class);

  private final String categoryName;
  private final Class<? extends Annotation> annotationClass;

  TestCaseCategory(String categoryName, Class<? extends Annotation> annotationClass) {
    this.categoryName = categoryName;
    this.annotationClass = annotationClass;
  }

  /**
   * Resolve a TestCaseCategory from the provided string.
   *
   * @throws IllegalArgumentException in case no match is found
   */
  public static TestCaseCategory fromString(String categoryAsString) {
    for (var testCaseCategory : TestCaseCategory.values()) {
      if (testCaseCategory.getCategoryName().equals(categoryAsString)) {
        return testCaseCategory;
      }
    }
    throw new IllegalArgumentException(
        "Unknown test case category: " + categoryAsString + "\nKnown categories are: "
            + categoryNames());
  }

  public String getCategoryName() {
    return categoryName;
  }

  public Class<? extends Annotation> getAnnotationClass() {
    return annotationClass;
  }

  private static List<String> categoryNames() {
    return Arrays.stream(TestCaseCategory.values())
        .map(TestCaseCategory::getCategoryName).collect(Collectors.toList());
  }
}
