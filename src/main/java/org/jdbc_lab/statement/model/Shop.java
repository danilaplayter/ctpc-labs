package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class Shop {
    private Long id;
    private String name;
    private String address;

    public Shop() {}

    public Shop(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
