package org.spdx.testbed.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Specialized version of {@link Difference} intended to be used when the difference is detected
 * in elements of a list or in children of such elements. Since reordered lists are considered to
 * be equal, the concrete list index and therefore the path of the two elements may differ. In
 * order to capture this information, this class adds a second path.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ListDifference extends Difference {
    private String secondPath;

    public ListDifference(JsonNode firstValue, JsonNode secondValue, String path, String comment,
                          String secondPath) {
        super(firstValue, secondValue, path, comment);
        this.secondPath = secondPath;
    }

    @Override
    public String toString() {
        return "ListDifference(firstValue=" + this.getFirstValue() + ", secondValue=" + this.getSecondValue() + ", firstPath=" + this.getPath() + ", secondPath=" + this.getSecondPath() + ", comment=" + this.getComment() + ")";
    }
}
