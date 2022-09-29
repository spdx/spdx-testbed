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
import org.spdx.library.model.license.ExtractedLicenseInfo;
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

public class ExtractedLicenseInfoTest {
    
    private SpdxDocument buildExtractedLicenseInfoExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();
        
        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);
        
        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setCreationInfo(document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");
        
        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        
        ExtractedLicenseInfo extractedLicenseInfo = new ExtractedLicenseInfo(modelStore, documentUri, "LicenseRef-1", copyManager, true);
        extractedLicenseInfo.setExtractedText("some extracted text");
        extractedLicenseInfo.setName("some extracted license info name");
        extractedLicenseInfo.setComment("extracted license info comment");
        extractedLicenseInfo.setSeeAlso(List.of("some (cross reference/see also) url", "another url"));
        
        //TODO: can you set a crossRef on an extractedLicenseInfo? Currently this is accomplished via seeAlso, a collection of Strings
        var crossRef = document.createCrossRef("some cross reference url").build();

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
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testInput/generation/ExtractedLicenseInfoTest.xml"));
    }
    
    @Test
    public void compareExtractedLicenseInfoExample() throws InvalidSPDXAnalysisException, IOException, InvalidFileNameException {
        var referenceDoc = buildExtractedLicenseInfoExample();
        
        File inputFile = new File("testInput/generation/ExtractedLicenseInfoTest.xml");
        var inputDoc = SpdxToolsHelper.deserializeDocument(inputFile);
        
        assertThat(Comparisons.findDifferences(referenceDoc, inputDoc, false)).isEmpty();
    }
}
