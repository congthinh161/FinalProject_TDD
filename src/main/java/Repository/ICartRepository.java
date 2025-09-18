package Repository;

import Model.Cart;
import Model.User;

import java.util.Optional;

public interface ICartRepository {
    Optional<Cart> findByUserId(Long userId);
    Cart save(Cart cart);
}

