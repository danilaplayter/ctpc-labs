package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class ShopBook {
    private Long id;
    private Long shopId;
    private Long bookId;

    public ShopBook() {}

    public ShopBook(Long shopId, Long bookId) {
        this.shopId = shopId;
        this.bookId = bookId;
    }
}
