package Model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
    private Long id;
    private User user;
    private Book book;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
