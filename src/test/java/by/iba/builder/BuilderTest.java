package by.iba.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import by.iba.exception.RepositoryException;
import by.iba.model.Person;
import by.iba.model.Teacher;
import by.iba.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class BuilderTest {

    @Test
    void userBuilderBuildsUser() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("login")).thenReturn("login");
        when(rs.getBytes("passw")).thenReturn(new byte[] {1, 2});

        User user = new UserBuilder().build(rs);

        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getLogin()).isEqualTo("login");
    }

    @Test
    void userBuilderWrapsSqlException() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenThrow(new SQLException("boom"));

        assertThatThrownBy(() -> new UserBuilder().build(rs))
                .isInstanceOf(RepositoryException.class)
                .hasMessageContaining("boom");
    }

    @Test
    void personBuilderBuildsPerson() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("pname")).thenReturn("name");
        when(rs.getString("phone")).thenReturn("phone");
        when(rs.getString("email")).thenReturn("email");

        Person person = new PersonBuilder().build(rs);

        assertThat(person.getName()).isEqualTo("name");
    }

    @Test
    void personBuilderWrapsSqlException() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenThrow(new SQLException("boom"));

        assertThatThrownBy(() -> new PersonBuilder().build(rs))
                .isInstanceOf(RepositoryException.class);
    }

    @Test
    void teacherBuilderBuildsTeacher() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("tname")).thenReturn("name");
        when(rs.getString("subject")).thenReturn("subject");
        when(rs.getString("phone")).thenReturn("phone");

        Teacher teacher = new TeacherBuilder().build(rs);

        assertThat(teacher.getSubject()).isEqualTo("subject");
    }

    @Test
    void teacherBuilderWrapsSqlException() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getInt("id")).thenThrow(new SQLException("boom"));

        assertThatThrownBy(() -> new TeacherBuilder().build(rs))
                .isInstanceOf(RepositoryException.class);
    }

    @Test
    void buildFactoryCreatesCorrectBuilders() {
        assertThat(BuildFactory.create("users")).isInstanceOf(UserBuilder.class);
        assertThat(BuildFactory.create("persons")).isInstanceOf(PersonBuilder.class);
        assertThat(BuildFactory.create("teachers")).isInstanceOf(TeacherBuilder.class);
    }

    @Test
    void buildFactoryThrowsForUnknownName() {
        assertThatThrownBy(() -> BuildFactory.create("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown Builder name!");
    }
}
