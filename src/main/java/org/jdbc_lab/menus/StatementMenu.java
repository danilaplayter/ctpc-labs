package org.jdbc_lab.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.jdbc_lab.statement.action.StatementAction;
import org.jdbc_lab.utils.InputManager;
import org.jdbc_lab.utils.MenuUtils;

public class StatementMenu {
    private static final Logger logger = Logger.getLogger(StatementMenu.class.getName());
    private final StatementAction statementAction = new StatementAction();

    public void print() {
        List<String> options = new ArrayList<>();
        options.add("Очистить все таблицы");
        options.add("Просмотреть все записи (с выборками)");
        options.add("Добавить дефолтную информацию");
        options.add("Обновить название случайной книги в магазине");
        options.add("Обновить имена авторов по магазину");

        final String header = MenuUtils.getHeader("Меню Statement", options);

        while (true) {
            System.out.print(header);
            try {
                switch (InputManager.getNextInt()) {
                    case 1 -> statementAction.deleteAllInfo();
                    case 2 -> statementAction.getAllInfo();
                    case 3 -> statementAction.addDefaultInfo();
                    case 4 -> statementAction.updateRandomBookInShop();
                    case 5 -> statementAction.updateAuthorsNamesByShop();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Такого варианта нет!");
                }
            } catch (Exception e) {
                logger.severe("Ошибка при обработке меню Statement: " + e.getMessage());
            }
        }
    }
}
