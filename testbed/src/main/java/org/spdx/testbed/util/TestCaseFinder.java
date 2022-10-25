package org.spdx.testbed.util;

import org.spdx.testbed.TestCase;
import org.spdx.testbed.TestCaseCategory;
import org.spdx.testbed.util.testClassification.TestName;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.spdx.testbed.util.TestCaseUtils.filterForMatchingNames;

/**
 * Utility class to dynamically find test cases. Works by scanning the classpath for classes
 * annotated with special marker annotations defined in the testClassification package. Can scan
 * for specific attribute values on the annotations as well.
 */
public class TestCaseFinder {

    public List<TestCase> findTestCasesByCategories(List<TestCaseCategory> categories) {
        var casesOptional = categories.stream()
                .map(category -> determineTestCases(category.getAnnotationClass()))
                .reduce(this::intersectByName);
        return casesOptional.orElse(new ArrayList<>());
    }

    public List<TestCase> findTestCasesByNames(List<String> names) {
        var foundTestCases = new ArrayList<TestCase>();
        for (var name : names) {
            var cases = determineTestCases(TestName.class, Map.of("value", name));
            if (cases.isEmpty()) {
                System.out.println("No test case found for name " + name + "!");
            }
            foundTestCases.addAll(cases);
        }
        return foundTestCases;
    }
    
    public List<TestCase> findAllTestCases() {
        return determineTestCases(TestName.class, Map.of());
    }

    private List<TestCase> intersectByName(List<TestCase> accumulatedList,
                                           List<TestCase> nextList) {
        filterForMatchingNames(accumulatedList).accept(nextList);
        return accumulatedList;
    }

    private List<TestCase> determineTestCases(Class<? extends Annotation> annotationClass) {
        return determineTestCases(annotationClass, Map.of());
    }

    /**
     * Scans classes in package org.spdx.testbed for all candidates that are annotated with a
     * specific annotation that carries the prescribed attributes.
     *
     * @param annotationClass    annotation class to look for
     * @param requiredAttributes attributes on the annotation that are required. Can be an empty
     *                           map in case no attributes are required
     * @return one instance of each class satisfying the search criteria, constructed via no-args
     * constructor
     */
    private List<TestCase> determineTestCases(Class<? extends Annotation> annotationClass,
                                              Map<String, Object> requiredAttributes) {
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(annotationClass));

        var beansWithRequiredAttributes = provider.findCandidateComponents("org.spdx.testbed")
                .stream()
                .filter(beanDefinition -> beanDefinition instanceof AnnotatedBeanDefinition)
                .map(beanDefinition -> (AnnotatedBeanDefinition) beanDefinition)
                .filter(hasAttributes(annotationClass.getCanonicalName(), requiredAttributes))
                .collect(Collectors.toList());

        var testCases = new ArrayList<TestCase>();

        for (var bean : beansWithRequiredAttributes) {
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

    private Predicate<AnnotatedBeanDefinition> hasAttributes(String annotationName, Map<String,
            Object> attributes) {
        return beanDef -> {
            if (attributes.isEmpty()) {
                return true;
            }
            var annotationAttributes = beanDef.getMetadata()
                    .getAnnotationAttributes(annotationName);
            if (annotationAttributes == null) {
                return false;
            }
            return annotationAttributes.entrySet().containsAll(attributes.entrySet());
        };
    }
}
