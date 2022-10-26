// SPDX-FileCopyrightText: TNG Technology Consulting GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.spdx.testbed;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.spdx.testbed.util.json.Difference;

@Builder
public class TestResult {

  Boolean success;

  @Builder.Default
  List<Difference> differences = new ArrayList<>();
}
