package by.iba.repository;

import by.iba.connection.ConnectionPool;
import java.sql.Connection;

public class RepositoryCreator implements AutoCloseable {
    private final ConnectionPool connectionPool;
    private final Connection connection;

    public RepositoryCreator() {
        connectionPool = ConnectionPool.getInstance();
        connection = connectionPool.getConnection();
    }

    public UserRepository getUserRepository() {
        return new UserRepository(connection);
    }

    public PersonRepository getPersonRepository() {
        return new PersonRepository(connection);
    }

    public TeacherRepository getTeacherRepository() {
        return new TeacherRepository(connection);
    }

    @Override
    public void close() {
        connectionPool.releaseConnection(connection);
    }
}
