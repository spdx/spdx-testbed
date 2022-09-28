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
import org.spdx.library.model.enumerations.RelationshipType;
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

public class RelationshipTest {

    private SpdxDocument buildRelationshipExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "https://some.namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setName("SPDX-tool-test");

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");

        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only");

        SpdxFile fileA = document.createSpdxFile("SPDXRef-fileA", "./fileA.c", concludedLicense,
                        List.of(), "Copyright 2022 some person", sha1Checksum)
                .build();

        SpdxFile fileB = document.createSpdxFile("SPDXRef-fileB", "./fileB.c", concludedLicense,
                        List.of(), "Copyright 2022 some person", sha1Checksum)
                .build();

        document.getDocumentDescribes().add(fileA);
        document.getDocumentDescribes().add(fileB);

        for (RelationshipType relationshipType : RelationshipType.values()) {
            if (relationshipType == RelationshipType.MISSING) {
                continue;
            }
            fileB.addRelationship(
                    document.createRelationship(
                            fileA, relationshipType, String.format("comment on %s", relationshipType.name())));

        }

        return document;
    }

    @Test
    public void generateRelationshipExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildRelationshipExample();
        assertThat(doc.verify()).isEmpty();

        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testOutput/generated/test.xml"));
    }

    @Test
    public void compareRelationshipExample() throws InvalidSPDXAnalysisException, IOException, InvalidFileNameException {
        var referenceDoc = buildRelationshipExample();

        File inputFile = new File("testOutput/generated/test.xml");
        var inputDoc = SpdxToolsHelper.deserializeDocument(inputFile);

        assertThat(Comparisons.findDifferences(referenceDoc, inputDoc, false)).isEmpty();
    }
}
