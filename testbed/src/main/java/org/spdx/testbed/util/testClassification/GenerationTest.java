package org.spdx.testbed.util.testClassification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface to allow dynamically selecting all generation testcases. As it is inherited, 
 * it can either be added directly to generation testcase classes or to a common superclass like
 * {@link org.spdx.testbed.generationTestCases.GenerationTestCase}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface GenerationTest {
}
