package by.iba.repository;

import by.iba.repository.dbconstants.UserTableConstants;
import java.util.Map;

public class SQLHelper {
    public static final String ID = "id";
    private static final String INSERT_QUERY = "INSERT INTO ";
    private static final String VALUES = " VALUES ";
    private static final String USER_TABLE = "users";
    private static final String PERSON_TABLE = "persons";
    private static final String TEACHER_TABLE = "teachers";

    public static final String SQL_GET_USER =
            "SELECT "
                    + UserTableConstants.ID.getFieldName()
                    + ", "
                    + UserTableConstants.LOGIN.getFieldName()
                    + ", "
                    + UserTableConstants.PASSWORD.getFieldName()
                    + " FROM "
                    + USER_TABLE
                    + " WHERE "
                    + UserTableConstants.LOGIN.getFieldName()
                    + " = ? AND "
                    + UserTableConstants.PASSWORD.getFieldName()
                    + " = ?";

    public static final String SQL_CHECK_LOGIN =
            "SELECT "
                    + UserTableConstants.LOGIN.getFieldName()
                    + " FROM "
                    + USER_TABLE
                    + " WHERE "
                    + UserTableConstants.LOGIN.getFieldName()
                    + " = ?";

    public static String makeInsertQuery(Map<String, Object> fields, String table) {
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String column = entry.getKey();
            if (column.equals(ID)) {
                continue;
            }
            columns.append(column).append(", ");
            values.append("?, ");
        }
        values.setLength(values.length() - 2);
        columns.setLength(columns.length() - 2);
        values.append(")");
        columns.append(")");
        return INSERT_QUERY + table + columns + VALUES + values + ";";
    }
}
