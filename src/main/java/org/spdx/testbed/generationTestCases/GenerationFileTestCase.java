package org.spdx.testbed.generationTestCases;

import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
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

import java.util.List;

public class GenerationFileTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
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
                .addAnnotation(annotation)
                .addFileType(FileType.SOURCE)
                .addChecksum(md5Checksum)
                .setLicenseComments("The concluded license was taken from the package level that the file was included in.")
                .setComment("The concluded license was taken from the package level that the file was included in.\n" +
                        "      This information was found in the COPYING.txt file in the xyz directory.")
                .setNoticeText("Copyright (c) 2001 Aaron Lehmann aaroni@vitelus.com\n" +
                        "\n" +
                        "      Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the �Software�), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
                        "      The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
                        "\n" +
                        "      THE SOFTWARE IS PROVIDED �AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.")
                .setFileContributors(List.of("The Regents of the University of California",
                        "Modified by Paul Mundt lethal@linux-sh.org", "IBM Corporation"))
                .addAttributionText("file attribution")
                .build();

        document.getDocumentDescribes().add(file);

        return document;
    }
}
