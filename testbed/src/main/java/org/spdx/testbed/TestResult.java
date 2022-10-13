package org.spdx.testbed;

import lombok.Builder;
import org.spdx.testbed.util.Difference;

import java.util.ArrayList;
import java.util.List;

@Builder
public class TestResult {
    Boolean success;

    @Builder.Default
    List<Difference> differences = new ArrayList<>();
}
