package org.jdbc_lab.prepared_statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Course;
import org.jdbc_lab.prepared_statement.model.Enrollment;
import org.jdbc_lab.prepared_statement.model.Student;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnrollmentDaoTest extends BaseTest {

    private EnrollmentDao enrollmentDao;
    private StudentDao studentDao;
    private CourseDao courseDao;

    @BeforeEach
    void setUp() throws Exception {
        ConnectionManager.closeConnection();

        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("DELETE FROM enrollments");
            stmt.execute("DELETE FROM students");
            stmt.execute("DELETE FROM courses");
            stmt.execute("ALTER SEQUENCE enrollments_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE students_id_seq RESTART WITH 1");
            stmt.execute("ALTER SEQUENCE courses_id_seq RESTART WITH 1");
        }

        enrollmentDao = new EnrollmentDao();
        studentDao = new StudentDao();
        courseDao = new CourseDao();
    }

    private Long createTestStudent() {
        Student student = new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru");
        studentDao.insert(student);
        List<Student> students = studentDao.getAll();
        return students.get(students.size() - 1).getId();
    }

    private Long createTestCourse() {
        Course course = new Course("Java Basics", 5, "Intro");
        courseDao.insert(course);
        List<Course> courses = courseDao.getAll();
        return courses.get(courses.size() - 1).getId();
    }

    @Test
    void insertAndGetById_shouldWork() {
        Long studentId = createTestStudent();
        Long courseId = createTestCourse();
        Date date = Date.valueOf(LocalDate.now());

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setGrade(85);
        enrollment.setEnrollmentDate(date);
        enrollmentDao.insert(enrollment);

        List<Enrollment> all = enrollmentDao.getAll();
        assertThat(all).hasSize(1);

        Enrollment saved = all.get(0);
        assertThat(saved.getStudentId()).isEqualTo(studentId);
        assertThat(saved.getCourseId()).isEqualTo(courseId);
        assertThat(saved.getGrade()).isEqualTo(85);
        assertThat(saved.getEnrollmentDate()).isEqualTo(date);

        Enrollment byId = enrollmentDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void update_shouldUpdateEnrollment() {
        Long studentId = createTestStudent();
        Long courseId = createTestCourse();
        Date date = Date.valueOf(LocalDate.now());

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setGrade(85);
        enrollment.setEnrollmentDate(date);
        enrollmentDao.insert(enrollment);
        Enrollment saved = enrollmentDao.getAll().get(0);

        saved.setGrade(95);
        Date newDate = Date.valueOf(LocalDate.now().plusDays(1));
        saved.setEnrollmentDate(newDate);
        enrollmentDao.update(saved);

        Enrollment updated = enrollmentDao.getById(saved.getId());
        assertThat(updated.getGrade()).isEqualTo(95);
        assertThat(updated.getEnrollmentDate()).isEqualTo(newDate);
    }

    @Test
    void delete_shouldRemoveEnrollment() {
        Long studentId = createTestStudent();
        Long courseId = createTestCourse();
        Date date = Date.valueOf(LocalDate.now());

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setGrade(85);
        enrollment.setEnrollmentDate(date);
        enrollmentDao.insert(enrollment);
        Enrollment saved = enrollmentDao.getAll().get(0);

        enrollmentDao.delete(saved.getId());
        assertThat(enrollmentDao.getAll()).isEmpty();
        assertThat(enrollmentDao.getById(saved.getId())).isNull();
    }

    @Test
    void getByStudentId_shouldReturnEnrollmentsForStudent() {
        Long studentId1 = createTestStudent();
        Long studentId2 = createTestStudent();
        Long courseId = createTestCourse();
        Date date = Date.valueOf(LocalDate.now());

        Enrollment e1 = new Enrollment();
        e1.setStudentId(studentId1);
        e1.setCourseId(courseId);
        e1.setGrade(80);
        e1.setEnrollmentDate(date);
        enrollmentDao.insert(e1);

        Enrollment e2 = new Enrollment();
        e2.setStudentId(studentId2);
        e2.setCourseId(courseId);
        e2.setGrade(90);
        e2.setEnrollmentDate(date);
        enrollmentDao.insert(e2);

        List<Enrollment> found = enrollmentDao.getByStudentId(studentId1);
        assertThat(found).hasSize(1).extracting(Enrollment::getStudentId).containsOnly(studentId1);
    }

    @Test
    void getByCourseId_shouldReturnEnrollmentsForCourse() {
        Long studentId = createTestStudent();
        Long courseId1 = createTestCourse();
        Long courseId2 = createTestCourse();
        Date date = Date.valueOf(LocalDate.now());

        Enrollment e1 = new Enrollment();
        e1.setStudentId(studentId);
        e1.setCourseId(courseId1);
        e1.setGrade(80);
        e1.setEnrollmentDate(date);
        enrollmentDao.insert(e1);

        Enrollment e2 = new Enrollment();
        e2.setStudentId(studentId);
        e2.setCourseId(courseId2);
        e2.setGrade(90);
        e2.setEnrollmentDate(date);
        enrollmentDao.insert(e2);

        List<Enrollment> found = enrollmentDao.getByCourseId(courseId1);
        assertThat(found).hasSize(1).extracting(Enrollment::getCourseId).containsOnly(courseId1);
    }

    @Test
    void getById_shouldReturnNullForNonExistentId() {
        assertThat(enrollmentDao.getById(999L)).isNull();
    }
}
