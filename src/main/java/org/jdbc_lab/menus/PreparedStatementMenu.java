package org.jdbc_lab.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.prepared_statement.action.PreparedStatementAction;
import org.jdbc_lab.utils.InputManager;
import org.jdbc_lab.utils.MenuUtils;

public class PreparedStatementMenu {
    private static final Logger logger = Logger.getLogger(PreparedStatementMenu.class.getName());
    private final PreparedStatementAction preparedStatementAction = new PreparedStatementAction();
    private final CourseEnrollmentMenu courseEnrollmentMenu = new CourseEnrollmentMenu();

    public void print() {
        List<String> options = new ArrayList<>();
        options.add("Добавить студента");
        options.add("Обновить студента");
        options.add("Удалить студента");
        options.add("Найти по id");
        options.add("Найти по имени");
        options.add("Найти по фамилии");
        options.add("Найти по городу");
        options.add("Найти по email");
        options.add("Найти по возрасту");
        options.add("Отобразить всех студентов");
        options.add("Работа с курсами и зачислениями");

        final String header = MenuUtils.getHeader("Меню PreparedStatement", options);

        while (true) {
            System.out.print(header);
            try {
                switch (InputManager.getNextInt()) {
                    case 1 -> preparedStatementAction.insertStudent();
                    case 2 -> preparedStatementAction.updateStudent();
                    case 3 -> preparedStatementAction.deleteStudent();
                    case 4 -> preparedStatementAction.getStudentById();
                    case 5 -> preparedStatementAction.getStudentsByFirstName();
                    case 6 -> preparedStatementAction.getStudentsByLastName();
                    case 7 -> preparedStatementAction.getStudentsByCity();
                    case 8 -> preparedStatementAction.getStudentsByEmail();
                    case 9 -> preparedStatementAction.getStudentsByAge();
                    case 10 -> preparedStatementAction.getAllStudents();
                    case 11 -> courseEnrollmentMenu.print();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Такого варианта нет!");
                }
            } catch (Exception e) {
                logger.severe("Ошибка при обработке меню PreparedStatement: " + e.getMessage());
            }
        }
    }
}
