package org.jdbc_lab.prepared_statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Course;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseDaoTest extends BaseTest {

    private CourseDao courseDao;

    @BeforeEach
    void setUp() throws Exception {
        courseDao = new CourseDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE enrollments RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE students RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE courses RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void insertAndGetById_shouldWork() {
        Course course = new Course();
        course.setName("Java Basics");
        course.setCredits(5);
        course.setDescription("Introduction to Java");
        courseDao.insert(course);

        List<Course> all = courseDao.getAll();
        assertThat(all).hasSize(1);

        Course saved = all.get(0);
        assertThat(saved.getName()).isEqualTo("Java Basics");
        assertThat(saved.getCredits()).isEqualTo(5);
        assertThat(saved.getDescription()).isEqualTo("Introduction to Java");

        Course byId = courseDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void update_shouldUpdateCourse() {
        Course course = new Course();
        course.setName("Java Basics");
        course.setCredits(5);
        course.setDescription("Introduction to Java");
        courseDao.insert(course);
        Course saved = courseDao.getAll().get(0);

        saved.setName("Advanced Java");
        saved.setCredits(6);
        saved.setDescription("Advanced Java topics");
        courseDao.update(saved);

        Course updated = courseDao.getById(saved.getId());
        assertThat(updated.getName()).isEqualTo("Advanced Java");
        assertThat(updated.getCredits()).isEqualTo(6);
        assertThat(updated.getDescription()).isEqualTo("Advanced Java topics");
    }

    @Test
    void delete_shouldRemoveCourse() {
        Course course = new Course();
        course.setName("Java Basics");
        course.setCredits(5);
        course.setDescription("Introduction to Java");
        courseDao.insert(course);
        Course saved = courseDao.getAll().get(0);

        courseDao.delete(saved.getId());
        assertThat(courseDao.getAll()).isEmpty();
        assertThat(courseDao.getById(saved.getId())).isNull();
    }

    @Test
    void getByName_shouldReturnMatchingCourses() {
        courseDao.insert(new Course("Java Basics", 5, "Intro"));
        courseDao.insert(new Course("Java Advanced", 6, "Advanced"));
        courseDao.insert(new Course("Python Basics", 4, "Python intro"));

        List<Course> found = courseDao.getByName("Java");
        assertThat(found)
                .hasSize(2)
                .extracting(Course::getName)
                .containsExactlyInAnyOrder("Java Basics", "Java Advanced");
    }

    @Test
    void getById_shouldReturnNullForNonExistentId() {
        assertThat(courseDao.getById(999L)).isNull();
    }
}
