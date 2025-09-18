package Service;

import Model.User;
import Repository.IUserRepository;

import java.util.Optional;

public class AuthService {
    private final IUserRepository userRepository;

    public AuthService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    public boolean login(String username, String password) {
//        return userRepository.findByUsername(username)
//                .map(u -> u.getPassword().equals(password))
//                .orElse(false);
//    }
    public boolean login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getPassword().equals(password);
        }
        return false;
    }
}