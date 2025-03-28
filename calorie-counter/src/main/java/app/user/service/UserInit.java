package app.user.service;

import app.user.model.*;
import app.user.repository.UserRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
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
      User userTwo = userService.register(registerRequestTwo);

        UserEditRequest userEditRequest = UserEditRequest.builder()
                .firstName("Simona")
                .lastName("Marinova")
                .email("simona@abv.bg")
                .height(165)
                .weight(54)
                .age(36)
                .country(Country.BULGARIA)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .build();
        userService.editUser(user.getId(), userEditRequest);

        UserEditRequest userEditRequestTwo = UserEditRequest.builder()
                .firstName("Nikola")
                .lastName("Georgiev")
                .email("nikola@abv.bg")
                .height(175)
                .weight(95)
                .age(25)
                .country(Country.BULGARIA)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .weightGoal(WeightGoal.WEIGHT_MAINTENANCE)
                .build();
        userService.editUser(userTwo.getId(), userEditRequestTwo);


    }
}
