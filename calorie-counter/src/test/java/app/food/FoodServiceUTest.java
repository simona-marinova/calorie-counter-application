package app.food;

import app.exception.DomainException;
import app.exception.FoodAlreadyExistsException;
import app.exception.FoodItemAlreadyExistsException;
import app.food.model.Food;
import app.food.model.FoodItem;
import app.food.repository.FoodItemRepository;
import app.food.repository.FoodRepository;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.myRecipe.model.MyRecipe;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.CreateFoodRequest;
import app.web.dto.FoodEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceUTest {

    @Mock
    private FoodRepository foodRepository;
    @Mock
    private FoodItemRepository foodItemRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private FoodService foodService;

    @Test
    public void getListOfAllFoodsForUserId_whenNotEmpty_returnListOfFoods() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();
        Food foodTwo = Food.builder()
                .id(UUID.randomUUID())
                .user(user)
                .build();
        List<Food> foods = new ArrayList<>();
        foods.add(food);
        foods.add(foodTwo);
        when(foodRepository.findAllByUserIdOrderByName(user.getId())).thenReturn(foods);
        List<Food> result = foodService.getListOfAllFoodsForUserId(user.getId());
        assertEquals(foods, result);
        verify(foodRepository, times(1)).findAllByUserIdOrderByName(user.getId());
    }

    @Test
    public void getListOfAllFoodsForUserId_whenListIsEmpty_thenReturnEmptyList() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        when(foodRepository.findAllByUserIdOrderByName(user.getId())).thenReturn(List.of());
        List<Food> result = foodService.getListOfAllFoodsForUserId(user.getId());
        assertTrue(result.isEmpty());
        verify(foodRepository, times(1)).findAllByUserIdOrderByName(user.getId());
    }

    @Test
    public void createNewFood_whenFoodDoesNotAlreadyExist_thenCreateFoodAndSaveToRepository() {
        UUID userId = UUID.randomUUID();
        CreateFoodRequest createFoodRequest = CreateFoodRequest.builder()
                .name("Apple")
                .picture("apple.jpg")
                .caloriesPerHundredGrams(52)
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        Food food = Food.builder()
                .name(createFoodRequest.getName())
                .picture(createFoodRequest.getPicture())
                .caloriesPerHundredGrams(createFoodRequest.getCaloriesPerHundredGrams())
                .user(user)
                .build();
        when(userService.getById(userId)).thenReturn(user);
        when(foodRepository.findByUserIdAndName(userId, createFoodRequest.getName())).thenReturn(Optional.empty());
        when(foodRepository.save(any(Food.class))).thenReturn(food);
        foodService.createNewFood(createFoodRequest, userId);
        verify(foodRepository, times(1)).save(any(Food.class));
    }


    @Test
    public void createNewFood_whenFoodAlreadyExists_thenException() {
        UUID userId = UUID.randomUUID();
        CreateFoodRequest createFoodRequest = CreateFoodRequest.builder()
                .name("banana")
                .picture("banana.jpg")
                .caloriesPerHundredGrams(52)
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        Food food = Food.builder()
                .name(createFoodRequest.getName())
                .picture(createFoodRequest.getPicture())
                .caloriesPerHundredGrams(createFoodRequest.getCaloriesPerHundredGrams())
                .user(user)
                .build();
        when(userService.getById(userId)).thenReturn(user);
        when(foodRepository.findByUserIdAndName(userId, createFoodRequest.getName())).thenReturn(Optional.of(food));
        assertThrows(FoodAlreadyExistsException.class, () -> foodService.createNewFood(createFoodRequest, userId));
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    public void createNewFood_whenUserDoesNotExist_thenException() {
        UUID userId = UUID.randomUUID();
        CreateFoodRequest createFoodRequest = CreateFoodRequest.builder()
                .name("banana")
                .picture("banana.jpg")
                .caloriesPerHundredGrams(52)
                .build();
        when(userService.getById(userId)).thenThrow(new RuntimeException("User with id [%s] does not exist".formatted(userId)));
        assertThrows(RuntimeException.class, () -> foodService.createNewFood(createFoodRequest, userId));
        verify(foodRepository, never()).save(any(Food.class));
    }


    @Test
    public void editFoodDetails_whenFoodExists_thenEditAndSaveItToRepository() {
        UUID foodId = UUID.randomUUID();
        Food food = Food.builder()
                .id(foodId)
                .name("apple")
                .caloriesPerHundredGrams(52)
                .picture("apple.jpg")
                .build();
        FoodEditRequest foodEditRequest = FoodEditRequest.builder()
                .name("apple")
                .caloriesPerHundredGrams(60)
                .picture("new_apple.jpg")
                .build();
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
        foodService.editFoodDetails(foodId, foodEditRequest);
        assertEquals(foodEditRequest.getName(), food.getName());
        assertEquals(foodEditRequest.getCaloriesPerHundredGrams(), food.getCaloriesPerHundredGrams());
        assertEquals(foodEditRequest.getPicture(), food.getPicture());
        verify(foodRepository, times(1)).save(food);
    }

    @Test
    public void editFoodDetails_whenFoodDoesNotExist_thenException() {
        UUID foodId = UUID.randomUUID();
        FoodEditRequest foodEditRequest = FoodEditRequest.builder()
                .name("apple")
                .caloriesPerHundredGrams(60)
                .picture("new_apple.jpg")
                .build();
        when(foodRepository.findById(foodId)).thenThrow(new DomainException("Food with id [%s] does not exist".formatted(foodId)));
        assertThrows(RuntimeException.class, () -> foodService.editFoodDetails(foodId, foodEditRequest));
        verify(foodRepository, never()).save(any(Food.class));
    }

    @Test
    public void getFoodCalories_whenFoodExists_thenReturnCaloriesPerHundredGrams() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        String foodItemName = "apple";
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .name(foodItemName)
                .caloriesPerHundredGrams(52.0)
                .user(user)
                .build();
        when(foodRepository.findByUserIdAndName(userId, foodItemName)).thenReturn(Optional.of(food));
        double calories = foodService.getFoodCalories(userId, foodItemName);
        assertEquals(52.0, calories);
        verify(foodRepository, times(1)).findByUserIdAndName(userId, foodItemName);
    }

    @Test
    public void getFoodCalories_whenFoodDoesNotExist_thenException() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        String foodItemName = "apple";
        when(foodRepository.findByUserIdAndName(userId, foodItemName)).thenThrow(new DomainException("Food with name [%s] does not exist.".formatted(foodItemName)));
        assertThrows(RuntimeException.class, () -> foodService.getFoodCalories(userId, foodItemName));
        verify(foodRepository, times(1)).findByUserIdAndName(userId, foodItemName);
    }

    @Test
    public void createNewFoodItemForMeal_createFoodItemAndSaveToRepository() {
        UUID userId = UUID.randomUUID();
        Meal meal = Meal.builder()
                .id(UUID.randomUUID())
                .build();
        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(150.0)
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        Food food = Food.builder()
                .name("apple")
                .caloriesPerHundredGrams(52.0)
                .build();
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName())).thenReturn(Optional.of(food));
        double calories = foodService.getFoodCalories(userId, addFoodItemRequest.getFoodItemName());
        assertEquals(52.0, calories);
        when(userService.getById(userId)).thenReturn(user);
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName())).thenReturn(Optional.of(food));
        FoodItem foodItem = foodService.createNewFoodItemForMeal(addFoodItemRequest, userId, meal);
        assertEquals(addFoodItemRequest.getFoodItemName(), foodItem.getName());
        assertEquals(addFoodItemRequest.getFoodItemQuantity(), foodItem.getQuantityInGrams());
        assertEquals(78.0, foodItem.getCalories());
        assertEquals(user, foodItem.getUser());
        assertEquals(meal, foodItem.getMeal());
        verify(foodItemRepository, times(1)).save(foodItem);
    }


    @Test
    public void createNewFoodItemForMeal_whenFoodDoesNotExist_thenException() {
        UUID userId = UUID.randomUUID();
        Meal meal = Meal.builder().id(UUID.randomUUID()).build();
        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(150.0)
                .build();
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName()))
                .thenReturn(Optional.empty());
        assertThrows(DomainException.class,
                () -> foodService.createNewFoodItemForMeal(addFoodItemRequest, userId, meal));
        verify(foodItemRepository, never()).save(any(FoodItem.class));
    }

    @Test
    public void createNewFoodItemForRecipe_whenFoodItemDoesNotExist_thenException() {
        UUID userId = UUID.randomUUID();
        MyRecipe myRecipe = MyRecipe.builder().id(UUID.randomUUID()).build();
        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(150.0)
                .build();
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName()))
                .thenReturn(Optional.empty());
        assertThrows(DomainException.class,
                () -> foodService.createNewFoodItemForRecipe(addFoodItemRequest, userId, myRecipe));
        verify(foodItemRepository, never()).save(any(FoodItem.class));
    }

    @Test
    public void createNewFoodItemForRecipe_createFoodItemAndSaveToRepository() {
        UUID userId = UUID.randomUUID();
        MyRecipe myRecipe = MyRecipe.builder()
                .id(UUID.randomUUID())
                .build();
        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(150.0)
                .build();
        User user = User.builder()
                .id(userId)
                .build();
        Food food = Food.builder()
                .name("apple")
                .caloriesPerHundredGrams(52.0)
                .build();
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName())).thenReturn(Optional.of(food));
        double calories = foodService.getFoodCalories(userId, addFoodItemRequest.getFoodItemName());
        assertEquals(52.0, calories);
        when(userService.getById(userId)).thenReturn(user);
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName())).thenReturn(Optional.of(food));
        FoodItem foodItem = foodService.createNewFoodItemForRecipe(addFoodItemRequest, userId, myRecipe);
        assertEquals(addFoodItemRequest.getFoodItemName(), foodItem.getName());
        assertEquals(addFoodItemRequest.getFoodItemQuantity(), foodItem.getQuantityInGrams());
        assertEquals(78.0, foodItem.getCalories());
        assertEquals(user, foodItem.getUser());
        assertEquals(myRecipe, foodItem.getMyRecipe());
        verify(foodItemRepository, times(1)).save(foodItem);
    }

    @Test
    public void createNewFoodItemForRecipe_whenFoodItemAlreadyExists_thenException() {
        UUID userId = UUID.randomUUID();
        MyRecipe myRecipe = MyRecipe.builder().id(UUID.randomUUID()).build();
        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("apple")
                .foodItemQuantity(150.0)
                .build();
        Food food = Food.builder()
                .caloriesPerHundredGrams(52.0)
                .build();
        FoodItem existingFoodItem = FoodItem.builder()
                .quantityInGrams(150.0)
                .build();
        when(foodRepository.findByUserIdAndName(userId, addFoodItemRequest.getFoodItemName()))
                .thenReturn(Optional.of(food));
        when(foodItemRepository.findByMyRecipeIdAndName(myRecipe.getId(), addFoodItemRequest.getFoodItemName()))
                .thenReturn(Optional.of(existingFoodItem));
        assertThrows(FoodItemAlreadyExistsException.class,
                () -> foodService.createNewFoodItemForRecipe(addFoodItemRequest, userId, myRecipe));
        verify(foodItemRepository, never()).save(any(FoodItem.class));
    }

    @Test
    public void deleteFoodById_FoodIsDeletedFromRepository() {
        UUID foodId = UUID.randomUUID();
        foodService.deleteFoodById(foodId);
        verify(foodRepository, times(1)).deleteById(foodId);
    }

    @Test
    public void getFoodItemsByMealId_shouldReturnListOfFoodItems() {
        UUID mealId = UUID.randomUUID();
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
        when(foodItemRepository.findAllByMealId(mealId)).thenReturn(foodItems);
        List<FoodItem> result = foodService.getFoodItemsByMealId(mealId);
        assertEquals(foodItems, result);
        verify(foodItemRepository, times(1)).findAllByMealId(mealId);
    }

    @Test
    public void getFoodItemsByMealId_whenNoFoodItemsFound_thenReturnEmptyList() {
        UUID mealId = UUID.randomUUID();
        List<FoodItem> foodItems = new ArrayList<>();
        when(foodItemRepository.findAllByMealId(mealId)).thenReturn(List.of());
        List<FoodItem> result = foodService.getFoodItemsByMealId(mealId);
        assertEquals(foodItems, result);
        verify(foodItemRepository, times(1)).findAllByMealId(mealId);
    }

    @Test
    public void deleteListOfFoodItems_thenFoodItemsAreDeleted() {
        FoodItem foodItemOne = FoodItem.builder()
                .id(UUID.randomUUID())
                .name("apple").build();
        FoodItem foodItemTwo = FoodItem.builder()
                .id(UUID.randomUUID())
                .name("banana").build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItemOne);
        foodItems.add(foodItemTwo);
        foodService.deleteListOfFoodItems(foodItems);
        verify(foodItemRepository, times(1)).deleteById(foodItemOne.getId());
        verify(foodItemRepository, times(1)).deleteById(foodItemTwo.getId());
    }

    @Test
    public void deleteListOfFoodItems_whenListIsNull() {
        foodService.deleteListOfFoodItems(null);
        verify(foodItemRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    public void getFoodItemsByRecipeId_thenReturnFoodItems() {
        UUID recipeId = UUID.randomUUID();
        FoodItem foodItemOne = FoodItem.builder()
                .id(UUID.randomUUID())
                .name("apple").build();
        FoodItem foodItemTwo = FoodItem.builder()
                .id(UUID.randomUUID())
                .name("banana").build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItemOne);
        foodItems.add(foodItemTwo);
        when(foodItemRepository.findAllByMyRecipeId(recipeId)).thenReturn(foodItems);
        List<FoodItem> result = foodService.getFoodItemsByRecipeId(recipeId);
        assertEquals(foodItems, result);
        verify(foodItemRepository, times(1)).findAllByMyRecipeId(recipeId);
    }

    @Test
    public void getFoodItemsByRecipeId_whenNoFoodItemsFound_thenReturnEmptyList_() {
        UUID recipeId = UUID.randomUUID();
        List<FoodItem> foodItems = new ArrayList<>();
        when(foodItemRepository.findAllByMyRecipeId(recipeId)).thenReturn(foodItems);
        List<FoodItem> result = foodService.getFoodItemsByRecipeId(recipeId);
        assertEquals(foodItems, result);
        verify(foodItemRepository, times(1)).findAllByMyRecipeId(recipeId);
    }

    @Test
    public void getFoodItemById_whenFoodItemExists_thenReturnFoodItem_() {
        UUID foodItemId = UUID.randomUUID();
        FoodItem foodItem = FoodItem.builder()
                .id(foodItemId)
                .name("apple")
                .build();
        when(foodItemRepository.findById(foodItemId)).thenReturn(Optional.of(foodItem));
        FoodItem result = foodService.getFoodItemById(foodItemId);
        assertEquals(foodItem, result);
        verify(foodItemRepository, times(1)).findById(foodItemId);
    }

    @Test
    public void getFoodItemById_shouldThrowDomainException_whenFoodItemDoesNotExist() {
        UUID foodItemId = UUID.randomUUID();
        when(foodItemRepository.findById(foodItemId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> foodService.getFoodItemById(foodItemId));
        verify(foodItemRepository, times(1)).findById(foodItemId);
    }


    @Test
    public void deleteFoodItemById_thenFoodItemIsDeleted() {
        UUID foodItemId = UUID.randomUUID();
        foodService.deleteFoodItemById(foodItemId);
        verify(foodItemRepository, times(1)).deleteById(foodItemId);
    }


    @Test
    public void getAllFoods_thenListOfFoodsIsReturned() {
        Food food = Food.builder()
                .id(UUID.randomUUID())
                .build();
        Food foodTwo = Food.builder()
                .id(UUID.randomUUID())
                .build();
        List<Food> foods = new ArrayList<>();
        foods.add(food);
        foods.add(foodTwo);
        when(foodRepository.findAll()).thenReturn(foods);
        List<Food> result = foodService.getAllFoods();
        assertEquals(foods, result);
        verify(foodRepository, times(1)).findAll();
    }


    @Test
    public void getAllFoods_whenNoFoodsFound_thenReturnEmptyList() {
        List<Food> foods = new ArrayList<>();
        when(foodRepository.findAll()).thenReturn(foods);
        List<Food> result = foodService.getAllFoods();
        assertEquals(foods, result);
        verify(foodRepository, times(1)).findAll();
    }
}