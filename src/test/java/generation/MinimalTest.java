package generation;

import org.junit.jupiter.api.Test;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.storage.simple.StoredTypedItem;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;
import util.Comparisons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MinimalTest {

    private SpdxDocument buildMinimalExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setDataLicense(LicenseInfoFactory.parseSPDXLicenseString("CC0-1.0"));
        document.setCreationInfo(document.createCreationInfo(List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-3.0-only");
        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", concludedLicense, List.of(), "Copyright 2022 some guy", sha1Checksum).build();

        document.getDocumentDescribes().add(file);

        return document;
    }

    @Test
    public void generateMinimalExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildMinimalExample();
        assertThat(doc.verify()).isEmpty();

        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testOutput/generated/test.xml"));
    }

    @Test
    public void compareMinimalExample() throws InvalidSPDXAnalysisException, IOException, InvalidFileNameException {
        var referenceDoc = buildMinimalExample();

        File inputFile = new File("testOutput/generated/test.xml");
        var inputDoc = SpdxToolsHelper.deserializeDocument(inputFile);

        assertThat(Comparisons.findDifferences(referenceDoc, inputDoc, false)).isEmpty();
    }

    @Test
    public void correctMinimalExample() throws InvalidSPDXAnalysisException {
        SpdxDocument doc = buildMinimalExample();
        assertThat(doc.verify()).isEmpty();

        assertThat(doc.getId()).isEqualTo("SPDXRef-DOCUMENT");
        assertThat(doc.getSpecVersion()).isEqualTo("SPDX-2.3");

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
    }
}
