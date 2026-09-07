package by.iba.builder;

import by.iba.exception.RepositoryException;
import java.sql.ResultSet;

public interface Builder<T> {
    T build(ResultSet resultSet) throws RepositoryException;
}
