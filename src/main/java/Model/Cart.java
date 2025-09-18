package Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class Cart {
    private Long id;
    private User user;
    private List<CartItem> items = new ArrayList<>();

    public Cart(Long id,User user) {
        this.id = id;
        this.user = user;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }
}
