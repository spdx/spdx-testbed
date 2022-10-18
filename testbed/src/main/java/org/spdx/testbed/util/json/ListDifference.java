package org.spdx.testbed.util.json;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Specialized version of {@link Difference} intended to be used when the difference is detected
 * in elements of a list or in children of such elements. Since reordered lists are considered to
 * be equal, the concrete list index and therefore the path of the two elements may differ. In
 * order to capture this information, this class adds a second path.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class ListDifference extends Difference {
    private final String pathInReferenceDoc;

    public ListDifference(JsonNode actualValue, JsonNode expectedValue, String path, String comment,
                          String pathInReferenceDoc) {
        super(actualValue, expectedValue, path, comment);
        this.pathInReferenceDoc = pathInReferenceDoc;
    }

    @Override
    public String toString() {
        return "ListDifference(actualValue=" + this.getActualValue() + ", expectedValue=" + this.getExpectedValue() + ", path=" + this.getPath() + ", pathInReferenceDoc=" + this.getPathInReferenceDoc() + ", comment=" + this.getComment() + ")";
    }
}
