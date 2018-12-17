package org.awesome.enums;

public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    Role(String value) {
        this.value = value;
    }
}
