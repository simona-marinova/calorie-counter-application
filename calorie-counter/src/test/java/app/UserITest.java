package app;

import app.user.model.Country;
import app.user.model.User;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class UserITest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;

    @Test
    void registerUser_happyPath() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("doroteya");
        registerRequest.setPassword("123123");
        registerRequest.setEmail("doroteya@abv.bg");
        registerRequest.setCountry(Country.BULGARIA);
        User user = userService.register(registerRequest);
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(user.getUsername(), registerRequest.getUsername());
        assertEquals(user.getEmail(), registerRequest.getEmail());
        assertEquals(user.getCountry(), registerRequest.getCountry());
        assertTrue(userRepository.findByUsername(registerRequest.getUsername()).isPresent());
    }
}