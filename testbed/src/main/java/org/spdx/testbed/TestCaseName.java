package org.spdx.testbed;

public enum TestCaseName {

    GENERATION_MINIMAL("generationMinimalTest"),
    GENERATION_BASELINESBOM("generationBaselineSbomTest"),
    GENERATION_DOCUMENT("generationDocumentTest"),
    GENERATION_PACKAGE("generationPackageTest"),
    GENERATION_FILE("generationFileTest"),
    GENERATION_SNIPPET("generationSnippetTest"),
    GENERATION_LICENSE("generationLicenseTest"),
    GENERATION_RELATIONSHIP("generationRelationshipTest"),
    GENERATION_EXTRACTEDLICENSEINFO("generationExtractedLicenseInfoTest");

    private final String name;

    TestCaseName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static TestCaseName fromString(String str) {
        for (var testCaseName : TestCaseName.values()){
            if (testCaseName.name.equals(str)){
                return testCaseName;
            }
        }
        throw new IllegalArgumentException("Unknown test case name: " + str);
    }
}
