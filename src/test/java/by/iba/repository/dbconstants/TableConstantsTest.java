package by.iba.repository.dbconstants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TableConstantsTest {

    @Test
    void personTableConstants() {
        assertThat(PersonTableConstants.ID.getFieldName()).isEqualTo("id");
        assertThat(PersonTableConstants.NAME.getFieldName()).isEqualTo("pname");
        assertThat(PersonTableConstants.PHONE.getFieldName()).isEqualTo("phone");
        assertThat(PersonTableConstants.EMAIL.getFieldName()).isEqualTo("email");
    }

    @Test
    void userTableConstants() {
        assertThat(UserTableConstants.ID.getFieldName()).isEqualTo("id");
        assertThat(UserTableConstants.LOGIN.getFieldName()).isEqualTo("login");
        assertThat(UserTableConstants.PASSWORD.getFieldName()).isEqualTo("passw");
    }

    @Test
    void teacherTableConstants() {
        assertThat(TeacherTableConstants.ID.getFieldName()).isEqualTo("id");
        assertThat(TeacherTableConstants.NAME.getFieldName()).isEqualTo("tname");
        assertThat(TeacherTableConstants.SUBJECT.getFieldName()).isEqualTo("subject");
        assertThat(TeacherTableConstants.PHONE.getFieldName()).isEqualTo("phone");
    }
}
