package app.meal.service;

import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.service.DailyStatisticsService;
import app.meal.model.Meal;
import app.meal.model.MealType;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.AddRecipeItemRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Order(5)
@Component
public class MealInit implements CommandLineRunner {
    private final UserService userService;
    private final MealService mealService;
    private final DailyStatisticsService dailyStatisticsService;


    public MealInit(UserService userService, MealService mealService, DailyStatisticsService dailyStatisticsService) {
        this.userService = userService;
        this.mealService = mealService;
        this.dailyStatisticsService = dailyStatisticsService;
    }


    @Override
    public void run(String... args) throws Exception {

        User user = userService.getByUsername("simona");
        User userTwo = userService.getByUsername("nikola");

        DailyStatistics dailyStatisticsUserOne = dailyStatisticsService.getDailyStatisticsByDateAndUserId(LocalDate.now(), user.getId());
        DailyStatistics dailyStatisticsUserTwo = dailyStatisticsService.getDailyStatisticsByDateAndUserId(LocalDate.now(), userTwo.getId());


        if (dailyStatisticsUserOne.getConsumedCalories() != 0.00 && dailyStatisticsUserTwo.getConsumedCalories() != 0.00) {
            return;
        }

        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(200)
                .build();

        AddFoodItemRequest addFoodItemRequestTwo = AddFoodItemRequest.builder()
                .foodItemName("banana")
                .foodItemQuantity(100)
                .build();

        AddRecipeItemRequest addRecipeItemRequest = AddRecipeItemRequest.builder()
                .recipeItemName("Omelette with Tomatoes and Cheese")
                .recipeItemQuantity(300)
                .build();


        List<Meal> mealsForTheDayUserOneOptional = mealService.getMealsByUserIdAndDate(user.getId(), LocalDate.now());
        if (mealsForTheDayUserOneOptional.isEmpty()) {
            mealService.createMealsForTheDay(user.getId());
        }
        List<Meal> mealsForTheDayUserTwoOptional = mealService.getMealsByUserIdAndDate(userTwo.getId(), LocalDate.now());
        if (mealsForTheDayUserTwoOptional.isEmpty()) {
            mealService.createMealsForTheDay(userTwo.getId());
        }


        List<Meal> mealsForTheDayUserOne = mealService.getMealsByUserIdAndDate(user.getId(), LocalDate.now());
        List<Meal> mealsForTheDayUserTwo = mealService.getMealsByUserIdAndDate(userTwo.getId(), LocalDate.now());


        if (!mealsForTheDayUserOne.isEmpty()) {
            for (Meal meal : mealsForTheDayUserOne) {
                if (meal.getMealType() == MealType.BREAKFAST) {
                    mealService.addFoodItemsToMeal(addFoodItemRequest, user.getId(), meal.getId());
                    mealService.addFoodItemsToMeal(addFoodItemRequestTwo, user.getId(), meal.getId());
                }
                if (meal.getMealType() == MealType.LUNCH) {
                    mealService.addRecipeItemsToMeal(addRecipeItemRequest, user.getId(), meal.getId());
                }
            }
        }

        if (!mealsForTheDayUserTwo.isEmpty()) {
            for (Meal meal : mealsForTheDayUserTwo) {
                if (meal.getMealType() == MealType.BREAKFAST) {
                    mealService.addFoodItemsToMeal(addFoodItemRequest, userTwo.getId(), meal.getId());
                    mealService.addFoodItemsToMeal(addFoodItemRequestTwo, userTwo.getId(), meal.getId());
                }
                if (meal.getMealType() == MealType.LUNCH) {
                    mealService.addRecipeItemsToMeal(addRecipeItemRequest, userTwo.getId(), meal.getId());
                }
            }
        }


    }

}