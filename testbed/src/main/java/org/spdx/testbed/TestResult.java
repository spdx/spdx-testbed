package org.spdx.testbed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Builder;

@Builder
public class TestResult {
    Boolean success;

    @Builder.Default
    ArrayNode differences = (new ObjectMapper()).createArrayNode();
}
