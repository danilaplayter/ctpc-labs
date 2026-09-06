package org.jdbc_lab.prepared_statement.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.dao.CourseDao;
import org.jdbc_lab.prepared_statement.dao.EnrollmentDao;
import org.jdbc_lab.prepared_statement.dao.StudentDao;
import org.jdbc_lab.prepared_statement.model.Course;
import org.jdbc_lab.prepared_statement.model.Enrollment;
import org.jdbc_lab.prepared_statement.model.Student;
import org.jdbc_lab.test.BaseTest;
import org.jdbc_lab.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseEnrollmentActionTest extends BaseTest {

    private CourseEnrollmentAction action;
    private StudentDao studentDao;
    private CourseDao courseDao;
    private EnrollmentDao enrollmentDao;

    @BeforeEach
    void setUp() throws Exception {
        action = new CourseEnrollmentAction();
        studentDao = new StudentDao();
        courseDao = new CourseDao();
        enrollmentDao = new EnrollmentDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE enrollments RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE students RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE courses RESTART IDENTITY CASCADE");
        }
    }

    // ---- Тесты для курсов ----

    @Test
    void addCourse_shouldInsertCourse() {
        String input = "Java Basics\n5\nIntro to Java\n";
        TestUtils.provideInput(input);
        action.addCourse();

        List<Course> courses = courseDao.getAll();
        assertThat(courses).hasSize(1);
        Course c = courses.get(0);
        assertThat(c.getName()).isEqualTo("Java Basics");
        assertThat(c.getCredits()).isEqualTo(5);
        assertThat(c.getDescription()).isEqualTo("Intro to Java");
    }

    @Test
    void updateCourse_shouldUpdateExistingCourse() {
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Course saved = courseDao.getAll().get(0);
        Long id = saved.getId();

        String input = id + "\nAdvanced Java\n6\nAdvanced topics\n";
        TestUtils.provideInput(input);
        action.updateCourse();

        Course updated = courseDao.getById(id);
        assertThat(updated.getName()).isEqualTo("Advanced Java");
        assertThat(updated.getCredits()).isEqualTo(6);
        assertThat(updated.getDescription()).isEqualTo("Advanced topics");
    }

    @Test
    void updateCourse_shouldPrintNotFound_WhenCourseDoesNotExist() {
        // Передаём все поля, которые читает метод updateCourse (id, name, credits, description)
        String input = "999\nTestName\n5\nTestDesc\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.updateCourse());
        assertThat(output).contains("Курс не найден.");
    }

    @Test
    void deleteCourse_shouldDeleteExistingCourse() {
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Course saved = courseDao.getAll().get(0);
        Long id = saved.getId();

        String input = id + "\n";
        TestUtils.provideInput(input);
        action.deleteCourse();

        assertThat(courseDao.getAll()).isEmpty();
        assertThat(courseDao.getById(id)).isNull();
    }

    @Test
    void findCourseByName_shouldPrintMatchingCourses() {
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        courseDao.insert(new Course("Java Advanced", 6, "Advanced"));
        courseDao.insert(new Course("Python Basics", 4, "Python intro"));

        String input = "Java\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.findCourseByName());
        assertThat(output).contains("Java Basics", "Java Advanced").doesNotContain("Python");
    }

    @Test
    void findCourseByName_shouldPrintEmpty_WhenNoMatch() {
        String input = "XXX\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.findCourseByName());
        assertThat(output).contains("Курсы не найдены.");
    }

    @Test
    void getAllCourses_shouldPrintAllCourses() {
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        courseDao.insert(new Course("Python Basics", 4, "Python intro"));

        String output = TestUtils.captureOutput(() -> action.getAllCourses());
        assertThat(output).contains("Java Basics", "Python Basics");
    }

    @Test
    void getAllCourses_shouldPrintEmptyMessage_WhenNoCourses() {
        String output = TestUtils.captureOutput(() -> action.getAllCourses());
        assertThat(output).contains("Нет курсов.");
    }

    // ---- Тесты для зачислений ----

    @Test
    void enrollStudent_shouldInsertEnrollment() {
        // Создаём студента и курс
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Long studentId = studentDao.getAll().get(0).getId();
        Long courseId = courseDao.getAll().get(0).getId();
        Date today = Date.valueOf(LocalDate.now());

        String input = studentId + "\n" + courseId + "\n85\n" + today + "\n";
        TestUtils.provideInput(input);
        action.enrollStudent();

        List<Enrollment> enrollments = enrollmentDao.getAll();
        assertThat(enrollments).hasSize(1);
        Enrollment e = enrollments.get(0);
        assertThat(e.getStudentId()).isEqualTo(studentId);
        assertThat(e.getCourseId()).isEqualTo(courseId);
        assertThat(e.getGrade()).isEqualTo(85);
        assertThat(e.getEnrollmentDate()).isEqualTo(today);
    }

    @Test
    void enrollStudent_shouldPrintNotFound_WhenStudentDoesNotExist() {
        // Передаём все поля (studentId, courseId, grade, date)
        String input = "999\n85\n2026-09-04\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.enrollStudent());
        assertThat(output).contains("Студент не найден.");
    }

    @Test
    void enrollStudent_shouldPrintNotFound_WhenCourseDoesNotExist() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        Long studentId = studentDao.getAll().get(0).getId();

        // Передаём studentId, несуществующий courseId, grade и date
        String input = studentId + "\n999\n85\n2026-09-04\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.enrollStudent());
        assertThat(output).contains("Курс не найден.");
    }

    @Test
    void updateGrade_shouldUpdateExistingEnrollment() {
        // Создаём студента, курс и зачисление
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Long studentId = studentDao.getAll().get(0).getId();
        Long courseId = courseDao.getAll().get(0).getId();
        Date today = Date.valueOf(LocalDate.now());
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setGrade(80);
        enrollment.setEnrollmentDate(today);
        enrollmentDao.insert(enrollment);
        Long enrollmentId = enrollmentDao.getAll().get(0).getId();

        String input = enrollmentId + "\n95\n";
        TestUtils.provideInput(input);
        action.updateGrade();

        Enrollment updated = enrollmentDao.getById(enrollmentId);
        assertThat(updated.getGrade()).isEqualTo(95);
    }

    @Test
    void updateGrade_shouldPrintNotFound_WhenEnrollmentDoesNotExist() {
        // Передаём id и grade
        String input = "999\n95\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.updateGrade());
        assertThat(output).contains("Зачисление не найдено.");
    }

    @Test
    void deleteEnrollment_shouldDeleteExistingEnrollment() {
        // Создаём зачисление
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Long studentId = studentDao.getAll().get(0).getId();
        Long courseId = courseDao.getAll().get(0).getId();
        Date today = Date.valueOf(LocalDate.now());
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setGrade(80);
        enrollment.setEnrollmentDate(today);
        enrollmentDao.insert(enrollment);
        Long enrollmentId = enrollmentDao.getAll().get(0).getId();

        String input = enrollmentId + "\n";
        TestUtils.provideInput(input);
        action.deleteEnrollment();

        assertThat(enrollmentDao.getAll()).isEmpty();
        assertThat(enrollmentDao.getById(enrollmentId)).isNull();
    }

    @Test
    void findEnrollmentsByStudent_shouldPrintEnrollments() {
        // Создаём двух студентов, один курс, зачисление только на первого
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 30, "СПб", "petr@mail.ru"));
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Long studentId1 = studentDao.getAll().get(0).getId();
        Long studentId2 = studentDao.getAll().get(1).getId();
        Long courseId = courseDao.getAll().get(0).getId();
        Date today = Date.valueOf(LocalDate.now());
        Enrollment e = new Enrollment();
        e.setStudentId(studentId1);
        e.setCourseId(courseId);
        e.setGrade(85);
        e.setEnrollmentDate(today);
        enrollmentDao.insert(e);

        String input = studentId1 + "\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.findEnrollmentsByStudent());
        assertThat(output)
                .contains("studentId=" + studentId1)
                .doesNotContain("studentId=" + studentId2);
    }

    @Test
    void findEnrollmentsByStudent_shouldPrintEmpty_WhenNoEnrollments() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        Long studentId = studentDao.getAll().get(0).getId();

        String input = studentId + "\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.findEnrollmentsByStudent());
        assertThat(output).contains("Зачислений не найдено.");
    }

    @Test
    void findEnrollmentsByCourse_shouldPrintEnrollments() {
        // Создаём два курса, один студент, зачисление на первый курс
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        courseDao.insert(new Course("Python Basics", 4, "Python"));
        Long studentId = studentDao.getAll().get(0).getId();
        Long courseId1 = courseDao.getAll().get(0).getId();
        Long courseId2 = courseDao.getAll().get(1).getId();
        Date today = Date.valueOf(LocalDate.now());
        Enrollment e = new Enrollment();
        e.setStudentId(studentId);
        e.setCourseId(courseId1);
        e.setGrade(85);
        e.setEnrollmentDate(today);
        enrollmentDao.insert(e);

        String input = courseId1 + "\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.findEnrollmentsByCourse());
        assertThat(output)
                .contains("courseId=" + courseId1)
                .doesNotContain("courseId=" + courseId2);
    }

    @Test
    void findEnrollmentsByCourse_shouldPrintEmpty_WhenNoEnrollments() {
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        Long courseId = courseDao.getAll().get(0).getId();

        String input = courseId + "\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.findEnrollmentsByCourse());
        assertThat(output).contains("Зачислений не найдено.");
    }
}
