package org.spdx.testbed.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ListDifference extends Difference {
    // In case of list differences, the indices of the two elements may be different, so we 
    // provide the second path explicitly.
    private String secondPath;

    public ListDifference(JsonNode firstValue, JsonNode secondValue, String path, String comment,
                          String secondPath) {
        super(firstValue, secondValue, path, comment);
        this.secondPath = secondPath;
    }
}
