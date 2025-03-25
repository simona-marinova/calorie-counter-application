package app.meal.service;


import app.dailyStatistics.service.DailyStatisticsService;
import app.exception.DomainException;
import app.food.model.FoodItem;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.meal.model.MealType;
import app.meal.repository.MealRepository;
import app.myRecipe.model.MyRecipeItem;
import app.myRecipe.service.MyRecipeService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.AddRecipeItemRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MealService {

    private final MyRecipeService myRecipeService;
    private final MealRepository mealRepository;
    private final FoodService foodService;
    private final UserService userService;
    private final DailyStatisticsService dailyStatisticsService;


    @Autowired
    public MealService(MyRecipeService myRecipeService, MealRepository mealRepository, FoodService foodService, UserService userService, DailyStatisticsService dailyStatisticsService) {
        this.myRecipeService = myRecipeService;
        this.mealRepository = mealRepository;
        this.foodService = foodService;
        this.userService = userService;
        this.dailyStatisticsService = dailyStatisticsService;
    }


    public void addFoodItemsToMeal(AddFoodItemRequest addFoodItemRequest, UUID userId, UUID id) {
        Meal meal = getMealById(id);

        double foodCalories = 0;

        FoodItem foodItem = foodService.createNewFoodItemForMeal(addFoodItemRequest, userId, meal);
        List<FoodItem> foodItems = foodService.getFoodItemsByMealId(id);
        foodItems.add(foodItem);
        meal.setFoodItems(foodItems);
        foodCalories = foodItem.getCalories();

        meal.setCalories(meal.getCalories() + foodCalories);
        dailyStatisticsService.updateConsumedAndRemainingCalories(foodCalories, userId);

        mealRepository.save(meal);
    }

    public void addRecipeItemsToMeal(AddRecipeItemRequest addRecipeItemRequest, UUID userId, UUID id) {
        Meal meal = getMealById(id);

        double myRecipeCalories = 0;

        MyRecipeItem myRecipeItem = myRecipeService.createNewMyRecipeItem(addRecipeItemRequest, userId, meal);
        List<MyRecipeItem> myRecipeItems = meal.getMyRecipeItems();
        myRecipeItems.add(myRecipeItem);
        meal.setMyRecipeItems(myRecipeItems);
        double myRecipeItemCalories = myRecipeItem.getCalories();
        meal.setCalories(Math.round((meal.getCalories() + myRecipeItemCalories) * 100.0) / 100.0);
        dailyStatisticsService.updateConsumedAndRemainingCalories(myRecipeItemCalories, userId);

        mealRepository.save(meal);
    }


    @Transactional
    public void createMealsForTheDay(UUID userId) {
        LocalDate today = LocalDate.now();
        User user = userService.getById(userId);
        List<Meal> meals = getMealsByUserIdAndDate(userId, today);
        if (meals.isEmpty()) {
            Meal breakfast = new Meal();
            Meal lunch = new Meal();
            Meal dinner = new Meal();
            Meal snack = new Meal();
            breakfast.setMealType(MealType.BREAKFAST);
            breakfast.setDate(today);
            breakfast.setUser(user);
            breakfast.setCalories(0);
            breakfast.setFoodItems(new ArrayList<>());
            breakfast.setMyRecipeItems(new ArrayList<>());
            mealRepository.save(breakfast);
            lunch.setMealType(MealType.LUNCH);
            lunch.setDate(today);
            lunch.setUser(user);
            lunch.setCalories(0);
            lunch.setFoodItems(new ArrayList<>());
            lunch.setMyRecipeItems(new ArrayList<>());
            mealRepository.save(lunch);
            dinner.setMealType(MealType.DINNER);
            dinner.setDate(today);
            dinner.setUser(user);
            dinner.setCalories(0);
            dinner.setFoodItems(new ArrayList<>());
            dinner.setMyRecipeItems(new ArrayList<>());
            mealRepository.save(dinner);
            snack.setMealType(MealType.SNACK);
            snack.setDate(today);
            snack.setUser(user);
            snack.setCalories(0);
            snack.setFoodItems(new ArrayList<>());
            snack.setMyRecipeItems(new ArrayList<>());
           mealRepository.save(snack);
        }
    }


    public List<Meal> getMealsByUserIdAndDate(UUID userId, LocalDate date) {
        return mealRepository.findAllByUserIdAndDate(userId, date);
    }


    public Meal getMealById(UUID id) {
        return mealRepository.findById(id).orElseThrow(() -> new DomainException("Meal with id [%s] does not exist.".formatted(id)));

    }

    public void deleteMealById(UUID id, UUID userId) {
        Meal meal = getMealById(id);
        double calories = meal.getCalories();
        meal.setCalories(0);
        if (!meal.getFoodItems().isEmpty()) {
            foodService.deleteListOfFoodItems(meal.getFoodItems());
        }
        if (!meal.getMyRecipeItems().isEmpty()) {
            myRecipeService.deleteListOfMyRecipeItems(meal.getMyRecipeItems());
        }
        meal.setMyRecipeItems(new ArrayList<>());
        meal.setFoodItems(new ArrayList<>());
        dailyStatisticsService.updateRemainingCalories(userId, calories);
        dailyStatisticsService.lowerCaloriesConsumed(userId, calories);
        mealRepository.save(meal);
    }


}
