package testValidations;

import org.junit.jupiter.api.Test;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.Version;
import org.spdx.library.model.*;
import org.spdx.library.model.enumerations.AnnotationType;
import org.spdx.library.model.enumerations.ChecksumAlgorithm;
import org.spdx.library.model.enumerations.Purpose;
import org.spdx.library.model.enumerations.ReferenceCategory;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PackageTest {

    public SpdxDocument buildPackageExample() throws InvalidSPDXAnalysisException {
        ISerializableModelStore modelStore = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.XML);
        String documentUri = "some_namespace";
        ModelCopyManager copyManager = new ModelCopyManager();

        //createSpdxDocument already sets creationInfo, dataLicense and specVersion to default values
        SpdxDocument document = SpdxModelFactory.createSpdxDocument(modelStore, documentUri, copyManager);

        document.setSpecVersion(Version.TWO_POINT_THREE_VERSION);
        document.setDataLicense(LicenseInfoFactory.parseSPDXLicenseString("CC0-1.0"));
        document.setCreationInfo(document.createCreationInfo(
                List.of("Tool: test-tool"), "2022-01-01T00:00:00Z"));
        document.setName("SPDX-tool-test");

        Annotation annotation = new Annotation(modelStore, documentUri, modelStore.getNextId(IModelStore.IdType.Anonymous, documentUri), null, true)
                .setAnnotator("Person: Package Commenter")
                .setAnnotationDate("2011-01-29T18:30:22Z")
                .setComment("Package level annotation")
                .setAnnotationType(AnnotationType.OTHER);

        Checksum sha1Checksum = document.createChecksum(ChecksumAlgorithm.SHA1, "d6a770ba38583ed4bb4525bd96e50461655d2758");
        Checksum md5Checksum = document.createChecksum(ChecksumAlgorithm.MD5, "624c1abb3664f4b35547e7c73864ad24");
        Checksum sha256Checksum = document.createChecksum(ChecksumAlgorithm.SHA256, "11b6d3ee554eedf79299905a98f9b9a04e498210b59f15094c916c91d150efcd");

        AnyLicenseInfo lgpl2_0onlyORLicenseRef_2 = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only OR LicenseRef-2");
        AnyLicenseInfo lgpl2_0onlyANDLicenseRef_2 = LicenseInfoFactory.parseSPDXLicenseString("LGPL-2.0-only AND LicenseRef-3");
        AnyLicenseInfo gpl2_0only = LicenseInfoFactory.parseSPDXLicenseString("GPL-2.0-only");
        AnyLicenseInfo licenseRef_2 = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-2");
        AnyLicenseInfo licenseRef_3 = LicenseInfoFactory.parseSPDXLicenseString("LicenseRef-3");

        SpdxFile file = document.createSpdxFile("SPDXRef-somefile", "./foo.txt", gpl2_0only,
                        List.of(), "Copyright 2022 some guy", sha1Checksum)
                .build();

        ExternalRef externalRef1 = document.createExternalRef(ReferenceCategory.SECURITY,
                new ReferenceType(new SimpleUriValue("cpe23Type")),
                "cpe:2.3:a:pivotal_software:spring_framework:4.1.0:*:*:*:*:*:*:*", null);
        ExternalRef externalRef2 = document.createExternalRef(ReferenceCategory.OTHER,
                new ReferenceType(new SimpleUriValue("http://spdx.org/spdxdocs/spdx-example-444504E0-4F89-41D3-9A0C-0305E82C3301#LocationRef-acmeforge")),
                "acmecorp/acmenator/4.1.3-alpha", "This is the external ref for Acme");

        SpdxPackageVerificationCode spdxPackageVerificationCode = document.createPackageVerificationCode("d6a770ba38583ed4bb4525bd96e50461655d2758", List.of("./package.spdx"));


        SpdxPackage spdxPackage = document.createPackage("SPDXRef-somepackage", "glibc", lgpl2_0onlyORLicenseRef_2,
                        "Copyright 2008-2010 John Smith", lgpl2_0onlyANDLicenseRef_2)
                .addAnnotation(annotation)
                .setVersionInfo("2.11.1")
                .setPackageFileName("glibc-2.11.1.tar.gz")
                .setSupplier("Person: Jane Doe (jane.doe@example.com)")
                .setOriginator("Organization: ExampleCodeInspect (contact@example.com)")
                .setDownloadLocation("http://ftp.gnu.org/gnu/glibc/glibc-ports-2.15.tar.gz")
                .setFilesAnalyzed(true)
                .setFiles(List.of(file))
                .setPackageVerificationCode(spdxPackageVerificationCode)
                .setChecksums(List.of(sha1Checksum, md5Checksum, sha256Checksum))
                .setHomepage("http://ftp.gnu.org/gnu/glibc")
                .setSourceInfo("uses glibc-2_11-branch from git://sourceware.org/git/glibc.git.")
                .setLicenseInfosFromFile(List.of(gpl2_0only, licenseRef_2, licenseRef_3))
                .setLicenseComments("The license for this project changed with the release of version x.y.  The version of the project included here post-dates the license change.")
                .setSummary("GNU C library.")
                .setDescription("The GNU C Library defines functions that are specified by the ISO C standard, as well as additional features specific to POSIX and other derivatives of the Unix operating system, and extensions specific to GNU systems.")
                .setComment("package comment")
                .setExternalRefs(List.of(externalRef1, externalRef2))
                .addAttributionText("The GNU C Library is free software.  See the file COPYING.LIB for copying conditions, and LICENSES for notices about a few contributions that require these additional notices to be distributed.  License copyright years may be listed using range notation, e.g., 1996-2015, indicating that every year in the range, inclusive, is a copyrightable year that would otherwise be listed individually.")
                .setPrimaryPurpose(Purpose.LIBRARY)
                .setReleaseDate("2012-01-29T18:30:22Z")
                .setBuiltDate("2011-01-29T18:30:22Z")
                .setValidUntilDate("2014-01-29T18:30:22Z")
                .build();

        document.getDocumentDescribes().add(spdxPackage);

        return document;
    }

    @Test
    public void generatePackageExample() throws InvalidSPDXAnalysisException, IOException {
        var doc = buildPackageExample();
        assertThat(doc.verify()).isEmpty();

        var modelStore = (ISerializableModelStore) doc.getModelStore();
        modelStore.serialize(doc.getDocumentUri(), new FileOutputStream("testOutput/generated/test.xml"));
    }
}
