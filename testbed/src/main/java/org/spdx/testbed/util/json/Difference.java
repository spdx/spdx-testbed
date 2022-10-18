package org.spdx.testbed.util.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Holds information about a difference detected during the comparison of two json documents,
 * including the two values and the path of those values in the documents.
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Difference {
    private final JsonNode actualValue;
    private final JsonNode expectedValue;
    private final String path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final String comment;

    public static DifferenceBuilder builder() {
        return new DifferenceBuilder();
    }

    /**
     * This is a customized builder that will produce either {@link Difference} or
     * {@link ListDifference}, depending on whether pathInReferenceDoc is set.
     */
    public static class DifferenceBuilder {
        private JsonNode actualValue;
        private JsonNode expectedValue;
        private String path;
        private String pathInReferenceDoc;
        private String comment;


        DifferenceBuilder() {
        }

        public DifferenceBuilder actualValue(JsonNode actualValue) {
            this.actualValue = actualValue;
            return this;
        }

        public DifferenceBuilder expectedValue(JsonNode expectedValue) {
            this.expectedValue = expectedValue;
            return this;
        }

        public DifferenceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public DifferenceBuilder pathInReferenceDoc(String pathInReferenceDoc) {
            this.pathInReferenceDoc = pathInReferenceDoc;
            return this;
        }

        public DifferenceBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Difference build() {
            if (pathInReferenceDoc == null) {
                return new Difference(actualValue, expectedValue, path, comment);
            } else {
                return new ListDifference(actualValue, expectedValue, path, comment,
                        pathInReferenceDoc);
            }
        }

        public String toString() {
            return "Difference.DifferenceBuilder(actualValue=" + this.actualValue + ", " +
                    "expectedValue" +
                    "=" + this.expectedValue + ", path=" + this.path + ", pathInReferenceDoc=" + this.pathInReferenceDoc + ", comment=" + this.comment + ")";
        }
    }
}
