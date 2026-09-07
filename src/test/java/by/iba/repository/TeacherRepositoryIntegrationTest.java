package by.iba.repository;

import static org.assertj.core.api.Assertions.assertThat;

import by.iba.exception.RepositoryException;
import by.iba.model.Teacher;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeacherRepositoryIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() throws SQLException {
        clearTables();
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO teachers (tname, subject, phone) VALUES ('Mr. Smith', 'Math',"
                            + " '111')");
            stmt.execute(
                    "INSERT INTO teachers (tname, subject, phone) VALUES ('Ms. Jones', 'Science',"
                            + " '222')");
        }
    }

    @Test
    void findAll() throws RepositoryException {
        TeacherRepository repository = new TeacherRepository(connection);
        List<Teacher> teachers = repository.findAll();
        assertThat(teachers).hasSize(2);
        assertThat(teachers)
                .extracting(Teacher::getName)
                .containsExactlyInAnyOrder("Mr. Smith", "Ms. Jones");
    }

    @Test
    void save() throws RepositoryException {
        TeacherRepository repository = new TeacherRepository(connection);
        Teacher newTeacher = new Teacher("Mr. Brown", "History", "333");
        Integer id = repository.save(newTeacher);
        assertThat(id).isNotNull();

        List<Teacher> teachers = repository.findAll();
        assertThat(teachers).hasSize(3);
        assertThat(teachers).extracting(Teacher::getName).contains("Mr. Brown");
    }

    @Test
    void query() throws RepositoryException {
        TeacherRepository repository = new TeacherRepository(connection);
        List<Teacher> teachers =
                repository.query("SELECT * FROM teachers WHERE subject = ?", () -> List.of("Math"));
        assertThat(teachers).hasSize(1);
        assertThat(teachers.get(0).getPhone()).isEqualTo("111");
    }

    @Test
    void queryForSingleResult() throws RepositoryException {
        TeacherRepository repository = new TeacherRepository(connection);
        Optional<Teacher> teacher =
                repository.queryForSingleResult(
                        "SELECT * FROM teachers WHERE tname = ?", () -> List.of("Ms. Jones"));
        assertThat(teacher).isPresent();
        assertThat(teacher.get().getSubject()).isEqualTo("Science");

        Optional<Teacher> empty =
                repository.queryForSingleResult(
                        "SELECT * FROM teachers WHERE tname = ?", () -> List.of("Unknown"));
        assertThat(empty).isEmpty();
    }
}
