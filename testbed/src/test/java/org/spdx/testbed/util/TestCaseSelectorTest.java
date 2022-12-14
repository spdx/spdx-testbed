package org.spdx.testbed.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestCaseCategory;
import org.spdx.testbed.TestResult;
import org.spdx.tools.InvalidFileNameException;

/**
 * Tests for test case selection logic.
 */
@ExtendWith(MockitoExtension.class)
public class TestCaseSelectorTest {

  @Spy
  private TestCaseFinder testCaseFinder;
  @InjectMocks
  private TestCaseSelector selector;

  @Test
  public void selectOnlyByNames() {
    var namesParameter = new String[]{"firstValue", "secondValue", "thirdValue"};
    var namesList = Arrays.stream(namesParameter).collect(Collectors.toList());
    var expectedCases = new ArrayList<TestCase>(List.of(new TestTestCase1()));
    when(testCaseFinder.findTestCasesByNames(namesList)).thenReturn(expectedCases);

    var cases = selector.selectTestCases(namesParameter, null);

    verify(testCaseFinder).findTestCasesByNames(namesList);
    verifyNoMoreInteractions(testCaseFinder);
    assertThat(cases).containsExactlyElementsOf(expectedCases);
  }

  @Test
  public void selectOnlyByCategories() {
    var categoriesParameter = new String[]{"generation"};
    var categoriesList = List.of(TestCaseCategory.GENERATION);
    var casesReturnedByFinder = List.of(new TestTestCase2(), new TestTestCase1());
    // selector should order alphabetically
    when(testCaseFinder.findTestCasesByCategories(categoriesList)).thenReturn(
        casesReturnedByFinder);
    var expectedCases = casesReturnedByFinder.stream().sorted().collect(Collectors.toList());

    var cases = selector.selectTestCases(null, categoriesParameter);

    verify(testCaseFinder).findTestCasesByCategories(categoriesList);
    verifyNoMoreInteractions(testCaseFinder);
    assertThat(cases).containsExactlyElementsOf(expectedCases);
  }

  @Test
  public void selectByNamesAndCategories() {
    var namesParameter = new String[]{"firstValue", "secondValue", "thirdValue"};
    var namesList = Arrays.stream(namesParameter).collect(Collectors.toList());
    var categoriesParameter = new String[]{"generation"};
    var categoriesList = List.of(TestCaseCategory.GENERATION);
    var returnedNameCases = new ArrayList<>(List.of(new TestTestCase1(), new TestTestCase2()));
    when(testCaseFinder.findTestCasesByNames(namesList)).thenReturn(returnedNameCases);
    var returnedCategoryCases = new ArrayList<>(List.of(new TestTestCase2(),
        new TestTestCase3()));
    when(testCaseFinder.findTestCasesByCategories(categoriesList)).thenReturn(
        returnedCategoryCases);

    final var cases = selector.selectTestCases(namesParameter, categoriesParameter);

    verify(testCaseFinder).findTestCasesByNames(namesList);
    verify(testCaseFinder).findTestCasesByCategories(categoriesList);
    verifyNoMoreInteractions(testCaseFinder);
    assertThat(cases.size()).isEqualTo(1);
    assertThat(cases).allMatch(singleCase -> singleCase instanceof TestTestCase2);
  }

  // What a great name!
  private static class TestTestCase1 implements TestCase {

    @Override
    public TestResult test(String inputFile) throws InvalidSPDXAnalysisException, IOException,
        InvalidFileNameException {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String getName() {
      return "test1";
    }
  }

  private static class TestTestCase2 implements TestCase {

    @Override
    public TestResult test(String inputFile) throws InvalidSPDXAnalysisException, IOException,
        InvalidFileNameException {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String getName() {
      return "test2";
    }
  }

  private static class TestTestCase3 implements TestCase {

    @Override
    public TestResult test(String inputFile) throws InvalidSPDXAnalysisException, IOException,
        InvalidFileNameException {
      throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public String getName() {
      return "test3";
    }
  }


}
