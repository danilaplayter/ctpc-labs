package by.iba.builder;

import by.iba.exception.RepositoryException;
import by.iba.model.Teacher;
import by.iba.repository.dbconstants.TeacherTableConstants;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherBuilder implements Builder<Teacher> {
    @Override
    public Teacher build(ResultSet resultSet) throws RepositoryException {
        try {
            int id = resultSet.getInt(TeacherTableConstants.ID.getFieldName());
            String name = resultSet.getString(TeacherTableConstants.NAME.getFieldName());
            String subject = resultSet.getString(TeacherTableConstants.SUBJECT.getFieldName());
            String phone = resultSet.getString(TeacherTableConstants.PHONE.getFieldName());
            return new Teacher(id, name, subject, phone);
        } catch (SQLException exception) {
            throw new RepositoryException(exception.getMessage(), exception);
        }
    }
}
