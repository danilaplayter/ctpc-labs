package org.jdbc_lab.prepared_statement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private String name;
    private Integer credits;
    private String description;

    public Course(String name, Integer credits, String description) {
        this.name = name;
        this.credits = credits;
        this.description = description;
    }
}
