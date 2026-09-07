package by.iba.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import by.iba.model.Person;
import by.iba.repository.specification.PersonInsert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PersonRepositoryUnitTest {

    @Test
    void queryAndQueryForSingleResult() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getInt("id")).thenReturn(1);
        when(rs.getString("pname")).thenReturn("Ivan");
        when(rs.getString("phone")).thenReturn("111");
        when(rs.getString("email")).thenReturn("a@b.com");

        PersonRepository repository = new PersonRepository(connection);
        List<Person> persons =
                repository.query("SELECT ...", new PersonInsert("Ivan", "111", "a@b.com"));
        assertThat(persons).hasSize(1);

        PreparedStatement ps2 = mock(PreparedStatement.class);
        ResultSet rs2 = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(ps2);
        when(ps2.executeQuery()).thenReturn(rs2);
        when(rs2.next()).thenReturn(true, false);
        when(rs2.getInt("id")).thenReturn(1);
        when(rs2.getString("pname")).thenReturn("Ivan");
        when(rs2.getString("phone")).thenReturn("111");
        when(rs2.getString("email")).thenReturn("a@b.com");

        Optional<Person> single =
                repository.queryForSingleResult(
                        "SELECT ...", new PersonInsert("Ivan", "111", "a@b.com"));
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
        when(generatedKeys.getInt(1)).thenReturn(42);

        PersonRepository repository = new PersonRepository(connection);
        Person person = new Person("Petr", "222", "c@d.com");
        Integer id = repository.save(person);
        assertThat(id).isEqualTo(42);
    }
}
