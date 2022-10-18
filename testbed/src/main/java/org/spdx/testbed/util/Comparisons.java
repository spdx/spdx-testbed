// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spdx.jacksonstore.JacksonSerializer;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxDocument;
import org.spdx.testbed.util.json.Difference;
import org.spdx.testbed.util.json.JsonComparison;

import javax.annotation.Nonnull;

import java.util.List;

public class Comparisons {
    /**
     * Compares the two provided documents and returns a list of differences. Each difference is
     * provided with the respective values from the documents, the path of the property in the
     * document, and optionally an additional comment.
     * When comparing elements of a list, two separate paths are provided. This is because the
     * ordering of a list does not matter, so the index of matching elements may be different
     * between the two documents. In the absence of an exact match, the Spdx id is used to
     * identify corresponding elements on the two lists (if such an id is present).
     * <p>
     * Note: For technical reasons, the comparison is performed by serializing the documents to
     * json and comparing the jsons. Two of those reasons are the handling of anonymous id's in
     * the datastructure provided by java-spdx-library, and subtleties regarding related elements
     * that can lead to infinite recursion.
     *
     * @return a list of {@link Difference}s
     * @throws InvalidSPDXAnalysisException In case of parsing errors
     */
    public static List<Difference> findDifferencesInSerializedJson(@Nonnull SpdxDocument actualDocument,
                                                                   @Nonnull SpdxDocument expectedDocument) throws InvalidSPDXAnalysisException {
        var expectedJson = asJson(actualDocument);
        var actualJson = asJson(expectedDocument);
        return JsonComparison.findDifferences(expectedJson, actualJson);
    }

    private static ObjectNode asJson(ModelObject modelObject) throws InvalidSPDXAnalysisException {
        var serializer = new JacksonSerializer(new ObjectMapper(),
                MultiFormatStore.Format.JSON_PRETTY, MultiFormatStore.Verbose.COMPACT,
                modelObject.getModelStore());
        return serializer.docToJsonNode(modelObject.getDocumentUri());
    }
}
