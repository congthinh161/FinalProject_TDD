package Model;

import lombok.Data;

@Data
public class Order {
    private Long id;
    private Book book;
    private int quantity;

    public Order(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
}