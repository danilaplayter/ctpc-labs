package com.hibernate.xmlbased.menu;

import com.hibernate.xmlbased.model.User;

public class UserMenu extends Menu {

    @Override
    public void menu(User authUser) {
        super.menu(authUser);
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("0. Exit");
            System.out.println("1. My department info");
            System.out.println("2. List my colleagues");
            System.out.println("3. Change password");

            try {
                System.out.print("Choice: ");
                String option = in.nextLine();
                switch (option) {
                    case "0" -> {
                        return;
                    }
                    case "1" -> getMyDepartment();
                    case "2" -> getMyPals();
                    case "3" -> updatePassword();
                    default -> System.out.println("Invalid input.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void getMyDepartment() {
        System.out.println("Your department: " + authUser.getDeveloper().getDepartment());
    }

    private void getMyPals() {
        departmentDAO
                .getDevelopersByDepartment(authUser.getDeveloper().getDepartment())
                .forEach(System.out::println);
    }

    private void updatePassword() {
        System.out.print("New password: ");
        String newPass = in.nextLine();
        userDAO.updatePassword(authUser.getUserId(), newPass);
        System.out.println("Password updated.");
    }
}
