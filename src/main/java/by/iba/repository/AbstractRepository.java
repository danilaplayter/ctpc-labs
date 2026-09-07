package by.iba.repository;

import by.iba.builder.BuildFactory;
import by.iba.builder.Builder;
import by.iba.exception.RepositoryException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractRepository<T> implements Repository<T> {

    private static final String GET_ALL_QUERY = "SELECT * FROM ";
    private final Connection connection;

    protected AbstractRepository(Connection connection) {
        this.connection = connection;
    }

    protected abstract String getTableName();

    protected abstract Map<String, Object> getFields(T obj);

    private static Integer getType(Object object) {
        if (object instanceof Integer) {
            return Types.INTEGER;
        }
        if (object instanceof Float) {
            return Types.FLOAT;
        }
        if (object instanceof String) {
            return Types.VARCHAR;
        }
        return Types.NULL;
    }

    public static void prepare(PreparedStatement preparedStatement, List<Object> parameters)
            throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            Object value = parameters.get(i);
            if (value == null) {
                preparedStatement.setNull(i + 1, getType(value));
            } else {
                preparedStatement.setObject(i + 1, value);
            }
        }
    }

    private static void prepareInsert(
            PreparedStatement preparedStatement, Map<String, Object> fields) throws SQLException {
        int i = 1;
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            if (entry.getKey().equals(SQLHelper.ID)) {
                continue;
            }
            Object value = entry.getValue();
            if (value == null) {
                preparedStatement.setNull(i++, getType(value));
            } else {
                preparedStatement.setObject(i++, value);
            }
        }
    }

    protected List<T> executeQuery(String sql, Builder<T> builder, List<Object> parameters)
            throws RepositoryException {
        List<T> objects = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            prepare(preparedStatement, parameters);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                objects.add(builder.build(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
        return objects;
    }

    protected Optional<T> executeQueryForSingleResult(
            String query, Builder<T> builder, List<Object> parameters) throws RepositoryException {
        List<T> items = executeQuery(query, builder, parameters);
        return items.size() == 1 ? Optional.of(items.get(0)) : Optional.empty();
    }

    @Override
    public Integer save(T object) throws RepositoryException {
        Map<String, Object> fields = getFields(object);
        String sql = SQLHelper.makeInsertQuery(fields, getTableName());
        return executeSave(sql, fields);
    }

    private Integer executeSave(String query, Map<String, Object> fields)
            throws RepositoryException {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            prepareInsert(preparedStatement, fields);
            log.info(preparedStatement.toString());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            Integer generatedId = null;
            while (resultSet.next()) {
                generatedId = resultSet.getInt(1);
            }
            return generatedId;
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage(), e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() throws RepositoryException {
        Builder<T> builder = (Builder<T>) BuildFactory.create(getTableName());
        String query = GET_ALL_QUERY + getTableName();
        return executeQuery(query, builder, Collections.emptyList());
    }
}
