package testValidations;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.ModelSet;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxElement;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MinimalTest {

    @Test
    public void correctMinimalExample() throws IOException, InvalidFileNameException {
        String filepath = "/home/armin/IdeaProjects/spdx-testbed/SPDXDocumentExamples/playgroundFiles/SPDXXML_minimalExample-v2.2.spdx.xml";
        File spdxDocument = new File(filepath);
        SpdxDocument doc;

        try {
            doc = SpdxToolsHelper.deserializeDocument(spdxDocument);
        } catch (InvalidSPDXAnalysisException e) {
            throw new RuntimeException(e);
        }
        assertThat(doc.verify()).isEmpty();

        try {
            assertThat(doc.getId()).isEqualTo("SPDXRef-DOCUMENT");
            assertThat(doc.getSpecVersion()).isEqualTo("SPDX-2.2");

            var creationInfo = doc.getCreationInfo();
            assert creationInfo != null;
            assertThat(creationInfo.getCreated()).isEqualTo("2022-01-01T00:00:00Z");
//            Collection<String> creators = creationInfo.getCreators();
//            assertThat(creators).isEqualTo((List.of("Tool: test-tool")));

            assert doc.getName().isPresent();
            assertThat(doc.getName().get()).isEqualTo("SPDX-tool-test");
//            assertThat(doc.getDataLicense()).isEqualTo("CC0-1.0");

            assertThat(doc.getDocumentUri()).isEqualTo("some_namespace");

//            assertThat(doc.getDocumentDescribes()).isEqualTo("SPDXRef-somefile");

        } catch (InvalidSPDXAnalysisException e) {
            throw new RuntimeException(e);
        }
    }
}
