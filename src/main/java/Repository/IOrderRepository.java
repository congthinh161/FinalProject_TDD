package Repository;

import Model.Order;

import java.util.List;
import java.util.Optional;

public interface IOrderRepository {
    void save(Order order);
    void delete(Order order);
    Optional<Order> findById(Long Id);
}
