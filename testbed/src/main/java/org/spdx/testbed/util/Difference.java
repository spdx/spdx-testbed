package org.spdx.testbed.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Holds information about a difference detected during the comparison of two json documents,
 * including the two values and the path of those values in the documents.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Difference {
    private JsonNode firstValue;
    private JsonNode secondValue;
    private String path;
    private String comment;

    public static DifferenceBuilder builder() {
        return new DifferenceBuilder();
    }

    /**
     * This is a customized builder that will produce either {@link Difference} or
     * {@link ListDifference}, depending on whether secondPath is set.
     */
    public static class DifferenceBuilder {
        private JsonNode firstValue;
        private JsonNode secondValue;
        private String path;
        private String secondPath;
        private String comment;


        DifferenceBuilder() {
        }

        public DifferenceBuilder firstValue(JsonNode firstValue) {
            this.firstValue = firstValue;
            return this;
        }

        public DifferenceBuilder secondValue(JsonNode secondValue) {
            this.secondValue = secondValue;
            return this;
        }

        public DifferenceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public DifferenceBuilder secondPath(String path) {
            this.secondPath = path;
            return this;
        }

        public DifferenceBuilder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Difference build() {
            if (secondPath == null) {
                return new Difference(firstValue, secondValue, path, comment);
            } else {
                return new ListDifference(firstValue, secondValue, path, comment, secondPath);
            }
        }

        public String toString() {
            return "Difference.DifferenceBuilder(firstValue=" + this.firstValue + ", secondValue" +
                    "=" + this.secondValue + ", path=" + this.path + ", secondPath=" + this.secondPath + ", comment=" + this.comment + ")";
        }
    }
}
