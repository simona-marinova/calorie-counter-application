package app.myRecipe.service;

import app.exception.DomainException;
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
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
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
        double myRecipeCalories = getMyRecipeCalories(userId, myRecipeItem);
        double myRecipeItemCalories = myRecipeCalories / 100 * myRecipeItem.getQuantityInGrams();
        myRecipeItem.setCalories(myRecipeItemCalories);
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

        MyRecipe myRecipe = MyRecipe.builder()
                .name(createMyRecipeRequest.getName())
                .instructions(createMyRecipeRequest.getInstructions())
                .user(user)
                .foodItems(new ArrayList<>())
                .recipePublic(createMyRecipeRequest.isRecipePublic())
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
        myRecipe.setAllCalories(myRecipe.getAllCalories() + foodItem.getCalories());


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
        myRecipe.setAllCalories(myRecipe.getAllCalories() - foodItem.getCalories());
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
        return myRecipeRepository.findAllByRecipePublic(true)
                .stream()
                .filter(r->!r.getUser().getId().equals(userId)).toList();
    }

    public List<MyRecipe> getAllRecipesByUserId(UUID userId) {
        return myRecipeRepository.findAllByUserId(userId);
    }


}

