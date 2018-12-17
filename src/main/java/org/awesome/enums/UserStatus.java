package org.awesome.enums;

public enum UserStatus {
    ENABLE("1"),
    LOCKD("2"),
    EXPIRED("3");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    UserStatus(String value) {
        this.value = value;
    }
}
