package generation;

import org.junit.jupiter.api.Test;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxFile;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;
import util.Comparisons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LicenseTest {
    
    private SpdxDocument buildLicenseExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();
        
        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);
        
        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setCreationInfo(document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");
        
        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        
        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only OR LicenseRef-2");
        AnyLicenseInfo firstLicense = LicenseInfoFactory.parseSPDXLicenseString("GPL-3.0-only WITH GPL-3.0-linking-exception AND LicenseRef-1");
        AnyLicenseInfo secondLicense = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-1 AND LicenseRef-2 OR LicenseRef-1 WITH another-exception");
        AnyLicenseInfo thirdLicense = LicenseInfoFactory.parseSPDXLicenseString("CERN-OHL-1.2 WITH u-boot-exception-2.0 AND LicenseRef-2 OR LicenseRef-1 WITH another-exception");
        
        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./package/foo.c", concludedLicense,
                        List.of(firstLicense, secondLicense, thirdLicense), "Copyright 2008-2010 John Smith", sha1Checksum)
                .build();
        
        document.getDocumentDescribes().add(file);
        
        return document;
    }
    
    @Test
    public void generateLicenseExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildLicenseExample();
        assertThat(doc.verify()).isEmpty();
        
        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testInput/generation/LicenseTest.xml"));
    }
    
    @Test
    public void compareLicenseExample() throws InvalidSPDXAnalysisException, IOException, InvalidFileNameException {
        var referenceDoc = buildLicenseExample();
        
        File inputFile = new File("testInput/generation/LicenseTest.xml");
        var inputDoc = SpdxToolsHelper.deserializeDocument(inputFile);
        
        assertThat(Comparisons.findDifferences(referenceDoc, inputDoc, false)).isEmpty();
    }
}
