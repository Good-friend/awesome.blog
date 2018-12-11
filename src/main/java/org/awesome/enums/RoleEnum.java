package org.awesome.enums;

public enum RoleEnum {
    ADMIN("ADMIN"),
    USER("USER");

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    RoleEnum(String value) {
        this.value = value;
    }
}
