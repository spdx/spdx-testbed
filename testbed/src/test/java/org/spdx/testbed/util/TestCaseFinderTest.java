package org.spdx.testbed.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestCaseName;
import org.spdx.testbed.generationTestCases.GenerationTestCase;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class TestCaseFinderTest {
    private TestCaseFinder testCaseFinder;

    @BeforeEach
    public void setup() {
        testCaseFinder = new TestCaseFinder();
    }


    @Test
    public void findGenerationTestCases() {
        var expectedNames = Arrays.stream(TestCaseName.values())
                .filter(testCaseName -> !TestCaseName.GENERATION_ALL.equals(testCaseName))
                .map(TestCaseName::getFullName).collect(Collectors.toList());

        var testCases = testCaseFinder.findTestCases(TestCaseName.GENERATION_ALL);

        assertThat(testCases).allMatch(element -> element instanceof GenerationTestCase);
        var testCaseNames = testCases.stream().map(TestCase::getName).collect(Collectors.toList());
        assertThat(testCaseNames).containsExactlyInAnyOrderElementsOf(expectedNames);
    }

    @ParameterizedTest
    @ValueSource(strings = {"generationMinimalTest", "generationBaselineSbomTest",
            "generationDocumentTest", "generationPackageTest", "generationFileTest",
            "generationSnippetTest", "generationLicenseTest", "generationRelationshipTest"})
    public void findSpecificTestCase(String testCaseNameAsString) {
        var testCaseName = TestCaseName.fromString(testCaseNameAsString);
        var testCases = testCaseFinder.findTestCases(testCaseName);

        assertThat(testCases.size()).isEqualTo(1);
        assertThat(testCases).allMatch(testCase -> testCase.getName().equals(testCaseNameAsString));
    }
}
