package by.iba.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import by.iba.model.User;
import by.iba.repository.specification.UserByLogin;
import by.iba.repository.specification.UserByLoginPassword;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserRepositoryUnitTest {

    @Test
    void queryAndQueryForSingleResult() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("login")).thenReturn("user1");
        when(rs.getBytes("passw")).thenReturn("hash".getBytes());

        UserRepository repository = new UserRepository(connection);
        List<User> users =
                repository.query("SELECT ...", new UserByLoginPassword("user1", "hash".getBytes()));
        assertThat(users).hasSize(1);

        PreparedStatement ps2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps2);
        when(ps2.executeQuery()).thenReturn(rs2);
        when(rs2.next()).thenReturn(true, false);
        when(rs2.getInt("id")).thenReturn(1);
        when(rs2.getString("login")).thenReturn("user1");
        when(rs2.getBytes("passw")).thenReturn("hash".getBytes());

        Optional<User> single =
                repository.queryForSingleResult("SELECT ...", new UserByLogin("user1"));
        assertThat(single).isPresent();
    }

    @Test
    void save() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet generatedKeys = mock(ResultSet.class);

        when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true, false);
        when(generatedKeys.getInt(1)).thenReturn(77);

        UserRepository repository = new UserRepository(connection);
        User user = new User("user2", "pass".getBytes());
        Integer id = repository.save(user);
        assertThat(id).isEqualTo(77);
    }
}
