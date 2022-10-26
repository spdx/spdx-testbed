package org.spdx.testbed.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestCaseCategory;
import org.spdx.testbed.generationtestcases.GenerationTestCase;

/**
 * Tests for dynamic test case filtering.
 */
public class TestCaseFinderTest {

  private static final String GENERATION_MINIMAL = "generationMinimalTest";
  private static final String GENERATION_BASELINE_SBOM = "generationBaselineSbomTest";
  private static final String GENERATION_DOCUMENT = "generationDocumentTest";
  private static final String GENERATION_PACKAGE = "generationPackageTest";
  private static final String GENERATION_FILE = "generationFileTest";
  private static final String GENERATION_SNIPPET = "generationSnippetTest";
  private static final String GENERATION_LICENSE = "generationLicenseTest";
  private static final String GENERATION_RELATIONSHIP = "generationRelationshipTest";
  private static final List<String> generationTestCaseNames = List.of(GENERATION_MINIMAL,
      GENERATION_BASELINE_SBOM, GENERATION_DOCUMENT, GENERATION_PACKAGE, GENERATION_FILE,
      GENERATION_SNIPPET, GENERATION_LICENSE, GENERATION_RELATIONSHIP);
  private TestCaseFinder testCaseFinder;

  @BeforeEach
  public void setup() {
    testCaseFinder = new TestCaseFinder();
  }

  @Test
  public void findGenerationTestCases() {
    var testCases =
        testCaseFinder.findTestCasesByCategories(List.of(TestCaseCategory.GENERATION));

    assertThat(testCases).allMatch(element -> element instanceof GenerationTestCase);
    var testCaseNames = testCases.stream().map(TestCase::getName).collect(Collectors.toList());
    assertThat(testCaseNames).containsExactlyInAnyOrderElementsOf(generationTestCaseNames);
  }

  /**
   * This apparently requires a Javadoc.
   */
  @ParameterizedTest
  @ValueSource(strings = {GENERATION_MINIMAL, GENERATION_BASELINE_SBOM,
      GENERATION_DOCUMENT, GENERATION_PACKAGE, GENERATION_FILE,
      GENERATION_SNIPPET, GENERATION_LICENSE, GENERATION_RELATIONSHIP})
  public void findSpecificTestCaseByName(String testCaseNameAsString) {
    var testCases = testCaseFinder.findTestCasesByNames(List.of(testCaseNameAsString));

    assertThat(testCases.size()).isEqualTo(1);
    assertThat(testCases).allMatch(testCase -> testCase.getName().equals(testCaseNameAsString));
  }

  @Test
  public void findMultipleTestsByNames() {
    var testCases = testCaseFinder.findTestCasesByNames(List.of(GENERATION_MINIMAL,
        GENERATION_BASELINE_SBOM, GENERATION_DOCUMENT));

    assertThat(testCases.size()).isEqualTo(3);
    assertThat(testCases.stream()
        .map(TestCase::getName)).containsExactly(GENERATION_MINIMAL,
        GENERATION_BASELINE_SBOM, GENERATION_DOCUMENT);
  }
}
