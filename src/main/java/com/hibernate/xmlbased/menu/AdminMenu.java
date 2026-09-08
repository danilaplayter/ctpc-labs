package com.hibernate.xmlbased.menu;

import com.hibernate.xmlbased.model.Department;
import com.hibernate.xmlbased.model.Developer;
import com.hibernate.xmlbased.model.User;
import java.util.Set;

public class AdminMenu extends Menu {

    @Override
    public void menu(User authUser) {
        super.menu(authUser);
        while (true) {
            System.out.println("\n=== Admin Menu ===");
            System.out.println("0. Exit");
            System.out.println("1. View all departments with workers");
            System.out.println("2. Find department by ID");
            System.out.println("3. Add department");
            System.out.println("4. Move developer to another department");
            System.out.println("5. Delete department");
            System.out.println("6. Find developer by ID");
            System.out.println("7. Add developer");
            System.out.println("8. Delete developer");
            System.out.println("9. List users");
            System.out.println("10. Find user by username");
            System.out.println("11. Add user account");
            System.out.println("12. Delete user");
            System.out.println("13. Departments by first letter (HQL)");
            System.out.println("14. Developers by experience (Criteria)");
            System.out.println("15. Developers by specialty (HQL)");

            try {
                System.out.print("Choice: ");
                String option = in.nextLine();
                switch (option) {
                    case "0" -> {
                        return;
                    }
                    case "1" -> seeAllWorkers();
                    case "2" -> findDepartmentByID();
                    case "3" -> addDepartament();
                    case "4" -> relocateDeveloper();
                    case "5" -> deleteDepartment();
                    case "6" -> findDeveloperByID();
                    case "7" -> addDeveloper();
                    case "8" -> deleteDeveloper();
                    case "9" -> getUsers();
                    case "10" -> getUserByUsername();
                    case "11" -> addUser();
                    case "12" -> deleteUser();
                    case "13" -> findDepartmentByLetter();
                    case "14" -> findByExperience();
                    case "15" -> findBySpecialty();
                    default -> System.out.println("Invalid input.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void seeAllWorkers() {
        Set<Department> departments = departmentDAO.getDepartmentWithWorkers();
        for (Department dep : departments) {
            System.out.println(dep);
            for (Developer dev : dep.getDevelopers()) {
                System.out.println("\t" + dev);
            }
        }
    }

    private void findDepartmentByID() {
        System.out.print("Enter department ID: ");
        String id = in.nextLine();
        Department dep = departmentDAO.findDepartmentByID(id);
        System.out.println(dep != null ? dep : "Not found");
    }

    private void addDepartament() {
        System.out.print("Enter ID (3 chars): ");
        String id = in.nextLine();
        if (id.length() != 3) throw new IllegalArgumentException("ID must be 3 chars");
        System.out.print("Enter name: ");
        String name = in.nextLine();
        System.out.print("Enter location: ");
        String loc = in.nextLine();
        Department dep = new Department(id, name, loc);
        departmentDAO.addDepartament(dep);
        System.out.println("Department added.");
    }

    private void relocateDeveloper() {
        System.out.print("Enter developer ID: ");
        int devId = Integer.parseInt(in.nextLine());
        Developer dev = developerDAO.getDeveloperById(devId);
        if (dev == null) throw new NullPointerException("Developer not found");
        System.out.print("Enter new department ID: ");
        String depId = in.nextLine();
        Department dep = departmentDAO.findDepartmentByID(depId);
        if (dep == null) throw new NullPointerException("Department not found");
        developerDAO.updateDevelopersDepartment(devId, dep);
        System.out.println("Developer relocated.");
    }

    private void deleteDepartment() {
        System.out.print("Enter department ID: ");
        String id = in.nextLine();
        departmentDAO.deleteDepartment(id);
        System.out.println("Department deleted.");
    }

    private void findDeveloperByID() {
        System.out.print("Enter developer ID: ");
        int id = Integer.parseInt(in.nextLine());
        Developer dev = developerDAO.getDeveloperById(id);
        System.out.println(dev != null ? dev : "Not found");
    }

    private void addDeveloper() {
        System.out.print("Name: ");
        String name = in.nextLine();
        System.out.print("Specialty: ");
        String spec = in.nextLine();
        System.out.print("Experience: ");
        int exp = Integer.parseInt(in.nextLine());
        System.out.println("Choose department:");
        for (Department d : departmentDAO.getDepartments()) {
            System.out.println(d);
        }
        System.out.print("Department ID: ");
        String depId = in.nextLine();
        Department dep = departmentDAO.findDepartmentByID(depId);
        Developer dev = new Developer(0, name, spec, exp, dep);
        developerDAO.addDeveloper(dev);
        System.out.println("Developer added.");
    }

    private void deleteDeveloper() {
        System.out.print("Enter developer ID: ");
        int id = Integer.parseInt(in.nextLine());
        developerDAO.removeDeveloper(id);
        System.out.println("Developer deleted.");
    }

    private void getUsers() {
        userDAO.getUsers().forEach(System.out::println);
    }

    private void getUserByUsername() {
        System.out.print("Enter username: ");
        String username = in.nextLine();
        User user = userDAO.getUserByUsername(username);
        System.out.println(user);
    }

    private void addUser() {
        seeAllWorkers();
        System.out.print("Enter developer ID: ");
        int devId = Integer.parseInt(in.nextLine());
        Developer dev = developerDAO.getDeveloperById(devId);
        if (dev == null) {
            throw new NullPointerException("Developer not found");
        }
        System.out.print("Username: ");
        String username = in.nextLine();
        System.out.print("Password: ");
        String password = in.nextLine();

        User user = new User(username, password);
        user.setDeveloper(dev);
        userDAO.addUser(user);
        System.out.println("User added.");
    }

    private void deleteUser() {
        System.out.print("Enter user ID: ");
        int id = Integer.parseInt(in.nextLine());
        if (userDAO.getUserById(id) == null) throw new NullPointerException("User not found");
        developerDAO.removeDeveloper(id);
        System.out.println("User deleted.");
    }

    private void findDepartmentByLetter() {
        System.out.print("Enter first letter of department ID: ");
        String letter = in.nextLine();
        if (letter.length() != 1) throw new IllegalArgumentException("One character expected");
        departmentDAO.findDepartmentByLetterInID(letter.charAt(0)).forEach(System.out::println);
    }

    private void findByExperience() {
        System.out.print("Enter experience: ");
        int exp = Integer.parseInt(in.nextLine());
        developerDAO.findByExperienceEqualCriteria(exp).forEach(System.out::println);
    }

    private void findBySpecialty() {
        System.out.print("Enter specialty: ");
        String spec = in.nextLine();
        developerDAO.findBySpecialty(spec).forEach(System.out::println);
    }
}
