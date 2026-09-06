package com.hibernate.xmlbased;

import com.hibernate.xmlbased.dao.UserDAO;
import com.hibernate.xmlbased.menu.AdminMenu;
import com.hibernate.xmlbased.menu.Menu;
import com.hibernate.xmlbased.menu.UserMenu;
import com.hibernate.xmlbased.model.User;
import jakarta.persistence.NoResultException;
import java.util.Scanner;

public class MainClass {

    private static final UserDAO userDAO = new UserDAO();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Menu menu = null;

        while (true) {
            System.out.print("Username: ");
            String username = in.nextLine();
            System.out.print("Password: ");
            String password = in.nextLine();

            try {
                User user = userDAO.AuthUser(username, password);
                menu =
                        switch (user.getUserRole()) {
                            case ROLE_ADMIN -> new AdminMenu();
                            case ROLE_USER -> new UserMenu();
                        };
                menu.menu(user);
                break;
            } catch (NoResultException e) {
                System.out.println("Invalid credentials. Try again.\n");
            }
        }
        in.close();
    }
}
