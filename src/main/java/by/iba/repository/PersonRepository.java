package by.iba.repository;

import by.iba.builder.PersonBuilder;
import by.iba.exception.RepositoryException;
import by.iba.model.Person;
import by.iba.repository.dbconstants.PersonTableConstants;
import by.iba.repository.specification.Parameter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PersonRepository extends AbstractRepository<Person> {
    private static final String TABLE_NAME = "persons";

    public PersonRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<Person> query(String sqlString, Parameter parameter) throws RepositoryException {
        return executeQuery(sqlString, new PersonBuilder(), parameter.getParameters());
    }

    @Override
    public Optional<Person> queryForSingleResult(String sqlString, Parameter parameter)
            throws RepositoryException {
        List<Person> persons = query(sqlString, parameter);
        return persons.size() == 1 ? Optional.of(persons.get(0)) : Optional.empty();
    }

    @Override
    protected Map<String, Object> getFields(Person person) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(PersonTableConstants.NAME.getFieldName(), person.getName());
        fields.put(PersonTableConstants.PHONE.getFieldName(), person.getPhone());
        fields.put(PersonTableConstants.EMAIL.getFieldName(), person.getEmail());
        return fields;
    }
}
