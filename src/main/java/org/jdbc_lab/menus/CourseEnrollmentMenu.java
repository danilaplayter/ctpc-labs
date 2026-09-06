package org.jdbc_lab.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.prepared_statement.action.CourseEnrollmentAction;
import org.jdbc_lab.utils.InputManager;
import org.jdbc_lab.utils.MenuUtils;

public class CourseEnrollmentMenu {
    private static final Logger logger = Logger.getLogger(CourseEnrollmentMenu.class.getName());
    private final CourseEnrollmentAction action = new CourseEnrollmentAction();

    public void print() {
        List<String> options = new ArrayList<>();
        options.add("Добавить курс");
        options.add("Обновить курс");
        options.add("Удалить курс");
        options.add("Найти курс по имени");
        options.add("Все курсы");
        options.add("Зачислить студента на курс");
        options.add("Обновить оценку в зачислении");
        options.add("Удалить зачисление");
        options.add("Найти зачисления по студенту");
        options.add("Найти зачисления по курсу");

        String header = MenuUtils.getHeader("Курсы и зачисления", options);

        while (true) {
            System.out.print(header);
            try {
                switch (InputManager.getNextInt()) {
                    case 1 -> action.addCourse();
                    case 2 -> action.updateCourse();
                    case 3 -> action.deleteCourse();
                    case 4 -> action.findCourseByName();
                    case 5 -> action.getAllCourses();
                    case 6 -> action.enrollStudent();
                    case 7 -> action.updateGrade();
                    case 8 -> action.deleteEnrollment();
                    case 9 -> action.findEnrollmentsByStudent();
                    case 10 -> action.findEnrollmentsByCourse();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Неверный выбор.");
                }
            } catch (Exception e) {
                logger.severe("Ошибка: " + e.getMessage());
            }
        }
    }
}
