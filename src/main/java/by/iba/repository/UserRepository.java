package by.iba.repository;

import by.iba.builder.UserBuilder;
import by.iba.exception.RepositoryException;
import by.iba.model.User;
import by.iba.repository.dbconstants.UserTableConstants;
import by.iba.repository.specification.Parameter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserRepository extends AbstractRepository<User> {
    private static final String TABLE_NAME = "users";

    public UserRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<User> query(String sqlString, Parameter parameter) throws RepositoryException {
        return executeQuery(sqlString, new UserBuilder(), parameter.getParameters());
    }

    @Override
    public Optional<User> queryForSingleResult(String sqlString, Parameter parameter)
            throws RepositoryException {
        List<User> users = query(sqlString, parameter);
        return users.size() == 1 ? Optional.of(users.get(0)) : Optional.empty();
    }

    @Override
    protected Map<String, Object> getFields(User user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(UserTableConstants.LOGIN.getFieldName(), user.getLogin());
        fields.put(UserTableConstants.PASSWORD.getFieldName(), user.getPassw());
        return fields;
    }
}
