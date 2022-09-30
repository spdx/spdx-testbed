package org.spdx.testbed;

import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.tools.InvalidFileNameException;

import java.io.IOException;

public interface TestCase {
    int test(String[] args) throws InvalidSPDXAnalysisException, IOException, InvalidFileNameException;
}
