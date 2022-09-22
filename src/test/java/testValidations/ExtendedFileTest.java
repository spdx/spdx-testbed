package testValidations;

import org.junit.jupiter.api.Test;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.FileType;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.spdx.storage.simple.StoredTypedItem;
import org.spdx.tools.InvalidFileNameException;
import org.spdx.tools.SpdxToolsHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedFileTest {

    public SpdxDocument buildExtendedFileExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        //createSpdxDocument already sets creationInfo, dataLicense and specVersion to default values
        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_TWO_VERSION);
        document.setDataLicense(LicenseInfoFactory.parseSPDXLicenseString("CC0-1.0"));
        document.setCreationInfo(document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");

        Annotation annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: File Commenter")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("File level annotation")
                .setAnnotationType(AnnotationType.OTHER);

        Checksum sha1Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        Checksum md5Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.MD5, "624c1abb3664f4b35547e7c73864ad24");

        AnyLicenseInfo concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only OR LicenseRef-2");
        AnyLicenseInfo firstLicense = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");
        AnyLicenseInfo secondLicense = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-2");

        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./package/foo.c", concludedLicense,
                        List.of(firstLicense, secondLicense), "Copyright 2008-2010 John Smith", sha1Checksum)
                .addChecksum(md5Checksum)
                .setComment("The concluded license was taken from the package level that the file was included in.\n" +
                        "      This information was found in the COPYING.txt file in the xyz directory.")
                .setLicenseComments("The concluded license was taken from the package level that the file was included in.")
                .addAnnotation(annotation)
                .addFileType(FileType.SOURCE)
                .setNoticeText("Copyright (c) 2001 Aaron Lehmann aaroni@vitelus.com\n" +
                        "\n" +
                        "      Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the �Software�), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
                        "      The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
                        "\n" +
                        "      THE SOFTWARE IS PROVIDED �AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.")
                .setFileContributors(List.of("The Regents of the University of California",
                        "Modified by Paul Mundt lethal@linux-sh.org", "IBM Corporation"))
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }

    @Test
    public void generatedExtendedExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildExtendedFileExample();
        assertThat(doc.verify()).isEmpty();

        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testOutput/generated/test.xml"));
    }

    @Test
    public void correctExtendedFileExample() throws IOException, InvalidFileNameException {
        String filepath = "/home/armin/IdeaProjects/spdx-testbed/SPDXDocumentExamples/playgroundFiles/SPDXXML_extendedFileExample-v2.2.spdx.xml";
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

            var annotation = (ArrayList) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_ANNOTATION);
            var annotationId = ((TypedValue) annotation.get(0)).getId();
            var annotator = modelStore.getValue(documentUri, annotationId, SpdxConstants.PROP_ANNOTATOR);
            var annotationDate = modelStore.getValue(documentUri, annotationId, SpdxConstants.PROP_ANNOTATION_DATE);
            var annotationType = modelStore.getValue(documentUri, annotationId, SpdxConstants.PROP_ANNOTATION_TYPE);
            var annotationComment = modelStore.getValue(documentUri, annotationId, SpdxConstants.RDFS_PROP_COMMENT);

            var checksum = (ArrayList) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_CHECKSUM);
            var checksumId = ((TypedValue) checksum.get(0)).getId();
            var checksumNumber = (String) modelStore.getValue(documentUri, checksumId, SpdxConstants.PROP_CHECKSUM_VALUE).get();
            var checksumAlgorithm = (SimpleUriValue) modelStore.getValue(documentUri, checksumId, SpdxConstants.PROP_CHECKSUM_ALGORITHM).get();

            var fileContributor = (ArrayList<String>) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_CONTRIBUTOR);
            var fileComment = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.RDFS_PROP_COMMENT);
            var copyrightText = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_COPYRIGHT_TEXT);
            var fileName = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_NAME);
            var fileType = (ArrayList<SimpleUriValue>) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_TYPE);
            var noticeText = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_NOTICE);
            var licenseInfoInFiles = (ArrayList<TypedValue>) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_FILE_SEEN_LICENSE);
            var licenceComment = (String) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_LIC_COMMENTS);
            var licenseConcluded = (TypedValue) spdxFileStoredTypedItem.getValue(SpdxConstants.PROP_LICENSE_CONCLUDED);
            var licenses = (ModelCollection) modelStore.getValue(documentUri, licenseConcluded.getId(), SpdxConstants.PROP_LICENSE_SET_MEMEBER).get();
            var licenseIds = (ArrayList<String>) licenses.stream().map(item -> ((ModelObject) item).getId()).collect(Collectors.toList());


            assertThat(fileName).isEqualTo("./package/foo.c");
            assertThat(licenseIds).containsExactly("LGPL-2.0-only", "LicenseRef-2");
            assertThat(checksumAlgorithm.getIndividualURI()).isEqualTo(ChecksumAlgorithm.SHA1.getIndividualURI());
            assertThat(checksumNumber).isEqualTo("d6a770ba38583ed4bb4525bd96e50461655d2758");
            assertThat(copyrightText).isEqualTo("Copyright 2008-2010 John Smith");

        } catch (InvalidSPDXAnalysisException e) {
            throw new RuntimeException(e);
        }
    }
}
