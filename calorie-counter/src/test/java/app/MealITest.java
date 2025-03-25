package app;


import app.meal.model.Meal;
import app.meal.model.MealType;
import app.meal.repository.MealRepository;
import app.meal.service.MealService;
import app.user.model.Country;
import app.user.model.User;
import app.user.service.UserService;

import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class MealITest {


    @Autowired
    private MealService mealService;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private UserService userService;


    @Test
    public void createMealsForTheDay_shouldCreateMealsWhenNoneExist() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("doroteya");
        registerRequest.setPassword("123123");
        registerRequest.setEmail("doroteya@abv.bg");
        registerRequest.setCountry(Country.BULGARIA);
        User user = userService.register(registerRequest);
        LocalDate date = LocalDate.now();
         mealService.createMealsForTheDay(user.getId());
        List<Meal> meals = mealRepository.findAllByUserIdAndDate(user.getId(), date);
        assertEquals(4, meals.size());
        assertTrue(meals.stream().anyMatch(meal -> meal.getMealType() == MealType.BREAKFAST));
        assertTrue(meals.stream().anyMatch(meal -> meal.getMealType() == MealType.LUNCH));
        assertTrue(meals.stream().anyMatch(meal -> meal.getMealType() == MealType.DINNER));
        assertTrue(meals.stream().anyMatch(meal -> meal.getMealType() == MealType.SNACK));
        meals.forEach(meal -> {
            assertEquals(0, meal.getCalories());
            assertTrue(meal.getFoodItems().isEmpty());
            assertTrue(meal.getMyRecipeItems().isEmpty());
            assertEquals(LocalDate.now(), meal.getDate());
            assertEquals(user.getId(), meal.getUser().getId());
        });
    }

}
