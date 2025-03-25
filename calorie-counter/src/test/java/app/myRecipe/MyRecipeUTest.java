package app.myRecipe;

import app.exception.DomainException;
import app.exception.MyRecipeAlreadyExistsException;
import app.food.model.FoodItem;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.myRecipe.model.MyRecipe;
import app.myRecipe.model.MyRecipeItem;
import app.myRecipe.repository.MyRecipeItemRepository;
import app.myRecipe.repository.MyRecipeRepository;
import app.myRecipe.service.MyRecipeService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddRecipeItemRequest;
import app.web.dto.CreateMyRecipeRequest;
import app.web.dto.EditMyRecipeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyRecipeUTest {

    @Mock
    private MyRecipeRepository myRecipeRepository;
    @Mock
    private MyRecipeItemRepository myRecipeItemRepository;
    @Mock
    private UserService userService;
    @Mock
    private FoodService foodService;

    @InjectMocks
    private MyRecipeService myRecipeService;

    @Test
    public void getMyRecipeCalories_thenReturnCaloriesPerHundredGrams() {
        UUID userId = UUID.randomUUID();
        String recipeName = "Chicken Salad";
        double caloriesPerHundredGrams = 150.0;
        MyRecipeItem myRecipeItem = MyRecipeItem.builder().name(recipeName).build();
        MyRecipe myRecipe = MyRecipe.builder().caloriesPerHundredGrams(caloriesPerHundredGrams).build();
        when(myRecipeRepository.findByUserIdAndName(userId, recipeName)).thenReturn(Optional.of(myRecipe));
        double result = myRecipeService.getMyRecipeCalories(userId, myRecipeItem);
        assertEquals(caloriesPerHundredGrams, result);
        verify(myRecipeRepository, times(1)).findByUserIdAndName(userId, recipeName);
    }


    @Test
    public void deleteListOfMyRecipeItems_thenDeleteEachMyRecipeItemById() {
        MyRecipeItem myRecipeItemOne = MyRecipeItem.builder()
                .id(UUID.randomUUID())
                .build();
        MyRecipeItem myRecipeItemTwo = MyRecipeItem.builder()
                .id(UUID.randomUUID())
                .build();
        List<MyRecipeItem> myRecipeItems = new ArrayList<>();
        myRecipeItems.add(myRecipeItemOne);
        myRecipeItems.add(myRecipeItemTwo);
        myRecipeService.deleteListOfMyRecipeItems(myRecipeItems);
        verify(myRecipeItemRepository, times(1)).deleteById(myRecipeItemOne.getId());
        verify(myRecipeItemRepository, times(1)).deleteById(myRecipeItemTwo.getId());
    }

    @Test
    public void deleteListOfMyRecipeItems_whenEmptyList() {
        List<MyRecipeItem> myRecipeItems = new ArrayList<>();
        myRecipeService.deleteListOfMyRecipeItems(myRecipeItems);
        verify(myRecipeItemRepository, never()).deleteById(any(UUID.class));
    }


    @Test
    public void getListOfAllRecipes_thenReturnListOfRecipes() {
        MyRecipe recipeOne = MyRecipe.builder()
                .id(UUID.randomUUID())
                .build();
        MyRecipe recipeTwo = MyRecipe.builder()
                .id(UUID.randomUUID())
                .build();
        List<MyRecipe> myRecipes = new ArrayList<>();
        myRecipes.add(recipeOne);
        myRecipes.add(recipeTwo);
        when(myRecipeRepository.findAll()).thenReturn(myRecipes);
        List<MyRecipe> result = myRecipeService.getListOfAllRecipes();
        assertEquals(myRecipes, result);
        verify(myRecipeRepository, times(1)).findAll();
    }

    @Test
    public void getListOfAllRecipes_whenNoRecipesFound_thenReturnEmptyList() {
        List<MyRecipe> myRecipes = new ArrayList<>();
        when(myRecipeRepository.findAll()).thenReturn(myRecipes);
        List<MyRecipe> result = myRecipeService.getListOfAllRecipes();
        assertEquals(myRecipes, result);
        verify(myRecipeRepository, times(1)).findAll();
    }


    @Test
    public void getAllRecipesByUserIdAndCaloriesNotZero_thenReturnFilteredRecipes() {
        UUID userId = UUID.randomUUID();
        MyRecipe recipeOne = MyRecipe.builder()
                .id(UUID.randomUUID())
                .caloriesPerHundredGrams(0.00)
                .build();
        MyRecipe recipeTwo = MyRecipe.builder()
                .id(UUID.randomUUID())
                .caloriesPerHundredGrams(100.00)
                .build();
        List<MyRecipe> myRecipes = new ArrayList<>();
        myRecipes.add(recipeOne);
        myRecipes.add(recipeTwo);
        List<MyRecipe> expectedRecipes = new ArrayList<>();
        expectedRecipes.add(recipeTwo);
        when(myRecipeRepository.findAllByUserId(userId)).thenReturn(myRecipes);
        List<MyRecipe> result = myRecipeService.getAllRecipesByUserIdAndCaloriesNotZero(userId);
        assertEquals(expectedRecipes, result);
        verify(myRecipeRepository, times(1)).findAllByUserId(userId);
    }


    @Test
    public void getAllRecipesByUserIdAndCaloriesNotZero_whenNotFound_thenReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        MyRecipe recipeOne = MyRecipe.builder()
                .id(UUID.randomUUID())
                .caloriesPerHundredGrams(0.00)
                .build();
        MyRecipe recipeTwo = MyRecipe.builder()
                .id(UUID.randomUUID())
                .caloriesPerHundredGrams(.00)
                .build();
        List<MyRecipe> myRecipes = new ArrayList<>();
        myRecipes.add(recipeOne);
        myRecipes.add(recipeTwo);
        List<MyRecipe> expectedRecipes = new ArrayList<>();
        when(myRecipeRepository.findAllByUserId(userId)).thenReturn(myRecipes);
        List<MyRecipe> result = myRecipeService.getAllRecipesByUserIdAndCaloriesNotZero(userId);
        assertEquals(expectedRecipes, result);
        verify(myRecipeRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getAllRecipesByUserId_thenReturnListOfRecipes() {
        UUID userOneId = UUID.randomUUID();
        User userOne = User.builder()
                .id(userOneId)
                .build();
        MyRecipe recipeOne = MyRecipe.builder()
                .id(UUID.randomUUID())
                .caloriesPerHundredGrams(0.00)
                .user(userOne)
                .build();
        MyRecipe recipeTwo = MyRecipe.builder()
                .id(UUID.randomUUID())
                .user(userOne)
                .caloriesPerHundredGrams(.00)
                .build();
        List<MyRecipe> myRecipes = new ArrayList<>();
        myRecipes.add(recipeOne);
        myRecipes.add(recipeTwo);
        when(myRecipeRepository.findAllByUserId(userOneId)).thenReturn(myRecipes);
        List<MyRecipe> result = myRecipeService.getAllRecipesByUserId(userOneId);
        assertEquals(myRecipes, result);
        verify(myRecipeRepository, times(1)).findAllByUserId(userOneId);
    }

    @Test
    public void getAllRecipesByUserId_whenNoRecipesFound_thenReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        List<MyRecipe> myRecipes = new ArrayList<>();
        when(myRecipeRepository.findAllByUserId(userId)).thenReturn(myRecipes);
        List<MyRecipe> result = myRecipeService.getAllRecipesByUserId(userId);
        assertEquals(myRecipes, result);
        verify(myRecipeRepository, times(1)).findAllByUserId(userId);
    }


    @Test
    public void deleteMyRecipe_thenDeleteRecipeAndRelatedFoodItems() {
        UUID recipeId = UUID.randomUUID();
        FoodItem foodItem = FoodItem.builder()
                .id(UUID.randomUUID())
                .name("banana")
                .build();
        FoodItem foodItemTwo = FoodItem.builder()
                .id(UUID.randomUUID())
                .name("apple")
                .build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItem);
        foodItems.add(foodItemTwo);
        when(foodService.getFoodItemsByRecipeId(recipeId)).thenReturn(foodItems);
        myRecipeService.deleteMyRecipe(recipeId);
        verify(foodService, times(1)).deleteFoodItemById(foodItem.getId());
        verify(foodService, times(1)).deleteFoodItemById(foodItemTwo.getId());
        verify(myRecipeRepository, times(1)).deleteById(recipeId);
    }


    @Test
    public void deleteMyRecipe_whenNoRelatedFoodItems_thenDeleteRecipe() {
        UUID recipeId = UUID.randomUUID();
        List<FoodItem> foodItems = new ArrayList<>();
        when(foodService.getFoodItemsByRecipeId(recipeId)).thenReturn(foodItems);
        myRecipeService.deleteMyRecipe(recipeId);
        verify(foodService, never()).deleteFoodItemById(any(UUID.class));
        verify(myRecipeRepository, times(1)).deleteById(recipeId);
    }


    @Test
    public void getById_whenRecipeExists_thenReturnRecipe() {
        UUID recipeId = UUID.randomUUID();
        MyRecipe recipe = MyRecipe.builder()
                .id(recipeId)
                .build();
        when(myRecipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        MyRecipe result = myRecipeService.getById(recipeId);
        assertEquals(recipe, result);
        verify(myRecipeRepository, times(1)).findById(recipeId);
    }

    @Test
    public void getById_whenRecipeDoesNotExist_thenException() {
        UUID recipeId = UUID.randomUUID();
        when(myRecipeRepository.findById(recipeId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> myRecipeService.getById(recipeId));
        verify(myRecipeRepository, times(1)).findById(recipeId);
    }

    @Test
    public void editMyRecipe_thenRecipeExists_thenUpdateRecipe() {
        UUID recipeId = UUID.randomUUID();
        EditMyRecipeRequest editRequest = EditMyRecipeRequest.builder()
                .name("Updated Chicken Salad")
                .instructions("Updated instructions")
                .picture("updated_picture.jpg")
                .build();
        MyRecipe existingRecipe = MyRecipe.builder()
                .id(recipeId)
                .name("Chicken Salad")
                .instructions("Original instructions")
                .picture("original_picture.jpg")
                .build();
        when(myRecipeRepository.findById(recipeId)).thenReturn(Optional.of(existingRecipe));
        myRecipeService.editMyRecipe(recipeId, editRequest);
        assertEquals(editRequest.getName(), existingRecipe.getName());
        assertEquals(editRequest.getInstructions(), existingRecipe.getInstructions());
        assertEquals(editRequest.getPicture(), existingRecipe.getPicture());
        verify(myRecipeRepository, times(1)).save(existingRecipe);
    }


    @Test
    public void getAllPublicRecipes_whenListNotEmpty_thenReturnFilteredRecipes() {
        UUID userId = UUID.randomUUID();
        User userOne = User.builder().id(UUID.randomUUID()).build();
        User userTwo = User.builder().id(UUID.randomUUID()).build();
        MyRecipe recipeOne = MyRecipe.builder()
                .id(UUID.randomUUID())
                .publicRecipe(true)
                .user(userOne)
                .build();
        MyRecipe recipeTwo = MyRecipe
                .builder()
                .id(UUID.randomUUID())
                .publicRecipe(true)
                .user(userTwo)
                .build();
        List<MyRecipe> expectedResult = new ArrayList<>();
        expectedResult.add(recipeOne);
        expectedResult.add(recipeTwo);
        when(myRecipeRepository.findAllByPublicRecipe(true)).thenReturn(expectedResult);
        List<MyRecipe> result = myRecipeService.getAllPublicRecipes(userId);
        assertEquals(expectedResult, result);
        verify(myRecipeRepository, times(1)).findAllByPublicRecipe(true);
    }


    @Test
    public void createNewMyRecipeItem_thenCreateAndSaveMyRecipeItem() {
        UUID userId = UUID.randomUUID();
        Meal meal = Meal.builder()
                .id(UUID.randomUUID())
                .build();
        AddRecipeItemRequest addRecipeItemRequest = AddRecipeItemRequest.builder()
                .recipeItemName("Chicken Salad")
                .recipeItemQuantity(200.0)
                .build();
        MyRecipe myRecipe = MyRecipe.builder()
                .caloriesPerHundredGrams(150.0)
                .build();
        User user = User.builder().id(userId).build();
        when(myRecipeRepository.findByUserIdAndName(userId, "Chicken Salad")).thenReturn(Optional.of(myRecipe));
        when(userService.getById(userId)).thenReturn(user);
        MyRecipeItem result = myRecipeService.createNewMyRecipeItem(addRecipeItemRequest, userId, meal);
        assertEquals("Chicken Salad", result.getName());
        assertEquals(200.0, result.getQuantityInGrams());
        assertEquals(300.0, result.getCalories());
        assertEquals(user, result.getUser());
        assertEquals(meal, result.getMeal());
        verify(myRecipeItemRepository, times(1)).save(result);
    }


    @Test
    public void createNewMyRecipeItem_thenRoundCorrectly() {
        UUID userId = UUID.randomUUID();
        Meal meal = Meal.builder().id(UUID.randomUUID()).build();
        AddRecipeItemRequest addRecipeItemRequest = AddRecipeItemRequest.builder()
                .recipeItemName("Chicken Salad")
                .recipeItemQuantity(150.0)
                .build();
        MyRecipe myRecipe = MyRecipe.builder()
                .caloriesPerHundredGrams(123.456)
                .build();
        User user = User.builder().id(userId).build();
        when(myRecipeRepository.findByUserIdAndName(userId, "Chicken Salad")).thenReturn(Optional.of(myRecipe));
        when(userService.getById(userId)).thenReturn(user);
        MyRecipeItem result = myRecipeService.createNewMyRecipeItem(addRecipeItemRequest, userId, meal);
        assertEquals(185.18, result.getCalories());
        verify(myRecipeItemRepository, times(1)).save(result);
    }

    @Test
    public void calculateCaloriesPer100Grams_thenReturnCorrectCalories() {
        FoodItem foodItemOne = FoodItem.builder()
                .quantityInGrams(100.0)
                .calories(200.0)
                .build();
        FoodItem foodItemTwo = FoodItem.builder()
                .quantityInGrams(200.0)
                .calories(400.0)
                .build();
        List<FoodItem> foodItems = Arrays.asList(foodItemOne, foodItemTwo);
        MyRecipe recipe = MyRecipe.builder()
                .foodItems(foodItems)
                .build();
        double result = myRecipeService.calculateCaloriesPer100Grams(recipe);
        assertEquals(222.22, result);
    }

    @Test
    public void calculateCaloriesPer100Grams__whenFoodItemsEmpty_thenReturnZero() {
        List<FoodItem> foodItems = new ArrayList<>();
        MyRecipe recipe = MyRecipe.builder().foodItems(foodItems).build();
        double result = myRecipeService.calculateCaloriesPer100Grams(recipe);
        assertEquals(0.0, result);
    }

    @Test
    public void calculateCaloriesPer100Grams_whenFoodItemsNull_thenReturnZero() {
        MyRecipe recipe = MyRecipe.builder()
                .foodItems(null)
                .build();
        double result = myRecipeService.calculateCaloriesPer100Grams(recipe);
        assertEquals(0.0, result);
    }

    @Test
    public void calculateCaloriesPer100Grams_whenTotalWeightIsZero_thenReturnZero() {
        FoodItem foodItemOne = FoodItem.builder()
                .quantityInGrams(0.0)
                .calories(100.0)
                .build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItemOne);
        MyRecipe recipe = MyRecipe.builder()
                .foodItems(foodItems)
                .build();
        double result = myRecipeService.calculateCaloriesPer100Grams(recipe);
        assertEquals(0.0, result);
    }

    @Test
    public void calculateCaloriesPer100Grams_whenQuantityIsLargeNumber() {
        FoodItem foodItemOne = FoodItem.builder()
                .quantityInGrams(1000000.0)
                .calories(2000000.0)
                .build();
        FoodItem foodItemTwo = FoodItem.builder()
                .quantityInGrams(2000000.0)
                .calories(4000000.0)
                .build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItemOne);
        foodItems.add(foodItemTwo);
        MyRecipe recipe = MyRecipe.builder()
                .foodItems(foodItems)
                .build();
        double result = myRecipeService.calculateCaloriesPer100Grams(recipe);
        assertEquals(222.22, result);
    }


    @Test
    public void removeFoodItemFromRecipe_thenRemoveFoodItemAndUpdateCalories() {
        UUID recipeId = UUID.randomUUID();
        UUID foodItemId = UUID.randomUUID();
        FoodItem foodItem = FoodItem.builder()
                .id(foodItemId)
                .calories(100.0).build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItem);
        MyRecipe recipe = MyRecipe.builder()
                .id(recipeId)
                .totalCalories(200.0)
                .foodItems(foodItems).build();
        when(myRecipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(foodService.getFoodItemById(foodItemId)).thenReturn(foodItem);
        when(foodService.getFoodItemsByRecipeId(recipeId)).thenReturn(foodItems);
        myRecipeService.removeFoodItemFromRecipe(recipeId, foodItemId);
        assertEquals(100.0, recipe.getTotalCalories());
        assertEquals(0, recipe.getFoodItems().size());
        verify(foodService, times(1)).deleteFoodItemById(foodItemId);
        verify(myRecipeRepository, times(1)).save(recipe);
    }


    @Test
    public void removeFoodItemFromRecipe_whenMultipleFoodItems_thenRemoveFoodItemAndUpdateCalories() {
        UUID recipeId = UUID.randomUUID();
        UUID foodItemIdToRemove = UUID.randomUUID();
        FoodItem foodItemOne = FoodItem.builder()
                .id(foodItemIdToRemove)
                .calories(100.0)
                .build();
        FoodItem foodItemTwo = FoodItem.builder()
                .id(UUID.randomUUID())
                .calories(50.0)
                .build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItemOne);
        foodItems.add(foodItemTwo);
        MyRecipe recipe = MyRecipe.builder()
                .id(recipeId)
                .totalCalories(150.0)
                .foodItems(foodItems)
                .build();
        when(myRecipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(foodService.getFoodItemById(foodItemIdToRemove)).thenReturn(foodItemOne);
        when(foodService.getFoodItemsByRecipeId(recipeId)).thenReturn(foodItems);
        myRecipeService.removeFoodItemFromRecipe(recipeId, foodItemIdToRemove);
        assertEquals(50.0, recipe.getTotalCalories());
        assertEquals(1, recipe.getFoodItems().size());
        verify(foodService, times(1)).deleteFoodItemById(foodItemIdToRemove);
        verify(myRecipeRepository, times(1)).save(recipe);
    }


    @Test
    public void createNewRecipe_whenRecipeDoesNotExist_thenCreateAndSaveRecipe_() {
        UUID userId = UUID.randomUUID();
        CreateMyRecipeRequest createMyRecipeRequest = CreateMyRecipeRequest.builder()
                .name("chicken salad")
                .instructions("mix ingredients")
                .picture("chicken_salad.jpg")
                .recipePublic(true)
                .build();
        User user = User.builder().id(userId).build();
        MyRecipe savedRecipe = MyRecipe.builder()
                .name("chicken salad")
                .instructions("mix ingredients")
                .user(user)
                .picture("chicken_salad.jpg")
                .foodItems(new ArrayList<>())
                .publicRecipe(true)
                .build();
        when(myRecipeRepository.findByUserIdAndName(userId, "chicken salad")).thenReturn(Optional.empty());
        when(userService.getById(userId)).thenReturn(user);
        when(myRecipeRepository.save(any(MyRecipe.class))).thenReturn(savedRecipe);
        MyRecipe result = myRecipeService.createNewRecipe(createMyRecipeRequest, userId);
        assertEquals(savedRecipe, result);
        verify(myRecipeRepository, times(1)).save(any(MyRecipe.class));
    }


    @Test
    public void createNewRecipe_whenRecipeExists_thenException() {
        UUID userId = UUID.randomUUID();
        CreateMyRecipeRequest createRequest = CreateMyRecipeRequest.builder()
                .name("chicken salad")
                .instructions("mix ingredients")
                .picture("chicken_salad.jpg")
                .recipePublic(true)
                .build();
        MyRecipe existingRecipe = MyRecipe.builder().build();
        when(myRecipeRepository.findByUserIdAndName(userId, "chicken salad")).thenReturn(Optional.of(existingRecipe));
        assertThrows(MyRecipeAlreadyExistsException.class, () -> myRecipeService.createNewRecipe(createRequest, userId));
        verify(myRecipeRepository, never()).save(any(MyRecipe.class));
    }
}


