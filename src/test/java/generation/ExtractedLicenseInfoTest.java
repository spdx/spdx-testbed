package generation;

import org.junit.jupiter.api.Test;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.ExtractedLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.listedlicense.SpdxListedLicenseLocalStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;
import util.Comparisons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtractedLicenseInfoTest {

    private SpdxDocument buildExtractedLicenseInfoExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setDataLicense(LicenseInfoFactory.parseSPDXLicenseString("CC0-1.0"));
        document.setCreationInfo(document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");

        ExtractedLicenseInfo extractedLicenseInfo = new ExtractedLicenseInfo(modelStore, documentUri, "LicenseRef-1", copyManager, true);
        extractedLicenseInfo.setExtractedText("lustiger Text");
        extractedLicenseInfo.setName("extracted license lustig");
        extractedLicenseInfo.setComment("lustiger Kommentar");
        var crossRef = document.createCrossRef("cross Ref lustig").build();

        var spdxListedLicenseLocalStore = new SpdxListedLicenseLocalStore();
        spdxListedLicenseLocalStore.addValueToCollection(documentUri, extractedLicenseInfo.getId(), SpdxConstants.PROP_CROSS_REF, crossRef);

        document.addExtractedLicenseInfos(extractedLicenseInfo);

        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", extractedLicenseInfo,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }

    @Test
    public void generateExtractedLicenseInfoExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildExtractedLicenseInfoExample();
        assertThat(doc.verify()).isEmpty();

        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testOutput/generated/test.xml"));
    }

    @Test
    public void compareExtractedLicenseInfoExample() throws InvalidSPDXAnalysisException, IOException, InvalidFileNameException {
        var referenceDoc = buildExtractedLicenseInfoExample();

        File inputFile = new File("testOutput/generated/test.xml");
        var inputDoc = SpdxToolsHelper.deserializeDocument(inputFile);

        assertThat(Comparisons.findDifferences(referenceDoc, inputDoc, false)).isEmpty();
    }
}
