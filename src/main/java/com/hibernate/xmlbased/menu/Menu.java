package com.hibernate.xmlbased.menu;

import com.hibernate.xmlbased.dao.DepartmentDAO;
import com.hibernate.xmlbased.dao.DeveloperDAO;
import com.hibernate.xmlbased.dao.UserDAO;
import com.hibernate.xmlbased.model.User;
import java.util.Scanner;

public abstract class Menu {

    protected User authUser;
    protected Scanner in = new Scanner(System.in);

    protected static final DeveloperDAO developerDAO = new DeveloperDAO();
    protected static final UserDAO userDAO = new UserDAO();
    protected static final DepartmentDAO departmentDAO = new DepartmentDAO();

    public void menu(User authUser) {
        this.authUser = authUser;
        System.out.printf(
                "\nHi, %s. Your role: %s\n",
                authUser.getDeveloper().getName(), authUser.getUserRole());
    }
}
