import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.spdx.tools.SpdxToolsHelper;
import org.spdx.tools.SpdxVerificationException;
import org.spdx.tools.Verify;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCase {
    @Test
    public void simpleTestCase() throws SpdxVerificationException, IOException,
            InterruptedException {
        // Required steps:
        // pip install ../tools-python/
        // (this means the CLI tool "convertor" is available in your global python installation...)

        // Generate any valid SPDX
        // Example using the Python converter tool
        // Could also call "convertor" directly after tools-python is installed
        var processBuilder = new ProcessBuilder("python3", "/home/nico/IdeaProjects/tools-python/spdx/cli_tools/convertor.py", "-f", "tag", "/home/nico/IdeaProjects/spdx-testbed/src/test/resources/SPDXTagExample.tag", "-o", "output.json");
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // Could include errors, helpful for debugging
        var output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);

        int exitCode = process.waitFor();
        assertThat(exitCode).withFailMessage("Received nonzero exit code. Stdout: " + output).isEqualTo(0);

        Path outputFilepath = Path.of("output.json");
        SpdxToolsHelper.SerFileType fileType = SpdxToolsHelper.SerFileType.JSON;

        // Verify the output
        var verificationErrors = Verify.verify(outputFilepath.toString(), fileType);
        assertThat(verificationErrors).isEmpty();
    }
}
