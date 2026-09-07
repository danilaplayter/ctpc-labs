package by.iba.repository.specification;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ParameterTest {

    @Test
    void userByLogin() {
        assertThat(new UserByLogin("login").getParameters()).containsExactly("login");
    }

    @Test
    void userByLoginPassword() {
        byte[] password = {1, 2, 3};
        assertThat(new UserByLoginPassword("login", password).getParameters())
                .containsExactly("login", password);
    }

    @Test
    void personInsert() {
        assertThat(new PersonInsert("name", "phone", "email").getParameters())
                .containsExactly("name", "phone", "email");
    }

    @Test
    void teacherInsert() {
        assertThat(new TeacherInsert("name", "subject", "phone").getParameters())
                .containsExactly("name", "subject", "phone");
    }
}
