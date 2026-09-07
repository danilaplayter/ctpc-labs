package by.iba.repository;

import static org.assertj.core.api.Assertions.assertThat;

import by.iba.exception.RepositoryException;
import by.iba.model.User;
import by.iba.repository.specification.UserByLogin;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @BeforeEach
    void setUp() throws SQLException {
        clearTables();
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO users (login, passw) VALUES ('alice', decode('616c696365',"
                            + " 'hex'))");
            stmt.execute(
                    "INSERT INTO users (login, passw) VALUES ('bob', decode('626f62', 'hex'))");
        }
    }

    @Test
    void findAll() throws RepositoryException {
        UserRepository repository = new UserRepository(connection);
        List<User> users = repository.findAll();
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin).containsExactlyInAnyOrder("alice", "bob");
    }

    @Test
    void save() throws RepositoryException {
        UserRepository repository = new UserRepository(connection);
        User newUser = new User("charlie", "charlie".getBytes());
        Integer id = repository.save(newUser);
        assertThat(id).isNotNull();

        List<User> users = repository.findAll();
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getLogin).contains("charlie");
    }

    @Test
    void query() throws RepositoryException {
        UserRepository repository = new UserRepository(connection);
        List<User> users =
                repository.query("SELECT * FROM users WHERE login = ?", new UserByLogin("alice"));
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getLogin()).isEqualTo("alice");
    }

    @Test
    void queryForSingleResult() throws RepositoryException {
        UserRepository repository = new UserRepository(connection);
        Optional<User> user =
                repository.queryForSingleResult(
                        "SELECT * FROM users WHERE login = ?", new UserByLogin("bob"));
        assertThat(user).isPresent();
        assertThat(user.get().getLogin()).isEqualTo("bob");

        Optional<User> empty =
                repository.queryForSingleResult(
                        "SELECT * FROM users WHERE login = ?", new UserByLogin("unknown"));
        assertThat(empty).isEmpty();
    }
}
