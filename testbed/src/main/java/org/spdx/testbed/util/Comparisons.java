package org.spdx.testbed.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.spdx.jacksonstore.JacksonSerializer;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.ModelObject;
import org.spdx.library.model.SpdxDocument;
import org.spdx.testbed.util.json.JsonComparison;

import javax.annotation.Nonnull;

import java.util.List;

public class Comparisons {
    public static List<Difference> findDifferencesInSerializedJson(@Nonnull SpdxDocument firstDocument,
                                                                   @Nonnull SpdxDocument secondDocument) throws InvalidSPDXAnalysisException {
        var firstJson = asJson(firstDocument);
        var secondJson = asJson(secondDocument);
        return JsonComparison.findDifferences(firstJson, secondJson);
    }

    private static ObjectNode asJson(ModelObject modelObject) throws InvalidSPDXAnalysisException {
        var serializer = new JacksonSerializer(new ObjectMapper(),
                MultiFormatStore.Format.JSON_PRETTY, MultiFormatStore.Verbose.COMPACT,
                modelObject.getModelStore());
        return serializer.docToJsonNode(modelObject.getDocumentUri());
    }
}
