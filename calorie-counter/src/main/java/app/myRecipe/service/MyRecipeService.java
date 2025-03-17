package app.myRecipe.service;

import app.exception.DomainException;
import app.exception.MyRecipeAlreadyExistsException;
import app.food.model.FoodItem;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.myRecipe.model.MyRecipe;
import app.myRecipe.model.MyRecipeItem;
import app.myRecipe.repository.MyRecipeItemRepository;
import app.myRecipe.repository.MyRecipeRepository;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.AddRecipeItemRequest;
import app.web.dto.CreateMyRecipeRequest;
import app.web.dto.EditMyRecipeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MyRecipeService {

    private final MyRecipeRepository myRecipeRepository;
    private final MyRecipeItemRepository myRecipeItemRepository;
    private final UserService userService;
    private final FoodService foodService;

    @Autowired
    public MyRecipeService(MyRecipeRepository myRecipeRepository, MyRecipeItemRepository myRecipeItemRepository, UserService userService, FoodService foodService) {
        this.myRecipeRepository = myRecipeRepository;
        this.myRecipeItemRepository = myRecipeItemRepository;
        this.userService = userService;
        this.foodService = foodService;
    }


    public List<MyRecipe> getListOfAllRecipes() {
        return myRecipeRepository.findAll();
    }


    public double getMyRecipeCalories(UUID userId, MyRecipeItem myRecipeItem) {
        MyRecipe myRecipe = getByUserIdAndName(userId, myRecipeItem.getName());
        return myRecipe.getCaloriesPerHundredGrams();
    }

    public MyRecipeItem createNewMyRecipeItem(AddRecipeItemRequest addRecipeItemRequest, UUID userId, Meal meal) {
        MyRecipeItem myRecipeItem = new MyRecipeItem();
        myRecipeItem.setName(addRecipeItemRequest.getRecipeItemName());
        myRecipeItem.setQuantityInGrams(addRecipeItemRequest.getRecipeItemQuantity());
        MyRecipe myRecipe = getByUserIdAndName(userId, addRecipeItemRequest.getRecipeItemName());
        double myRecipeCaloriesPerHundredGrams = myRecipe.getCaloriesPerHundredGrams();
        double myRecipeItemCalories = Math.round(myRecipeCaloriesPerHundredGrams * addRecipeItemRequest.getRecipeItemQuantity())*100;
        myRecipeItem.setCalories(myRecipeItemCalories/100);
        User user = userService.getById(userId);
        myRecipeItem.setUser(user);
        myRecipeItem.setMeal(meal);
        myRecipeItemRepository.save(myRecipeItem);

        return myRecipeItem;
    }


    public MyRecipe getByUserIdAndName(UUID userId, String name) {
        return myRecipeRepository.findByUserIdAndName(userId, name).orElseThrow(() -> new DomainException("Recipe with id [%s] does not exist.".formatted(name)));
    }


    public void deleteListOfMyRecipeItems(List<MyRecipeItem> myRecipeItems) {
        for (MyRecipeItem myRecipeItem : myRecipeItems) {
            UUID myRecipeItemId = myRecipeItem.getId();
            myRecipeItemRepository.deleteById(myRecipeItemId);
        }
    }

    public void createNewRecipe(CreateMyRecipeRequest createMyRecipeRequest, User user) {
        Optional<MyRecipe> optionalRecipe = myRecipeRepository.findByUserIdAndName(user.getId(), createMyRecipeRequest.getName());

        if(optionalRecipe.isPresent()) {
            throw new MyRecipeAlreadyExistsException("Recipe with this name [%s] already exists".formatted(createMyRecipeRequest.getName()));
        }

        MyRecipe myRecipe = MyRecipe.builder()
                .name(createMyRecipeRequest.getName())
                .instructions(createMyRecipeRequest.getInstructions())
                .user(user)
                .foodItems(new ArrayList<>())
                .publicRecipe(createMyRecipeRequest.isRecipePublic())
                .build();

        myRecipeRepository.save(myRecipe);

    }


    public void addFoodItemToRecipe(AddFoodItemRequest addFoodItemRequest, User user, MyRecipe myRecipe) {

        FoodItem foodItem = foodService.createNewFoodItemForRecipe(addFoodItemRequest, user.getId(), myRecipe);

        List<FoodItem> foodItems = foodService.getFoodItemsByRecipeId(myRecipe.getId());
        if (!foodItems.isEmpty()) {
            foodItems = new ArrayList<>();
        }
        foodItems.add(foodItem);
        myRecipe.setFoodItems(foodItems);
        myRecipe.setTotalCalories(myRecipe.getTotalCalories() + foodItem.getCalories());
        myRecipe.setCaloriesPerHundredGrams(calculateCaloriesPer100Grams(myRecipe));
        myRecipeRepository.save(myRecipe);
    }

    public void editMyRecipe(UUID id, EditMyRecipeRequest editMyRecipeRequest) {
        MyRecipe myRecipe = getById(id);
        myRecipe.setName(editMyRecipeRequest.getName());
        myRecipe.setInstructions(editMyRecipeRequest.getInstructions());
        myRecipeRepository.save(myRecipe);

    }


    public void removeFoodItemFromRecipe(UUID id, UUID foodItemId) {
        MyRecipe myRecipe = getById(id);
        FoodItem foodItem = foodService.getFoodItemById(foodItemId);
        myRecipe.setTotalCalories(myRecipe.getTotalCalories() - foodItem.getCalories());
        List<FoodItem> foodItems = foodService.getFoodItemsByRecipeId(id);
        foodItems.remove(foodItem);
        foodService.deleteFoodItemById(foodItemId);
        if (foodItems.isEmpty()) {
            myRecipe.setFoodItems(new ArrayList<>());
        } else {
            myRecipe.setFoodItems(foodItems);
        }
        myRecipeRepository.save(myRecipe);
    }


    public double calculateCaloriesPer100Grams(MyRecipe recipe) {
        if (recipe.getFoodItems() == null || recipe.getFoodItems().isEmpty()) {
            return 0.0;
        }

        double totalWeight = recipe.getFoodItems().stream()
                .mapToDouble(FoodItem::getQuantityInGrams)
                .sum();

        double totalCalories = recipe.getFoodItems().stream()
                .mapToDouble(FoodItem::getCalories)
                .sum();

        // Calculation of weight loss during cooking
        totalWeight = totalWeight - totalWeight * 0.10;

        if (totalWeight <= 0) {
            return 0.0;
        }

        double calories = Math.round(totalCalories / totalWeight * 100);

        return calories / 100;
    }


    public void deleteMyRecipe(UUID id) {
        List<FoodItem> foodItems = foodService.getFoodItemsByRecipeId(id);
        if (!foodItems.isEmpty()) {
            for (FoodItem foodItem : foodItems) {
                foodService.deleteFoodItemById(foodItem.getId());
            }
        }
        myRecipeRepository.deleteById(id);
    }


    public MyRecipe getById(UUID id) {
        return myRecipeRepository.findById(id).orElseThrow(() -> new DomainException("Recipe with id [%s] does not exist.".formatted(id)));

    }

    public List<MyRecipe> getAllPublicRecipes(UUID userId) {
        return myRecipeRepository.findAllByPublicRecipe(true)
                .stream()
                .filter(r -> !r.getUser().getId().equals(userId)).toList();
    }

    public List<MyRecipe> getAllRecipesByUserId(UUID userId) {
        return myRecipeRepository.findAllByUserId(userId);
    }

    public List<MyRecipe> getAllRecipesByUserIdAndCaloriesNotZero(UUID userId) {
        return myRecipeRepository.findAllByUserId(userId).stream().filter(r -> r.getCaloriesPerHundredGrams() != 0.00).toList();
    }

}

