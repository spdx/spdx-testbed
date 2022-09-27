package conversion;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxConverter;
import org.spdx.tools.SpdxConverterException;
import org.spdx.tools.SpdxToolsHelper;
import util.Comparisons;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConversion {
    /**
     * Test that conversion from tag to json format works.
     * The converted file is expected to be provided by the user
     */
    @ParameterizedTest
    @MethodSource("provideFileNames")
    public void convertDocument(String inputFilePath, String outputFilePath) throws IOException,
            InvalidSPDXAnalysisException,
            InvalidFileNameException, SpdxConverterException {
        var inputFile = new File(inputFilePath);

        /*
         This should be handled by the user script
         */
        var convertedFile = new File(outputFilePath);
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

    private static Stream<Arguments> provideFileNames() {
        var xmlInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXXMLExample-v2.3.spdx.xml";
        var rdfInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXRdfExample-v2.3.spdx.rdf.xml";
        var jsonInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXJSONExample-v2.3.spdx.json";
        var xlsInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXSpreadsheetExample-v2.3.xls";
        var xlsxInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXSpreadsheetExample-v2.3.xlsx";
        var tagInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXTagExample-v2.3.spdx";
        var yamlInputFile = "SPDXDocumentExamples/spdx-spec-v2.3/SPDXYAMLExample-2.3.spdx.yaml";
        var inputFiles = Set.of(xmlInputFile, rdfInputFile, jsonInputFile, xlsInputFile,
                xlsxInputFile, tagInputFile, yamlInputFile);

        var xmlOutputFile = "SPDXDocumentExamples/temp/convertedFile.spdx.xml";
        var rdfOutputFile = "SPDXDocumentExamples/temp/convertedFile.spdx.rdf.xml";
        var jsonOutputFile = "SPDXDocumentExamples/temp/convertedFile.spdx.json";
        var xlsOutputFile = "SPDXDocumentExamples/temp/convertedFile.xls";
        var xlsxOutputFile = "SPDXDocumentExamples/temp/convertedFile.xlsx";
        var tagOutputFile = "SPDXDocumentExamples/temp/convertedFile.spdx";
        var yamlOutputFile = "SPDXDocumentExamples/temp/convertedFile.spdx.yaml";
        var outputFiles = Set.of(xmlOutputFile, rdfOutputFile, jsonOutputFile, xlsOutputFile,
                xlsxOutputFile, tagOutputFile, yamlOutputFile);

        return inputFiles.stream()
                .flatMap(inputFile -> outputFiles.stream()
                        .map(outputFile -> Arguments.of(inputFile, outputFile)));
    }
}
