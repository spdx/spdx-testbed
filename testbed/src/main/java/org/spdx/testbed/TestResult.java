package org.spdx.testbed;

import lombok.Builder;
import org.spdx.testbed.util.Comparisons;

import java.util.Map;

@Builder
public class TestResult {
    Boolean success;
    Map<String, Comparisons.Tuple<?>> differences;
}
