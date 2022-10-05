package org.spdx.testbed.generationTestCases;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.model.Annotation;
import org.spdx.library.model.Checksum;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.FileType;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;

import java.util.List;

public class GenerationFileTestCase extends GenerationTestCase {

    public SpdxDocument buildReferenceDocument() throws InvalidSPDXAnalysisException {
        var document = createSpdxDocumentWithBasicInfo("File test document");

        var modelStore = document.getModelStore();
        var documentUri = document.getDocumentUri();

        var annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: File Annotator")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("File level annotation")
                .setAnnotationType(AnnotationType.OTHER);

        var sha1Checksum = createSha1Checksum(modelStore, documentUri);
        var md5Checksum = Checksum.create(modelStore, documentUri, ChecksumAlgorithm.MD5, "624c1abb3664f4b35547e7c73864ad24");

        var concludedLicense = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only OR LicenseRef-2");
        var firstLicense = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");
        var secondLicense = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-2");

        var file = document.createSpdxFile("SPDXRef-somefile", "./package/foo.c", concludedLicense,
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
