// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;

public interface TestCase extends Comparable<TestCase> {

  TestResult test(String inputFile) throws InvalidSPDXAnalysisException, IOException,
      InvalidFileNameException;

  String getName();

  default int compareTo(@Nonnull TestCase other) {
    return getName().compareTo(other.getName());
  }
}
