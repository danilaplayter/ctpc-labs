package org.jdbc_lab.statement.action;

import java.util.*;
import java.util.logging.Logger;
import org.jdbc_lab.statement.dao.AuthorDao;
import org.jdbc_lab.statement.dao.BookDao;
import org.jdbc_lab.statement.dao.ShopBookDao;
import org.jdbc_lab.statement.dao.ShopDao;
import org.jdbc_lab.statement.model.Author;
import org.jdbc_lab.statement.model.Book;
import org.jdbc_lab.statement.model.Shop;
import org.jdbc_lab.statement.model.ShopBook;
import org.jdbc_lab.utils.InputManager;

public class StatementAction {
    private static final Logger logger = Logger.getLogger(StatementAction.class.getName());

    AuthorDao authorDao = new AuthorDao();
    BookDao bookDao = new BookDao();
    ShopDao shopDao = new ShopDao();
    ShopBookDao shopBookDao = new ShopBookDao();

    public void deleteAllInfo() {
        System.out.print("Подтвердите удаление всей информации (y / Y): ");
        final String choice = InputManager.getNextLine();
        if (!choice.equals("Y") && !choice.equals("y")) {
            System.out.println("Удаление было отменено!");
            return;
        }
        try {
            shopBookDao.clearTable();
            bookDao.clearTable();
            authorDao.clearTable();
            shopDao.clearTable();
            System.out.println("Информация из таблиц была успешно удалена!");
        } catch (RuntimeException e) {
            logger.severe("При удалении информации из таблиц ошибка: " + e.getMessage());
        }
    }

    public void addDefaultInfo() {
        try {
            if (authorDao.getCount() != 0
                    || bookDao.getCount() != 0
                    || shopDao.getCount() != 0
                    || shopBookDao.getCount() != 0) {
                System.out.println("Таблицы не пусты. Вставка информации по умолчанию невозможна!");
                return;
            }
            List<Author> authors = getDefaultAuthors();
            for (Author author : authors) {
                authorDao.insert(author);
            }
            authors = authorDao.getAll();

            List<Book> books = getDefaultBooks(authors);
            for (Book book : books) {
                bookDao.insert(book);
            }
            books = bookDao.getAll();

            List<Shop> shops = getDefaultShops();
            for (Shop shop : shops) {
                shopDao.insert(shop);
            }
            shops = shopDao.getAll();

            List<ShopBook> shopBooks = getDefaultShopBooks(shops, books);
            for (ShopBook shopBook : shopBooks) {
                shopBookDao.insert(shopBook);
            }
            System.out.println("Информация по умолчанию вставлена успешно!");
        } catch (RuntimeException e) {
            logger.severe("При добавлении информации в таблицы возникла ошибка: " + e.getMessage());
        }
    }

    public void getAllInfo() {
        try {
            // 1. Выборка по всем таблицам
            System.out.println("\n=== Все авторы ===");
            authorDao.getAll().forEach(System.out::println);
            System.out.println("\n=== Все книги ===");
            bookDao.getAll().forEach(System.out::println);
            System.out.println("\n=== Все магазины ===");
            shopDao.getAll().forEach(System.out::println);
            System.out.println("\n=== Все связи магазин-книга ===");
            shopBookDao.getAll().forEach(System.out::println);

            // 2. Авторы, отсортированные по имени и по фамилии
            System.out.println("\n=== Авторы по имени ===");
            authorDao.getAllSortedByFirstName().forEach(System.out::println);
            System.out.println("\n=== Авторы по фамилии ===");
            authorDao.getAllSortedByLastName().forEach(System.out::println);

            // 3. Книги после заданного года
            System.out.print("\nВведите год для фильтрации книг: ");
            int year = InputManager.getNextInt();
            System.out.println("=== Книги после " + year + " года ===");
            bookDao.getBooksPublishedAfterYear(year).forEach(System.out::println);

            // 5. Книги по началу фамилии автора
            System.out.print("\nВведите первую букву фамилии автора: ");
            String prefix = InputManager.getNextLine();
            System.out.println("=== Книги авторов с фамилией на '" + prefix + "' ===");
            bookDao.getBooksByAuthorLastNameStartingWith(prefix).forEach(System.out::println);

            // 7. Группировка по авторам с количеством книг
            System.out.println("\n=== Количество книг по авторам ===");
            bookDao.getBooksGroupedByAuthorWithCount()
                    .forEach(
                            map ->
                                    System.out.println(
                                            map.get("first_name")
                                                    + " "
                                                    + map.get("last_name")
                                                    + " - "
                                                    + map.get("book_count")
                                                    + " книг"));

        } catch (RuntimeException e) {
            logger.severe("Ошибка при просмотре информации: " + e.getMessage());
        }
    }

    public void updateRandomBookInShop() {
        System.out.print("Введите ID магазина: ");
        Long shopId = InputManager.getNextLong();
        Long bookId = shopBookDao.getRandomBookIdByShop(shopId);
        if (bookId == null) {
            System.out.println("В магазине нет книг.");
            return;
        }
        System.out.print("Введите новое название книги: ");
        String newTitle = InputManager.getNextLine();
        bookDao.updateTitle(bookId, newTitle);
        System.out.println("Название книги обновлено.");
    }

    public void updateAuthorsNamesByShop() {
        System.out.print("Введите ID магазина: ");
        Long shopId = InputManager.getNextLong();
        System.out.print("Введите новое имя для авторов: ");
        String newFirstName = InputManager.getNextLine();
        authorDao.updateFirstNameByShop(shopId, newFirstName);
        System.out.println("Имена авторов обновлены.");
    }

    // ---- Вспомогательные методы для заполнения дефолтных данных ----

    private List<Author> getDefaultAuthors() {
        return Arrays.asList(
                new Author("Лев", "Толстой"),
                new Author("Фёдор", "Достоевский"),
                new Author("Александр", "Пушкин"),
                new Author("Антон", "Чехов"));
    }

    private List<Book> getDefaultBooks(List<Author> authors) {
        List<Book> books = new ArrayList<>();
        books.add(
                new Book(
                        "Война и мир",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Толстой"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1869));
        books.add(
                new Book(
                        "Анна Каренина",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Толстой"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1877));
        books.add(
                new Book(
                        "Преступление и наказание",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Достоевский"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1866));
        books.add(
                new Book(
                        "Идиот",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Достоевский"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1869));
        books.add(
                new Book(
                        "Евгений Онегин",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Пушкин"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1833));
        books.add(
                new Book(
                        "Капитанская дочка",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Пушкин"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1836));
        books.add(
                new Book(
                        "Чайка",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Чехов"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1896));
        books.add(
                new Book(
                        "Вишнёвый сад",
                        authors.stream()
                                .filter(x -> x.getLastName().equals("Чехов"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        1904));
        return books;
    }

    private List<Shop> getDefaultShops() {
        return Arrays.asList(
                new Shop("Белкнига", "проспект Независимости, 14"),
                new Shop("Букинист", "проспект Независимости, 53"),
                new Shop("Oz", "улица Сурганова, 21"));
    }

    private List<ShopBook> getDefaultShopBooks(List<Shop> shops, List<Book> books) {
        List<ShopBook> shopBooks = new ArrayList<>();
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Белкнига"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Война и мир"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Белкнига"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Чайка"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Белкнига"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Капитанская дочка"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Букинист"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Анна Каренина"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Букинист"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Идиот"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Букинист"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Евгений Онегин"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Oz"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Преступление и наказание"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        shopBooks.add(
                new ShopBook(
                        shops.stream()
                                .filter(x -> x.getName().equals("Oz"))
                                .findFirst()
                                .orElseThrow()
                                .getId(),
                        books.stream()
                                .filter(x -> x.getTitle().equals("Вишнёвый сад"))
                                .findFirst()
                                .orElseThrow()
                                .getId()));
        return shopBooks;
    }
}
