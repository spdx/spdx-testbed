package org.spdx.testbed.util.testClassification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface to allow dynamically selecting test cases by name. The benefits of doing this
 * dynamically are: - information relevant for a test case (in this case, the name as used by the
 * CLI) can be kept in annotations directly on the class - maintaining a name-class mapping in a
 * central location would become unwieldy once the number of test cases grows large
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestName {

  String value();
}
