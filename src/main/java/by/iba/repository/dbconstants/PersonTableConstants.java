package by.iba.repository.dbconstants;

import lombok.Getter;

@Getter
public enum PersonTableConstants {
    ID("id"),
    NAME("pname"),
    PHONE("phone"),
    EMAIL("email");

    private final String fieldName;

    PersonTableConstants(String fieldName) {
        this.fieldName = fieldName;
    }
}
