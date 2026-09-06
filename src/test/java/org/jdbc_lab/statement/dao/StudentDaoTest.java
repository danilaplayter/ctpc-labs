package org.jdbc_lab.prepared_statement.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.model.Student;
import org.jdbc_lab.test.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentDaoTest extends BaseTest {

    private StudentDao studentDao;

    @BeforeEach
    void setUp() throws Exception {
        studentDao = new StudentDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE enrollments RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE students RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE courses RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void insertAndGetById_shouldWork() {
        Student student = new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru");
        studentDao.insert(student);

        List<Student> all = studentDao.getAll();
        assertThat(all).hasSize(1);

        Student saved = all.get(0);
        assertThat(saved.getFirstName()).isEqualTo("Иван");
        assertThat(saved.getLastName()).isEqualTo("Иванов");
        assertThat(saved.getAge()).isEqualTo(25);
        assertThat(saved.getCity()).isEqualTo("Москва");
        assertThat(saved.getEmail()).isEqualTo("ivan@mail.ru");

        Student byId = studentDao.getById(saved.getId());
        assertThat(byId).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void update_shouldUpdateStudent() {
        Student student = new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru");
        studentDao.insert(student);
        Student saved = studentDao.getAll().get(0);

        saved.setFirstName("Пётр");
        saved.setLastName("Петров");
        saved.setAge(30);
        saved.setCity("Санкт-Петербург");
        saved.setEmail("petr@mail.ru");
        studentDao.update(saved);

        Student updated = studentDao.getById(saved.getId());
        assertThat(updated.getFirstName()).isEqualTo("Пётр");
        assertThat(updated.getLastName()).isEqualTo("Петров");
        assertThat(updated.getAge()).isEqualTo(30);
        assertThat(updated.getCity()).isEqualTo("Санкт-Петербург");
        assertThat(updated.getEmail()).isEqualTo("petr@mail.ru");
    }

    @Test
    void delete_shouldRemoveStudent() {
        Student student = new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru");
        studentDao.insert(student);
        Student saved = studentDao.getAll().get(0);

        studentDao.delete(saved.getId());
        assertThat(studentDao.getAll()).isEmpty();
        assertThat(studentDao.getById(saved.getId())).isNull();
    }

    @Test
    void getByFirstName_shouldReturnMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Иван", "Петров", 30, "СПб", "ivanp@mail.ru"));
        studentDao.insert(new Student("Пётр", "Сидоров", 20, "Киев", "petr@mail.ru"));

        List<Student> found = studentDao.getByFirstName("Ив");
        assertThat(found).hasSize(2).extracting(Student::getFirstName).containsOnly("Иван", "Иван");
    }

    @Test
    void getByLastName_shouldReturnMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Иванов", 30, "СПб", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 20, "Таганрог", "chekhov@mail.ru"));

        List<Student> found = studentDao.getByLastName("Ивано");
        assertThat(found)
                .hasSize(2)
                .extracting(Student::getLastName)
                .containsOnly("Иванов", "Иванов");
    }

    @Test
    void getByCity_shouldReturnMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 30, "Москва", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 20, "Таганрог", "chekhov@mail.ru"));

        List<Student> found = studentDao.getByCity("Мос");
        assertThat(found).hasSize(2).extracting(Student::getCity).containsOnly("Москва", "Москва");
    }

    @Test
    void getByEmail_shouldReturnMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 30, "Москва", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 20, "Таганрог", "chekhov@mail.ru"));

        List<Student> found = studentDao.getByEmail("mail.ru");
        assertThat(found).hasSize(3);
    }

    @Test
    void getByAge_shouldReturnStudentsInRange() {
        studentDao.insert(new Student("Иван", "Иванов", 18, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 25, "Москва", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 30, "Таганрог", "chekhov@mail.ru"));

        List<Student> found = studentDao.getByAge(20, 29);
        assertThat(found).hasSize(1).extracting(Student::getAge).containsOnly(25);
    }

    @Test
    void getById_shouldReturnNullForNonExistentId() {
        assertThat(studentDao.getById(999L)).isNull();
    }
}
