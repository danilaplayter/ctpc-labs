package org.jdbc_lab.prepared_statement.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Student;

public class StudentDao implements PreparedStatementDao<Student> {
    private static final Logger logger = Logger.getLogger(StudentDao.class.getName());

    @Override
    public Student getById(Long id) {
        Student student = null;
        String query = "SELECT * FROM students WHERE id = ? LIMIT 1";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                student = new Student();
                student.setId(rs.getLong("id"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setAge(rs.getInt("age"));
                student.setCity(rs.getString("city"));
                student.setEmail(rs.getString("email"));
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return student;
    }

    @Override
    public void insert(Student student) {
        String query =
                "INSERT INTO students (first_name, last_name, age, city, email) VALUES (?, ?, ?, ?,"
                        + " ?)";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setInt(3, student.getAge());
            ps.setString(4, student.getCity());
            ps.setString(5, student.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Student student) {
        String query =
                "UPDATE students SET first_name = ?, last_name = ?, age = ?, city = ?, email = ?"
                        + " WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());
            ps.setInt(3, student.getAge());
            ps.setString(4, student.getCity());
            ps.setString(5, student.getEmail());
            ps.setLong(6, student.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getAll() {
        String query = "SELECT * FROM students";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            return getStudentListByResultSet(rs);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Student> getByFirstName(String firstName) {
        String query = "SELECT * FROM students WHERE first_name LIKE ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, firstName + "%");
            ResultSet rs = ps.executeQuery();
            return getStudentListByResultSet(rs);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Student> getByLastName(String lastName) {
        String query = "SELECT * FROM students WHERE last_name LIKE ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, lastName + "%");
            ResultSet rs = ps.executeQuery();
            return getStudentListByResultSet(rs);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Student> getByCity(String city) {
        String query = "SELECT * FROM students WHERE city LIKE ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, city + "%");
            ResultSet rs = ps.executeQuery();
            return getStudentListByResultSet(rs);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Student> getByEmail(String email) {
        String query = "SELECT * FROM students WHERE email LIKE ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, "%" + email + "%");
            ResultSet rs = ps.executeQuery();
            return getStudentListByResultSet(rs);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Student> getByAge(int start, int end) {
        String query = "SELECT * FROM students WHERE age >= ? AND age <= ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setInt(1, start);
            ps.setInt(2, end);
            ResultSet rs = ps.executeQuery();
            return getStudentListByResultSet(rs);
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private List<Student> getStudentListByResultSet(ResultSet rs) throws SQLException {
        List<Student> students = new ArrayList<>();
        while (rs.next()) {
            Student s = new Student();
            s.setId(rs.getLong("id"));
            s.setFirstName(rs.getString("first_name"));
            s.setLastName(rs.getString("last_name"));
            s.setAge(rs.getInt("age"));
            s.setCity(rs.getString("city"));
            s.setEmail(rs.getString("email"));
            students.add(s);
        }
        return students;
    }
}
