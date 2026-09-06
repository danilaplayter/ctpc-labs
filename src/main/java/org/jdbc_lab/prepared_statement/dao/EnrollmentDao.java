package org.jdbc_lab.prepared_statement.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Enrollment;

public class EnrollmentDao implements PreparedStatementDao<Enrollment> {
    private static final Logger logger = Logger.getLogger(EnrollmentDao.class.getName());

    @Override
    public Enrollment getById(Long id) {
        String query = "SELECT * FROM enrollments WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Enrollment e = new Enrollment();
                e.setId(rs.getLong("id"));
                e.setStudentId(rs.getLong("student_id"));
                e.setCourseId(rs.getLong("course_id"));
                e.setGrade(rs.getInt("grade"));
                e.setEnrollmentDate(rs.getDate("enrollment_date"));
                return e;
            }
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return null;
    }

    @Override
    public void insert(Enrollment enrollment) {
        String query =
                "INSERT INTO enrollments (student_id, course_id, grade, enrollment_date) VALUES (?,"
                        + " ?, ?, ?)";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, enrollment.getStudentId());
            ps.setLong(2, enrollment.getCourseId());
            ps.setInt(3, enrollment.getGrade());
            ps.setDate(4, enrollment.getEnrollmentDate());
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void update(Enrollment enrollment) {
        String query =
                "UPDATE enrollments SET student_id = ?, course_id = ?, grade = ?, enrollment_date ="
                        + " ? WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, enrollment.getStudentId());
            ps.setLong(2, enrollment.getCourseId());
            ps.setInt(3, enrollment.getGrade());
            ps.setDate(4, enrollment.getEnrollmentDate());
            ps.setLong(5, enrollment.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM enrollments WHERE id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<Enrollment> getAll() {
        List<Enrollment> list = new ArrayList<>();
        String query = "SELECT * FROM enrollments";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setId(rs.getLong("id"));
                e.setStudentId(rs.getLong("student_id"));
                e.setCourseId(rs.getLong("course_id"));
                e.setGrade(rs.getInt("grade"));
                e.setEnrollmentDate(rs.getDate("enrollment_date"));
                list.add(e);
            }
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return list;
    }

    public List<Enrollment> getByStudentId(Long studentId) {
        List<Enrollment> list = new ArrayList<>();
        String query = "SELECT * FROM enrollments WHERE student_id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setId(rs.getLong("id"));
                e.setStudentId(rs.getLong("student_id"));
                e.setCourseId(rs.getLong("course_id"));
                e.setGrade(rs.getInt("grade"));
                e.setEnrollmentDate(rs.getDate("enrollment_date"));
                list.add(e);
            }
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return list;
    }

    public List<Enrollment> getByCourseId(Long courseId) {
        List<Enrollment> list = new ArrayList<>();
        String query = "SELECT * FROM enrollments WHERE course_id = ?";
        try (PreparedStatement ps = ConnectionManager.getConnection().prepareStatement(query)) {
            ps.setLong(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Enrollment e = new Enrollment();
                e.setId(rs.getLong("id"));
                e.setStudentId(rs.getLong("student_id"));
                e.setCourseId(rs.getLong("course_id"));
                e.setGrade(rs.getInt("grade"));
                e.setEnrollmentDate(rs.getDate("enrollment_date"));
                list.add(e);
            }
        } catch (SQLException ex) {
            logger.severe("Ошибка: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return list;
    }
}
