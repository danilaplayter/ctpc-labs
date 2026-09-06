package org.jdbc_lab.prepared_statement.action;

import java.sql.Date;
import java.util.List;
import org.jdbc_lab.prepared_statement.dao.CourseDao;
import org.jdbc_lab.prepared_statement.dao.EnrollmentDao;
import org.jdbc_lab.prepared_statement.dao.StudentDao;
import org.jdbc_lab.prepared_statement.model.Course;
import org.jdbc_lab.prepared_statement.model.Enrollment;
import org.jdbc_lab.prepared_statement.model.Student;
import org.jdbc_lab.utils.InputManager;

public class CourseEnrollmentAction {
    private final CourseDao courseDao = new CourseDao();
    private final EnrollmentDao enrollmentDao = new EnrollmentDao();
    private final StudentDao studentDao = new StudentDao();

    // ---- Действия с курсами ----

    public void addCourse() {
        Course c = new Course();
        System.out.print("Введите название курса: ");
        c.setName(InputManager.getNextLine());
        System.out.print("Введите количество кредитов: ");
        c.setCredits(InputManager.getNextInt());
        System.out.print("Введите описание: ");
        c.setDescription(InputManager.getNextLine());
        courseDao.insert(c);
        System.out.println("Курс добавлен.");
    }

    public void updateCourse() {
        System.out.print("Введите ID курса для обновления: ");
        Long id = InputManager.getNextLong();
        Course c = courseDao.getById(id);
        if (c == null) {
            System.out.println("Курс не найден.");
            return;
        }
        System.out.print("Новое название (оставьте пустым, чтобы не менять): ");
        String name = InputManager.getNextLine();
        if (!name.isEmpty()) c.setName(name);
        System.out.print("Новое количество кредитов (0, чтобы не менять): ");
        int credits = InputManager.getNextInt();
        if (credits != 0) c.setCredits(credits);
        System.out.print("Новое описание (оставьте пустым, чтобы не менять): ");
        String desc = InputManager.getNextLine();
        if (!desc.isEmpty()) c.setDescription(desc);
        courseDao.update(c);
        System.out.println("Курс обновлён.");
    }

    public void deleteCourse() {
        System.out.print("Введите ID курса для удаления: ");
        Long id = InputManager.getNextLong();
        courseDao.delete(id);
        System.out.println("Курс удалён (если существовал).");
    }

    public void findCourseByName() {
        System.out.print("Введите часть названия: ");
        String part = InputManager.getNextLine();
        List<Course> list = courseDao.getByName(part);
        if (list.isEmpty()) {
            System.out.println("Курсы не найдены.");
        } else {
            list.forEach(System.out::println);
        }
    }

    public void getAllCourses() {
        List<Course> list = courseDao.getAll();
        if (list.isEmpty()) {
            System.out.println("Нет курсов.");
        } else {
            list.forEach(System.out::println);
        }
    }

    // ---- Действия с зачислениями ----

    public void enrollStudent() {
        // Проверим существование студента и курса
        System.out.print("Введите ID студента: ");
        Long studentId = InputManager.getNextLong();
        Student s = studentDao.getById(studentId);
        if (s == null) {
            System.out.println("Студент не найден.");
            return;
        }
        System.out.print("Введите ID курса: ");
        Long courseId = InputManager.getNextLong();
        Course c = courseDao.getById(courseId);
        if (c == null) {
            System.out.println("Курс не найден.");
            return;
        }
        Enrollment e = new Enrollment();
        e.setStudentId(studentId);
        e.setCourseId(courseId);
        System.out.print("Введите оценку (0-100): ");
        e.setGrade(InputManager.getNextIntInRange(0, 100));
        System.out.print("Введите дату зачисления (ГГГГ-ММ-ДД): ");
        String dateStr = InputManager.getNextLine();
        e.setEnrollmentDate(Date.valueOf(dateStr));
        enrollmentDao.insert(e);
        System.out.println("Зачисление выполнено.");
    }

    public void updateGrade() {
        System.out.print("Введите ID зачисления: ");
        Long enrollmentId = InputManager.getNextLong();
        Enrollment e = enrollmentDao.getById(enrollmentId);
        if (e == null) {
            System.out.println("Зачисление не найдено.");
            return;
        }
        System.out.print("Введите новую оценку (0-100): ");
        e.setGrade(InputManager.getNextIntInRange(0, 100));
        enrollmentDao.update(e);
        System.out.println("Оценка обновлена.");
    }

    public void deleteEnrollment() {
        System.out.print("Введите ID зачисления для удаления: ");
        Long id = InputManager.getNextLong();
        enrollmentDao.delete(id);
        System.out.println("Зачисление удалено (если существовало).");
    }

    public void findEnrollmentsByStudent() {
        System.out.print("Введите ID студента: ");
        Long studentId = InputManager.getNextLong();
        List<Enrollment> list = enrollmentDao.getByStudentId(studentId);
        printEnrollments(list);
    }

    public void findEnrollmentsByCourse() {
        System.out.print("Введите ID курса: ");
        Long courseId = InputManager.getNextLong();
        List<Enrollment> list = enrollmentDao.getByCourseId(courseId);
        printEnrollments(list);
    }

    private void printEnrollments(List<Enrollment> list) {
        if (list.isEmpty()) {
            System.out.println("Зачислений не найдено.");
        } else {
            list.forEach(System.out::println);
        }
    }
}
