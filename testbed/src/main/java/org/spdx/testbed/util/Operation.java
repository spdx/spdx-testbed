package org.spdx.testbed.util;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Operation {
    ADD("add"),
    REMOVE("remove"),
    MOVE("move"),
    REPLACE("replace"),
    COPY("copy");

    private String rfcName;

    Operation(String rfcName) {
        this.rfcName = rfcName;
    }

    @JsonValue
    public String getRfcName() {
        return this.rfcName;
    }
}
