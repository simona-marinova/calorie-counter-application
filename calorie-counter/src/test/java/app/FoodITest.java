package app;

import app.food.model.FoodItem;
import app.food.repository.FoodItemRepository;
import app.food.repository.FoodRepository;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.meal.model.MealType;
import app.meal.service.MealService;
import app.user.model.Country;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.CreateFoodRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class FoodITest {

    @Autowired
    private FoodItemRepository foodItemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private MealService mealService;

    @Test
    public void createNewFoodItemForMeal_shouldCreateFoodItemSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("doroteya");
        registerRequest.setPassword("123123");
        registerRequest.setEmail("doroteya@abv.bg");
        registerRequest.setCountry(Country.BULGARIA);
        User user = userService.register(registerRequest);
        mealService.createMealsForTheDay(user.getId());
        CreateFoodRequest createFoodRequest = CreateFoodRequest.builder()
                .name("apple")
                .caloriesPerHundredGrams(52)
                .build();

        foodService.createNewFood(createFoodRequest, user.getId());
        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(150.00)
                .build();
        LocalDate today = LocalDate.now();
        List<Meal> meals = mealService.getMealsByUserIdAndDate(user.getId(), today);
        Meal mealBreakfast = null;
        for (Meal meal : meals) {
            if (meal.getMealType() == MealType.BREAKFAST) {
                mealBreakfast = meal;
            }
        }
        FoodItem createdFoodItem = foodService.createNewFoodItemForMeal(addFoodItemRequest, user.getId(), mealBreakfast);
        assertNotNull(createdFoodItem);
        assertEquals(addFoodItemRequest.getFoodItemName(), createdFoodItem.getName());
        assertEquals(addFoodItemRequest.getFoodItemQuantity(), createdFoodItem.getQuantityInGrams());
        assertEquals(78.00, createdFoodItem.getCalories());
        assertTrue(foodItemRepository.findById(createdFoodItem.getId()).isPresent());
    }


}
