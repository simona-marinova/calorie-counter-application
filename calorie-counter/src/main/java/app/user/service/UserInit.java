package app.user.service;

import app.user.model.Country;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class UserInit implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserInit(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!userService.getAllUsers().isEmpty()) {
            return;
        }

//register user who is ADMIN
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("simona")
                .password("123123")
                .country(Country.BULGARIA)
                .email("simona@abv.bg")
                .build();
        User user = userService.register(registerRequest);
        user.setUserRole(UserRole.ADMIN);
        userRepository.save(user);

        //register common user
        RegisterRequest registerRequestTwo = RegisterRequest.builder()
                .username("nikola")
                .password("123123")
                .country(Country.BULGARIA)
                .email("nikola@abv.bg")
                .build();
        userService.register(registerRequestTwo);


    }
}
