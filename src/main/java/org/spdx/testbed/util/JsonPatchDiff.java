package org.spdx.testbed.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonPatchDiff {
    @JsonProperty("op")
    private String operation;
    // Path of the second element in case the operation describes two
    private String path;
    // Path of the first element in case the operation describes two
    private String from;
    private JsonNode value;
    private JsonNode fromValue;

    public ObjectNode toObjectNode(ObjectMapper mapper) {
        return mapper.valueToTree(this);
    }

}
