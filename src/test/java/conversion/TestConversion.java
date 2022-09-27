package conversion;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxConverter;
import org.spdx.tools.SpdxConverterException;
import org.spdx.tools.SpdxToolsHelper;
import util.Comparisons;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConversion {
    /**
     * Test that conversion from tag to json format works.
     * The converted file is expected to be provided by the user
     */
    @Test
    public void convertXmlToJson() throws IOException, InvalidSPDXAnalysisException,
            InvalidFileNameException, SpdxConverterException {
        var inputFile = new File("SPDXDocumentExamples/tools-java/SPDXXMLExample-v2.3.spdx.xml");

        /*
         This should be handled by the user script
         */
        var convertedFile = new File("SPDXDocumentExamples/temp/convertedFile.json");
        // Just some convenient cleanup
        convertedFile.delete();
        convertedFile.getParentFile().mkdirs();
        SpdxConverter.convert(inputFile.getPath(), convertedFile.getPath());


        var inputDocument = SpdxToolsHelper.deserializeDocument(inputFile);
        var outputDocument = SpdxToolsHelper.deserializeDocument(convertedFile);
        assertThat(outputDocument.verify()).isEmpty();

        // Covers documentUri and id
        assertThat(inputDocument).isEqualTo(outputDocument);
        // Covers all other properties (hopefully...)
        assertThat(Comparisons.findDifferences(inputDocument, outputDocument, true)).isEmpty();
    }
}
