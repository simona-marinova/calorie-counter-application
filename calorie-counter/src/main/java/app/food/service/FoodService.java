package app.food.service;


import app.exception.DomainException;
import app.food.model.Food;
import app.food.model.FoodItem;
import app.food.repository.FoodItemRepository;
import app.food.repository.FoodRepository;
import app.meal.model.Meal;
import app.myRecipe.model.MyRecipe;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.CreateFoodRequest;
import app.web.dto.FoodEditRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserService userService;

    @Autowired
    public FoodService(FoodRepository foodRepository, FoodItemRepository foodItemRepository, UserService userService) {
        this.foodRepository = foodRepository;
        this.foodItemRepository = foodItemRepository;
        this.userService = userService;
    }

    public List<Food> getListOfAllFoods() {
        return foodRepository.findAll();
    }

    public void createNewFood(CreateFoodRequest createFoodRequest, User user) {

        Optional<Food> optionalFood = foodRepository.findByUserIdAndName(user.getId(), createFoodRequest.getName());
        if (optionalFood.isPresent()) {
           throw new DomainException("Food with this name [%s] already exists".formatted(createFoodRequest.getName()));
        }

        Food food = Food.builder()
                .name(createFoodRequest.getName())
                .picture(createFoodRequest.getPicture())
                .caloriesPerHundredGrams(createFoodRequest.getCaloriesPerHundredGrams())
                .user(user)
                .build();

        foodRepository.save(food);
    }


    public void editFoodDetails(UUID id, FoodEditRequest foodEditRequest) {
        Food food = getById(id);
        food.setName(foodEditRequest.getName());
        food.setCaloriesPerHundredGrams(foodEditRequest.getCaloriesPerHundredGrams());
        food.setPicture(foodEditRequest.getPicture());

        foodRepository.save(food);
    }



    public double getFoodCalories(UUID userId, String foodItemName) {
        Food food = getByUserIdAndName(userId, foodItemName);
        return food.getCaloriesPerHundredGrams();
    }

    public FoodItem createNewFoodItemForMeal(AddFoodItemRequest addFoodItemRequest, UUID userId, Meal meal) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(addFoodItemRequest.getFoodItemName());
        foodItem.setQuantityInGrams(addFoodItemRequest.getFoodItemQuantity());
        double foodCalories = getFoodCalories(userId, addFoodItemRequest.getFoodItemName());
        double foodItemCalories = foodCalories / 100 * addFoodItemRequest.getFoodItemQuantity();
        foodItem.setCalories(foodItemCalories);
        User user = userService.getById(userId);
        foodItem.setUser(user);
        foodItem.setMeal(meal);
        foodItemRepository.save(foodItem);

        return foodItem;
    }

    public FoodItem createNewFoodItemForRecipe(AddFoodItemRequest addFoodItemRequest, UUID userId, MyRecipe myRecipe) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(addFoodItemRequest.getFoodItemName());
        foodItem.setQuantityInGrams(addFoodItemRequest.getFoodItemQuantity());
        double foodCalories = getFoodCalories(userId, addFoodItemRequest.getFoodItemName());
        double foodItemCalories = foodCalories / 100 * addFoodItemRequest.getFoodItemQuantity();
        foodItem.setCalories(foodItemCalories);
        User user = userService.getById(userId);
        foodItem.setUser(user);
        foodItem.setMyRecipe(myRecipe);
        foodItemRepository.save(foodItem);

        return foodItem;
    }

    public Food getById(UUID id) {
        return foodRepository.findById(id).orElseThrow(() -> new DomainException("Food with id [%s] does not exist.".formatted(id)));
    }

    public Food getByUserIdAndName(UUID userId, String name) {
        return foodRepository.findByUserIdAndName(userId, name).orElseThrow(() -> new DomainException("Food with name [%s] does not exist.".formatted(name)));
    }
    public void deleteFoodById(UUID id) {
        foodRepository.deleteById(id);
    }

    public List<FoodItem> getFoodItemsByMealId(UUID mealId) {
        return foodItemRepository.findAllByMealId(mealId);
    }

    public void deleteListOfFoodItems(List<FoodItem> foodItems) {
        for (FoodItem foodItem : foodItems) {
            UUID foodId = foodItem.getId();
            foodItemRepository.deleteById(foodId);
        }
    }

    public List<FoodItem> getFoodItemsByRecipeId(UUID recipeId) {
        return foodItemRepository.findAllByMyRecipeId(recipeId);
    }

    public FoodItem getFoodItemById(UUID foodItemId) {
        return foodItemRepository.findById(foodItemId).orElseThrow(() -> new DomainException("Food item with id [%s] does not exist.".formatted(foodItemId)));
    }


    public void deleteFoodItemById(UUID foodItemId) {
        foodItemRepository.deleteById(foodItemId);
    }
}

