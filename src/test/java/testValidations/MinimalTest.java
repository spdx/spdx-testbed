package testValidations;

import org.junit.jupiter.api.Test;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.SimpleUriValue;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.TypedValue;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.storage.simple.StoredTypedItem;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class MinimalTest {

    @Test
    public void correctMinimalExample() throws IOException, InvalidFileNameException {
        String filepath = "/home/armin/IdeaProjects/spdx-testbed/SPDXDocumentExamples/playgroundFiles/SPDXXML_minimalExample-v2.2.spdx.xml";
        File spdxDocument = new File(filepath);

        try {
            SpdxDocument doc = SpdxToolsHelper.deserializeDocument(spdxDocument);

            assertThat(doc.verify()).isEmpty();

            assertThat(doc.getId()).isEqualTo("SPDXRef-DOCUMENT");
            assertThat(doc.getSpecVersion()).isEqualTo("SPDX-2.2");

            var creationInfo = doc.getCreationInfo();
            assertThat(creationInfo).isNotNull();
            assertThat(creationInfo.getCreated()).isEqualTo("2022-01-01T00:00:00Z");
            var creators = creationInfo.getCreators();
            assertThat(creators.stream().findFirst().get()).isEqualTo("Tool: test-tool");

            assertThat(doc.getName()).isPresent();
            assertThat(doc.getName().get()).isEqualTo("SPDX-tool-test");
            assertThat(doc.getDataLicense().getId()).isEqualTo("CC0-1.0");

            String documentUri = doc.getDocumentUri();
            assertThat(documentUri).isEqualTo("some_namespace");

            var documentDescribes = doc.getDocumentDescribes();
            assertThat(documentDescribes.stream().findFirst().get().getId()).isEqualTo("SPDXRef-somefile");

            var modelStore = doc.getModelStore();
            var spdxFile = modelStore.getTypedValue(documentUri, "SPDXRef-somefile");
            assertThat(spdxFile).isPresent();
            var spdxFileStoredTypedItem = (StoredTypedItem) spdxFile.get();

            var checksum = (ArrayList) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_CHECKSUM);
            var checksumId = ((TypedValue) checksum.get(0)).getId();
            var checksumNumber = (String) modelStore.getValue(documentUri, checksumId, SpdxConstants.PROP_CHECKSUM_VALUE).get();
            var checksumAlgorithm = (SimpleUriValue) modelStore.getValue(documentUri, checksumId, SpdxConstants.PROP_CHECKSUM_ALGORITHM).get();

            var copyrightText = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_COPYRIGHT_TEXT);
            var fileName = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_NAME);
            var licenseConcluded = (TypedValue) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_LICENSE_CONCLUDED);

            assertThat(fileName).isEqualTo("./foo.txt");
            assertThat(licenseConcluded.getId()).isEqualTo("LGPL-3.0-only");
            assertThat(checksumAlgorithm.getIndividualURI()).isEqualTo(ChecksumAlgorithm.SHA1.getIndividualURI());
            assertThat(checksumNumber).isEqualTo("d6a770ba38583ed4bb4525bd96e50461655d2758");
            assertThat(copyrightText).isEqualTo("Copyright 2022 some guy");

        } catch (InvalidSPDXAnalysisException e) {
            throw new RuntimeException(e);
        }
    }
}
