package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private double price;
    private int stockQuantity;
    private String description;
    private Category category;

    public Book(long id, String title, int stockQuantity) {
        this.id = id;
        this.title = title;
        this.stockQuantity = stockQuantity;
    }
}
