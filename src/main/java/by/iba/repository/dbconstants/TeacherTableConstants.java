package by.iba.repository.dbconstants;

import lombok.Getter;

@Getter
public enum TeacherTableConstants {
    ID("id"),
    NAME("tname"),
    SUBJECT("subject"),
    PHONE("phone");

    private final String fieldName;

    TeacherTableConstants(String fieldName) {
        this.fieldName = fieldName;
    }
}
