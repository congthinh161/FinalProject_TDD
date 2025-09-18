package Repository;

import Model.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
}