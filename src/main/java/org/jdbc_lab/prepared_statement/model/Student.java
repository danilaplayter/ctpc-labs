package org.jdbc_lab.prepared_statement.model;

import lombok.Data;
import org.jdbc_lab.utils.InputManager;

@Data
public class Student {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String city;
    private String email;

    public Student() {}

    public Student(String firstName, String lastName, Integer age, String city, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.city = city;
        this.email = email;
    }

    public static Student getStudentFromInput() {
        Student student = new Student();
        System.out.print("Введи имя: ");
        student.setFirstName(InputManager.getNextLine());
        System.out.print("Введи фамилию (-, чтобы пропустить): ");
        student.setLastName(InputManager.getNextLineWithSkip());
        System.out.print("Введи возраст (17 - 71): ");
        student.setAge(InputManager.getNextIntInRange(17, 71));
        System.out.print("Введи город (-, чтобы пропустить): ");
        student.setCity(InputManager.getNextLineWithSkip());
        System.out.print("Введи email (-, чтобы пропустить): ");
        student.setEmail(InputManager.getNextLineWithSkip());
        return student;
    }
}
