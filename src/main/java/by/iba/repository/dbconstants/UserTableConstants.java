package by.iba.repository.dbconstants;

import lombok.Getter;

@Getter
public enum UserTableConstants {
    ID("id"),
    LOGIN("login"),
    PASSWORD("passw");

    private final String fieldName;

    UserTableConstants(String fieldName) {
        this.fieldName = fieldName;
    }
}
