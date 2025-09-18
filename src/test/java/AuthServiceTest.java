import Model.User;
import Repository.IUserRepository;
import Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    IUserRepository userRepository;
    AuthService authService;
    User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        authService = new AuthService(userRepository);
        user = new User(1L, "thinhnc5", "@123456");
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByUsername("thinhnc5")).thenReturn(Optional.of(user));
        boolean result = authService.login("thinhnc5", "@123456");
        assertThat(result, is(true));
    }

    @Test
    void testLoginFail() {
        when(userRepository.findByUsername("Viet")).thenReturn(Optional.empty());
        boolean result = authService.login("Viet", "112233");
        assertThat(result, is(false));
    }
}