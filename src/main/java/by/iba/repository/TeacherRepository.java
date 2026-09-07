package by.iba.repository;

import by.iba.builder.TeacherBuilder;
import by.iba.exception.RepositoryException;
import by.iba.model.Teacher;
import by.iba.repository.dbconstants.TeacherTableConstants;
import by.iba.repository.specification.Parameter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TeacherRepository extends AbstractRepository<Teacher> {
    private static final String TABLE_NAME = "teachers";

    public TeacherRepository(Connection connection) {
        super(connection);
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public List<Teacher> query(String sqlString, Parameter parameter) throws RepositoryException {
        return executeQuery(sqlString, new TeacherBuilder(), parameter.getParameters());
    }

    @Override
    public Optional<Teacher> queryForSingleResult(String sqlString, Parameter parameter)
            throws RepositoryException {
        List<Teacher> teachers = query(sqlString, parameter);
        return teachers.size() == 1 ? Optional.of(teachers.get(0)) : Optional.empty();
    }

    @Override
    protected Map<String, Object> getFields(Teacher teacher) {
        Map<String, Object> fields = new HashMap<>();
        fields.put(TeacherTableConstants.NAME.getFieldName(), teacher.getName());
        fields.put(TeacherTableConstants.SUBJECT.getFieldName(), teacher.getSubject());
        fields.put(TeacherTableConstants.PHONE.getFieldName(), teacher.getPhone());
        return fields;
    }
}
