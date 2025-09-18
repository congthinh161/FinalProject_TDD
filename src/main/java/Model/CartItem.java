package Model;

import lombok.Data;

@Data
public class CartItem {
    private Long id;
    private Book book;
    private int quantity;

    public CartItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
    }
}
