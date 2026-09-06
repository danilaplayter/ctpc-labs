package org.jdbc_lab.prepared_statement.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Statement;
import java.util.List;
import org.jdbc_lab.connection.ConnectionManager;
import org.jdbc_lab.prepared_statement.dao.StudentDao;
import org.jdbc_lab.prepared_statement.model.Student;
import org.jdbc_lab.test.BaseTest;
import org.jdbc_lab.test.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreparedStatementActionTest extends BaseTest {

    private PreparedStatementAction action;
    private StudentDao studentDao;

    @BeforeEach
    void setUp() throws Exception {
        action = new PreparedStatementAction();
        studentDao = new StudentDao();
        try (Statement stmt = ConnectionManager.getConnection().createStatement()) {
            stmt.execute("TRUNCATE TABLE enrollments RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE students RESTART IDENTITY CASCADE");
            stmt.execute("TRUNCATE TABLE courses RESTART IDENTITY CASCADE");
        }
    }

    @Test
    void insertStudent_shouldAddStudent() {
        String input = "Иван\nИванов\n25\nМосква\nivan@mail.ru\n";
        TestUtils.provideInput(input);
        action.insertStudent();

        List<Student> students = studentDao.getAll();
        assertThat(students).hasSize(1);
        Student s = students.get(0);
        assertThat(s.getFirstName()).isEqualTo("Иван");
        assertThat(s.getLastName()).isEqualTo("Иванов");
        assertThat(s.getAge()).isEqualTo(25);
        assertThat(s.getCity()).isEqualTo("Москва");
        assertThat(s.getEmail()).isEqualTo("ivan@mail.ru");
    }

    @Test
    void updateStudent_shouldUpdateExistingStudent() {
        // Сначала добавляем студента
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        Student saved = studentDao.getAll().get(0);
        Long id = saved.getId();

        String input = id + "\nПётр\nПетров\n30\nСПб\npetr@mail.ru\n";
        TestUtils.provideInput(input);
        action.updateStudent();

        Student updated = studentDao.getById(id);
        assertThat(updated.getFirstName()).isEqualTo("Пётр");
        assertThat(updated.getLastName()).isEqualTo("Петров");
        assertThat(updated.getAge()).isEqualTo(30);
        assertThat(updated.getCity()).isEqualTo("СПб");
        assertThat(updated.getEmail()).isEqualTo("petr@mail.ru");
    }

    @Test
    void updateStudent_shouldPrintNotFound_WhenStudentDoesNotExist() {
        // Передаём все поля, которые читает метод updateStudent
        String input = "999\nИван\nИванов\n25\nМосква\nivan@mail.ru\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.updateStudent());
        assertThat(output).contains("Студент с введённым id не найден!");
    }

    @Test
    void deleteStudent_shouldDeleteExistingStudent() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        Student saved = studentDao.getAll().get(0);
        Long id = saved.getId();

        String input = id + "\n";
        TestUtils.provideInput(input);
        action.deleteStudent();

        assertThat(studentDao.getAll()).isEmpty();
        assertThat(studentDao.getById(id)).isNull();
    }

    @Test
    void deleteStudent_shouldPrintNotFound_WhenStudentDoesNotExist() {
        String input = "999\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.deleteStudent());
        assertThat(output).contains("Студент с введённым id не найден!");
    }

    @Test
    void getStudentById_shouldPrintStudent_WhenExists() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        Student saved = studentDao.getAll().get(0);

        String input = saved.getId() + "\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentById());
        assertThat(output).contains("Иван", "Иванов", "Москва", "ivan@mail.ru");
    }

    @Test
    void getStudentById_shouldPrintNotFound_WhenStudentDoesNotExist() {
        String input = "999\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentById());
        assertThat(output).contains("Студент с введённым id не найден!");
    }

    @Test
    void getStudentsByFirstName_shouldPrintMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Иван", "Петров", 30, "СПб", "ivanp@mail.ru"));
        studentDao.insert(new Student("Пётр", "Сидоров", 20, "Киев", "petr@mail.ru"));

        String input = "Ив\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentsByFirstName());
        assertThat(output).contains("Иван", "Иванов", "Петров").doesNotContain("Пётр");
    }

    @Test
    void getStudentsByFirstName_shouldPrintEmpty_WhenNoMatch() {
        String input = "XXX\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentsByFirstName());
        assertThat(output).contains("Не найдено ни одного студента по имени!");
    }

    @Test
    void getStudentsByLastName_shouldPrintMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Иванов", 30, "СПб", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 20, "Таганрог", "chekhov@mail.ru"));

        String input = "Ива\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentsByLastName());
        assertThat(output).contains("Иван", "Иванов", "Пётр").doesNotContain("Чехов");
    }

    @Test
    void getStudentsByCity_shouldPrintMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 30, "Москва", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 20, "Таганрог", "chekhov@mail.ru"));

        String input = "Мос\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentsByCity());
        assertThat(output).contains("Иван", "Пётр").doesNotContain("Чехов");
    }

    @Test
    void getStudentsByEmail_shouldPrintMatchingStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 30, "Москва", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 20, "Таганрог", "chekhov@mail.ru"));

        String input = "mail.ru\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentsByEmail());
        assertThat(output).contains("Иван", "Пётр", "Чехов");
    }

    @Test
    void getStudentsByAge_shouldPrintStudentsInRange() {
        studentDao.insert(new Student("Иван", "Иванов", 18, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 25, "Москва", "petr@mail.ru"));
        studentDao.insert(new Student("Антон", "Чехов", 30, "Таганрог", "chekhov@mail.ru"));

        String input = "20\n29\n";
        TestUtils.provideInput(input);
        String output = TestUtils.captureOutput(() -> action.getStudentsByAge());
        assertThat(output).contains("Пётр").doesNotContain("Иван", "Чехов");
    }

    @Test
    void getAllStudents_shouldPrintAllStudents() {
        studentDao.insert(new Student("Иван", "Иванов", 25, "Москва", "ivan@mail.ru"));
        studentDao.insert(new Student("Пётр", "Петров", 30, "Москва", "petr@mail.ru"));

        String output = TestUtils.captureOutput(() -> action.getAllStudents());
        assertThat(output).contains("Иван", "Пётр");
    }

    @Test
    void getAllStudents_shouldPrintEmptyMessage_WhenNoStudents() {
        String output = TestUtils.captureOutput(() -> action.getAllStudents());
        assertThat(output).contains("Нет ни одного студента!");
    }
}
