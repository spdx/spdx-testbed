package generation;

import org.junit.jupiter.api.Test;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SnippetTest {

    private SpdxDocument buildSnippetExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setDataLicense(LicenseInfoFactory.parseSPDXLicenseString("CC0-1.0"));
        document.setCreationInfo(document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");

        Annotation annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: Snippet Commenter")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("Snippet level annotation")
                .setAnnotationType(AnnotationType.OTHER);

        Checksum sha1Checksum = document.createChecksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");

        AnyLicenseInfo lgpl2_0onlyANDLicenseRef_2 = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only AND LicenseRef-3");
        AnyLicenseInfo gpl2_0only = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");

        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", gpl2_0only,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        SpdxSnippet spdxSnippet = document.createSpdxSnippet("SPDXRef-somesnippet", "from linux kernel", gpl2_0only,
                        List.of(lgpl2_0onlyANDLicenseRef_2), "Copyright 2008-2010 John Smith", file, 100, 400)
                .addAnnotation(annotation)
                .setLineRange(30, 40)
                .setLicenseComments("snippy license comment")
                .setComment("snippy comment")
//                .addAttributionText("The GNU C Library is free software.  See the file COPYING.LIB for copying conditions, and LICENSES for notices about a few contributions that require these additional notices to be distributed.  License copyright years may be listed using range notation, e.g., 1996-2015, indicating that every year in the range, inclusive, is a copyrightable year that would otherwise be listed individually.")
                .build();

        document.getDocumentDescribes().add(spdxSnippet);

        return document;
    }

    @Test
    public void generateSnippetExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildSnippetExample();
        assertThat(doc.verify()).isEmpty();

        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testOutput/generated/test.xml"));
    }
}
