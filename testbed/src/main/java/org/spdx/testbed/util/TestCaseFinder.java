package org.spdx.testbed.util;

import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestCaseName;
import org.spdx.testbed.generationTestCases.GenerationBaselineSbomTestCase;
import org.spdx.testbed.generationTestCases.GenerationDocumentTestCase;
import org.spdx.testbed.generationTestCases.GenerationFileTestCase;
import org.spdx.testbed.generationTestCases.GenerationLicenseTestCase;
import org.spdx.testbed.generationTestCases.GenerationMinimalTestCase;
import org.spdx.testbed.generationTestCases.GenerationPackageTestCase;
import org.spdx.testbed.generationTestCases.GenerationRelationshipTestCase;
import org.spdx.testbed.generationTestCases.GenerationSnippetTestCase;
import org.spdx.testbed.util.testClassification.GenerationTest;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestCaseFinder {

    public List<TestCase> findTestCases(TestCaseName testCaseName) {
        switch (testCaseName) {
            case GENERATION_MINIMAL:
                return List.of(new GenerationMinimalTestCase());
            case GENERATION_BASELINE_SBOM:
                return List.of(new GenerationBaselineSbomTestCase());
            case GENERATION_DOCUMENT:
                return List.of(new GenerationDocumentTestCase());
            case GENERATION_PACKAGE:
                return List.of(new GenerationPackageTestCase());
            case GENERATION_FILE:
                return List.of(new GenerationFileTestCase());
            case GENERATION_SNIPPET:
                return List.of(new GenerationSnippetTestCase());
            case GENERATION_LICENSE:
                return List.of(new GenerationLicenseTestCase());
            case GENERATION_RELATIONSHIP:
                return List.of(new GenerationRelationshipTestCase());
            case GENERATION_ALL:
                return determineGenerationTestCases();
        }
        throw new IllegalArgumentException("Unknown test case name: " + testCaseName);
    }

    /**
     * Scans classes on the classpath for the {@link GenerationTest} annotation.
     * Note: This currently only searches for annotated classes. It is also possible to read
     * annotation attributes, see e.g.
     * <a href="https://www.baeldung.com/java-scan-annotations-runtime#scanning-annotations-with-spring-context-library">this tutorial</a>
     */
    private List<TestCase> determineGenerationTestCases() {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(GenerationTest.class));

        var beanDefs = provider.findCandidateComponents("org.spdx.testbed.generationTestCases");

        var annotatedBeans = beanDefs.stream()
                .filter(beanDefinition -> beanDefinition instanceof AnnotatedBeanDefinition)
                .map(beanDefinition -> (AnnotatedBeanDefinition) beanDefinition)
                .collect(Collectors.toList());

        var testCases = new ArrayList<TestCase>();

        for (var bean : annotatedBeans) {
            try {
                var clazz = Class.forName(bean.getBeanClassName());
                if (TestCase.class.isAssignableFrom(clazz)) {
                    var constructor = (clazz).getDeclaredConstructor();
                    testCases.add((TestCase) constructor.newInstance());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                // If this happens, something is wrong with the setup. No clear-cut solution I 
                // can think of, so generic RuntimeException seems "okay".
                throw new RuntimeException(e);
            }
        }

        return testCases;
    }
}
