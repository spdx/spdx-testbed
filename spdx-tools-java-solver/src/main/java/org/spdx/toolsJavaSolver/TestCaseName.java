// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.toolsJavaSolver;

public enum TestCaseName {

  GENERATION_MINIMAL("generationMinimalTest"),
  GENERATION_BASELINE_SBOM("generationBaselineSbomTest"),
  GENERATION_DOCUMENT("generationDocumentTest"),
  GENERATION_PACKAGE("generationPackageTest"),
  GENERATION_FILE("generationFileTest"),
  GENERATION_SNIPPET("generationSnippetTest"),
  GENERATION_LICENSE("generationLicenseTest"),
  GENERATION_RELATIONSHIP("generationRelationshipTest");

  private final String fullName;

  TestCaseName(String name) {
    this.fullName = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public static TestCaseName fromString(String str) {
    for (var testCaseName : TestCaseName.values()) {
      if (testCaseName.getFullName().equals(str)) {
        return testCaseName;
      }
    }
    throw new IllegalArgumentException("Unknown test case name: " + str);
  }
}
