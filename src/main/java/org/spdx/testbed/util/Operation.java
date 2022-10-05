package org.spdx.testbed.util;

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

    public String getRfcName() {
        return this.rfcName;
    }
}
