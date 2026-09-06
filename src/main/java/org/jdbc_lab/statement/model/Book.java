package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class Book {
    private Long id;
    private String title;
    private Long authorId;
    private Integer yearOfPublication;

    public Book() {}

    public Book(String title, Long authorId, Integer yearOfPublication) {
        this.title = title;
        this.authorId = authorId;
        this.yearOfPublication = yearOfPublication;
    }
}
