package org.jdbc_lab.prepared_statement.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Course;

public class CourseDao implements PreparedStatementDao<Course> {
    private static final Logger logger = Logger.getLogger(CourseDao.class.getName());

    @Override
    public Course getById(Long id) {
        String query = "SELECT * FROM courses WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Course c = new Course();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setCredits(rs.getInt("credits"));
                c.setDescription(rs.getString("description"));
                return c;
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void insert(Course course) {
        String query = "INSERT INTO courses (name, credits, description) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, course.getName());
            ps.setInt(2, course.getCredits());
            ps.setString(3, course.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Course course) {
        String query = "UPDATE courses SET name = ?, credits = ?, description = ? WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, course.getName());
            ps.setInt(2, course.getCredits());
            ps.setString(3, course.getDescription());
            ps.setLong(4, course.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        String query = "SELECT * FROM courses";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setCredits(rs.getInt("credits"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Course> getByName(String namePart) {
        List<Course> list = new ArrayList<>();
        String query = "SELECT * FROM courses WHERE name LIKE ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setString(1, "%" + namePart + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course c = new Course();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setCredits(rs.getInt("credits"));
                c.setDescription(rs.getString("description"));
                list.add(c);
            }
        } catch (SQLException e) {
            logger.severe("Ошибка: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return list;
    }
}
