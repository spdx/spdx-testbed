package conversion;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxConverter;
import org.spdx.tools.SpdxConverterException;
import org.spdx.tools.SpdxToolsHelper;
import org.spdx.tools.SpdxVerificationException;
import org.spdx.tools.Verify;
import util.Comparisons;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class TestConversion {
    @Disabled("convertor.py is hardcoded, needs to be changed later")
    @Test
    public void tagToJsonConversionCreatesValidDocument() throws SpdxVerificationException,
            IOException,
            InterruptedException {
        // Required steps:
        // pip install ../tools-python/
        // (this means the CLI tool "convertor" is available in your global python installation...)

        // Generate any valid SPDX
        // Example using the Python converter tool
        // Could also call "convertor" directly after tools-python is installed
        var processBuilder = new ProcessBuilder("python3", "/home/nico/IdeaProjects/tools-python" +
                "/spdx/cli_tools/convertor.py", "-f", "tag", "/home/nico/IdeaProjects/spdx" +
                "-testbed/src/test/resources/SPDXTagExample.tag", "-o", "output.json");
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Could include errors, helpful for debugging
        var output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);

        int exitCode = process.waitFor();
        assertThat(exitCode).withFailMessage("Received nonzero exit code. Stdout: " + output)
                .isEqualTo(0);

        Path outputFilepath = Path.of("output.json");
        SpdxToolsHelper.SerFileType fileType = SpdxToolsHelper.SerFileType.JSON;

        // Verify the output
        var verificationErrors = Verify.verify(outputFilepath.toString(), fileType);
        assertThat(verificationErrors).isEmpty();
    }

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
