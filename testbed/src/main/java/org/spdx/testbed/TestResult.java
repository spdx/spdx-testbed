package org.spdx.testbed;

import lombok.Builder;
import org.spdx.testbed.util.Comparisons;

import java.util.Collections;
import java.util.Map;

@Builder
public class TestResult {
    Boolean success;
    @Builder.Default
    Map<String, Comparisons.Tuple<?>> differences = Collections.emptyMap();
}
