package org.spdx.testbed.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@AllArgsConstructor
@ToString
public class Difference {
    private JsonNode firstValue;
    private JsonNode secondValue;
    private String path;
    private String comment;
}
