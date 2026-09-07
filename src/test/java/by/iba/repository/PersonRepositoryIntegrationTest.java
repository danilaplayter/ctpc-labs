package by.iba.repository;

import static org.assertj.core.api.Assertions.assertThat;

import by.iba.exception.RepositoryException;
import by.iba.model.Person;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonRepositoryIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() throws SQLException {
        clearTables();
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO persons (pname, phone, email) VALUES ('John', '123', 'j@j.com')");
            stmt.execute(
                    "INSERT INTO persons (pname, phone, email) VALUES ('Jane', '456', 'ja@j.com')");
        }
    }

    @Test
    void findAll() throws RepositoryException {
        PersonRepository repository = new PersonRepository(connection);
        List<Person> persons = repository.findAll();
        assertThat(persons).hasSize(2);
        assertThat(persons).extracting(Person::getName).containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void save() throws RepositoryException {
        PersonRepository repository = new PersonRepository(connection);
        Person newPerson = new Person("Bob", "789", "b@b.com");
        Integer id = repository.save(newPerson);
        assertThat(id).isNotNull();

        List<Person> persons = repository.findAll();
        assertThat(persons).hasSize(3);
        assertThat(persons).extracting(Person::getName).contains("Bob");
    }

    @Test
    void query() throws RepositoryException {
        PersonRepository repository = new PersonRepository(connection);
        List<Person> persons =
                repository.query("SELECT * FROM persons WHERE pname = ?", () -> List.of("John"));
        assertThat(persons).hasSize(1);
        assertThat(persons.get(0).getPhone()).isEqualTo("123");
    }

    @Test
    void queryForSingleResult() throws RepositoryException {
        PersonRepository repository = new PersonRepository(connection);
        Optional<Person> person =
                repository.queryForSingleResult(
                        "SELECT * FROM persons WHERE pname = ?", () -> List.of("Jane"));
        assertThat(person).isPresent();
        assertThat(person.get().getEmail()).isEqualTo("ja@j.com");

        Optional<Person> empty =
                repository.queryForSingleResult(
                        "SELECT * FROM persons WHERE pname = ?", () -> List.of("Unknown"));
        assertThat(empty).isEmpty();
    }
}
