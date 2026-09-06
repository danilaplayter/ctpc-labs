package org.jdbc_lab.prepared_statement.action;

import java.util.List;
import org.jdbc_lab.prepared_statement.dao.StudentDao;
import org.jdbc_lab.prepared_statement.model.Student;
import org.jdbc_lab.utils.InputManager;

public class PreparedStatementAction {
    StudentDao studentDao = new StudentDao();

    public void insertStudent() {
        Student student = Student.getStudentFromInput();
        studentDao.insert(student);
        System.out.println("Студент добавлен успешно!");
    }

    public void updateStudent() {
        System.out.print("Введи id студента: ");
        Student student = studentDao.getById(InputManager.getNextLong());
        if (student == null) {
            System.out.println("Студент с введённым id не найден!");
            return;
        }
        Student newStudent = Student.getStudentFromInput();
        newStudent.setId(student.getId());
        studentDao.update(newStudent);
        System.out.println("Студент обновлён успешно!");
    }

    public void deleteStudent() {
        System.out.print("Введи id студента: ");
        Student student = studentDao.getById(InputManager.getNextLong());
        if (student == null) {
            System.out.println("Студент с введённым id не найден!");
            return;
        }
        studentDao.delete(student.getId());
        System.out.println("Студент удалён успешно!");
    }

    public void getStudentById() {
        System.out.print("Введи id студента: ");
        Student student = studentDao.getById(InputManager.getNextLong());
        if (student == null) {
            System.out.println("Студент с введённым id не найден!");
        } else {
            System.out.println("[Студент по id]\n" + student);
        }
    }

    public void getStudentsByFirstName() {
        System.out.print("Введи имя (или его часть): ");
        List<Student> students = studentDao.getByFirstName(InputManager.getNextLine());
        printStudents(students, "по имени");
    }

    public void getStudentsByLastName() {
        System.out.print("Введи фамилию (или её часть): ");
        List<Student> students = studentDao.getByLastName(InputManager.getNextLine());
        printStudents(students, "по фамилии");
    }

    public void getStudentsByCity() {
        System.out.print("Введи название города (или его часть): ");
        List<Student> students = studentDao.getByCity(InputManager.getNextLine());
        printStudents(students, "по городу");
    }

    public void getStudentsByEmail() {
        System.out.print("Введи электронную почту (или её часть): ");
        List<Student> students = studentDao.getByEmail(InputManager.getNextLine());
        printStudents(students, "по email");
    }

    public void getStudentsByAge() {
        int start, end;
        while (true) {
            System.out.print("Введи начальный возраст для поиска: ");
            start = InputManager.getNextIntInRange(17, 71);
            System.out.print("Введи конечный возраст для поиска: ");
            end = InputManager.getNextIntInRange(17, 71);
            if (start > end) {
                System.out.println("Начальный возраст не может быть > конечного!");
            } else {
                break;
            }
        }
        List<Student> students = studentDao.getByAge(start, end);
        printStudents(students, "по возрасту от " + start + " до " + end);
    }

    public void getAllStudents() {
        List<Student> students = studentDao.getAll();
        if (students.isEmpty()) {
            System.out.println("Нет ни одного студента!");
        } else {
            System.out.println("[Все студенты]");
            students.forEach(System.out::println);
        }
    }

    private void printStudents(List<Student> students, String filter) {
        if (students.isEmpty()) {
            System.out.println("Не найдено ни одного студента " + filter + "!");
        } else {
            System.out.println("[Студенты " + filter + "]");
            students.forEach(System.out::println);
        }
    }
}
