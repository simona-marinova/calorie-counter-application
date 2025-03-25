package app.meal;

import app.dailyStatistics.service.DailyStatisticsService;
import app.exception.DomainException;
import app.food.model.FoodItem;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.meal.model.MealType;
import app.meal.repository.MealRepository;
import app.meal.service.MealService;
import app.myRecipe.model.MyRecipeItem;
import app.myRecipe.service.MyRecipeService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.AddRecipeItemRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MealUTest {

    @Mock
    private MyRecipeService myRecipeService;
    @Mock
    private MealRepository mealRepository;
    @Mock
    private FoodService foodService;
    @Mock
    private UserService userService;
    @Mock
    private DailyStatisticsService dailyStatisticsService;


    @InjectMocks
    private MealService mealService;


    @Test
    public void getMealById__whenMealExists_thenReturnMeal() {
        UUID mealId = UUID.randomUUID();
        Meal meal = Meal.builder()
                .id(mealId)
                .mealType(MealType.BREAKFAST).build();
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        Meal result = mealService.getMealById(mealId);
        assertEquals(meal, result);
        verify(mealRepository, times(1)).findById(mealId);
    }

    @Test
    public void getMealById_whenMealDoesNotExist_thenException() {
        UUID mealId = UUID.randomUUID();
        when(mealRepository.findById(mealId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> mealService.getMealById(mealId));
        verify(mealRepository, times(1)).findById(mealId);
    }

    @Test
    public void getMealsByUserIdAndDate_thenReturnListOfMeals() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        Meal breakfast = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.BREAKFAST)
                .build();
        Meal lunch = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.LUNCH)
                .build();
        Meal snack = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.SNACK)
                .build();
        Meal dinner = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.DINNER)
                .build();
        List<Meal> meals = new ArrayList<>();
        meals.add(breakfast);
        meals.add(lunch);
        meals.add(dinner);
        meals.add(snack);
        when(mealRepository.findAllByUserIdAndDate(userId, date)).thenReturn(meals);
        List<Meal> result = mealService.getMealsByUserIdAndDate(userId, date);
        assertEquals(meals, result);
        assertEquals(4, result.size());
        verify(mealRepository, times(1)).findAllByUserIdAndDate(userId, date);
    }


    @Test
    public void getMealsByUserIdAndDate_whenNoMealsFound_thenReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        LocalDate date = LocalDate.now();
        List<Meal> meals = new ArrayList<>();
        when(mealRepository.findAllByUserIdAndDate(userId, date)).thenReturn(meals);
        List<Meal> result = mealService.getMealsByUserIdAndDate(userId, date);
        assertEquals(meals, result);
        verify(mealRepository, times(1)).findAllByUserIdAndDate(userId, date);
    }


    @Test
    public void createMealsForTheDay_whenNoMealsExist_thenCreateMeals() {
        UUID userId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        User user = User.builder().id(userId).build();
        when(userService.getById(userId)).thenReturn(user);
        when(mealService.getMealsByUserIdAndDate(userId, today)).thenReturn(new ArrayList<>());
        mealService.createMealsForTheDay(userId);
        verify(mealRepository, times(4)).save(any(Meal.class));
        verify(mealRepository, times(1)).save(argThat(meal -> meal.getMealType() == MealType.BREAKFAST));
        verify(mealRepository, times(1)).save(argThat(meal -> meal.getMealType() == MealType.LUNCH));
        verify(mealRepository, times(1)).save(argThat(meal -> meal.getMealType() == MealType.DINNER));
        verify(mealRepository, times(1)).save(argThat(meal -> meal.getMealType() == MealType.SNACK));
    }


    @Test
    public void createMealsForTheDay_whenMealsExist_thenNoMealsAreCreated() {
        UUID userId = UUID.randomUUID();
        LocalDate today = LocalDate.now();
        User user = User.builder().id(userId).build();
        Meal breakfast = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.BREAKFAST)
                .build();
        Meal lunch = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.LUNCH)
                .build();
        Meal snack = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.SNACK)
                .build();
        Meal dinner = Meal.builder()
                .id(UUID.randomUUID())
                .mealType(MealType.DINNER)
                .build();
        List<Meal> meals = new ArrayList<>();
        meals.add(breakfast);
        meals.add(lunch);
        meals.add(dinner);
        meals.add(snack);
        when(userService.getById(userId)).thenReturn(user);
        when(mealService.getMealsByUserIdAndDate(userId, today)).thenReturn(meals);
        mealService.createMealsForTheDay(userId);
        verify(mealRepository, never()).save(any(Meal.class));
    }

    @Test
    public void deleteMealById_whenEmptyFoodAndRecipeItems() {
        UUID mealId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        double calories = 100.0;
        Meal meal = Meal.builder()
                .id(mealId)
                .calories(calories)
                .foodItems(new ArrayList<>())
                .myRecipeItems(new ArrayList<>())
                .build();
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        mealService.deleteMealById(mealId, userId);
        assertEquals(0.0, meal.getCalories());
        assertEquals(0, meal.getFoodItems().size());
        assertEquals(0, meal.getMyRecipeItems().size());
        verify(foodService, never()).deleteListOfFoodItems(anyList());
        verify(myRecipeService, never()).deleteListOfMyRecipeItems(anyList());
        verify(dailyStatisticsService, times(1)).updateRemainingCalories(userId, calories);
        verify(dailyStatisticsService, times(1)).lowerCaloriesConsumed(userId, calories);
        verify(mealRepository, times(1)).save(meal);
    }


    @Test
    public void deleteMealById_thenDeleteMealAndRelatedItems() {
        UUID mealId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        double calories = 100.0;
        FoodItem foodItem = FoodItem.builder()
                .id(UUID.randomUUID())
                .build();
        MyRecipeItem myRecipeItem = MyRecipeItem
                .builder()
                .id(UUID.randomUUID())
                .build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItem);
        List<MyRecipeItem> myRecipeItems = new ArrayList<>();
        myRecipeItems.add(myRecipeItem);
        Meal meal = Meal.builder()
                .id(mealId)
                .calories(calories)
                .foodItems(foodItems)
                .myRecipeItems(myRecipeItems)
                .build();
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        mealService.deleteMealById(mealId, userId);
        assertEquals(0.0, meal.getCalories());
        assertEquals(0, meal.getFoodItems().size());
        assertEquals(0, meal.getMyRecipeItems().size());
        verify(foodService, times(1)).deleteListOfFoodItems(foodItems);
        verify(myRecipeService, times(1)).deleteListOfMyRecipeItems(myRecipeItems);
        verify(dailyStatisticsService, times(1)).updateRemainingCalories(userId, calories);
        verify(dailyStatisticsService, times(1)).lowerCaloriesConsumed(userId, calories);
        verify(mealRepository, times(1)).save(meal);
    }

    @Test
    public void addRecipeItemsToMeal_thenAddRecipeItemAndSaveMeal() {
        UUID userId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();
        AddRecipeItemRequest addRecipeItemRequest = AddRecipeItemRequest.builder().build();
        Meal meal = Meal.builder()
                .id(mealId)
                .calories(100.0)
                .myRecipeItems(new ArrayList<>())
                .build();
        MyRecipeItem myRecipeItem = MyRecipeItem.builder()
                .calories(50.555).build();
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(myRecipeService.createNewMyRecipeItem(addRecipeItemRequest, userId, meal)).thenReturn(myRecipeItem);
        mealService.addRecipeItemsToMeal(addRecipeItemRequest, userId, mealId);
        assertEquals(150.56, meal.getCalories());
        assertEquals(1, meal.getMyRecipeItems().size());
        assertEquals(myRecipeItem, meal.getMyRecipeItems().get(0));
        verify(mealRepository, times(1)).save(meal);
        verify(dailyStatisticsService, times(1)).updateConsumedAndRemainingCalories(50.555, userId);
    }

    @Test
    public void addRecipeItemsToMeal_whenEmptyRecipeItemsList() {
        UUID userId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();
        AddRecipeItemRequest addRecipeItemRequest = AddRecipeItemRequest.builder().build();
        Meal meal = Meal.builder().id(mealId).calories(100.0).myRecipeItems(new ArrayList<>()).build();
        MyRecipeItem myRecipeItem = MyRecipeItem.builder().calories(50.55).build();
        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(myRecipeService.createNewMyRecipeItem(addRecipeItemRequest, userId, meal)).thenReturn(myRecipeItem);
        mealService.addRecipeItemsToMeal(addRecipeItemRequest, userId, mealId);
        assertEquals(150.55, meal.getCalories());
        assertEquals(1, meal.getMyRecipeItems().size());
        assertEquals(myRecipeItem, meal.getMyRecipeItems().get(0));
        verify(mealRepository, times(1)).save(meal);
        verify(dailyStatisticsService, times(1)).updateConsumedAndRemainingCalories(50.55, userId);
    }

    @Test
    void addFoodItemsToMeal_thenFoodItemAreAddedAndUpdatedMealCalories() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Meal meal = Meal.builder()
                .id(UUID.randomUUID())
                .calories(100.0)
                .build();
         AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder().build();
        FoodItem foodItem = FoodItem.builder()
                .calories(50.0)
                .build();
        List<FoodItem> foodItems = new ArrayList<>();
        foodItems.add(foodItem);
        when(foodService.createNewFoodItemForMeal(addFoodItemRequest, user.getId(), meal)).thenReturn(foodItem);
        when(mealRepository.findById(meal.getId())).thenReturn(Optional.of(meal));
        when(foodService.getFoodItemsByMealId(meal.getId())).thenReturn(foodItems);
        mealService.addFoodItemsToMeal(addFoodItemRequest, user.getId(), meal.getId());
        assertEquals(150.0, meal.getCalories());
        verify(mealRepository, times(1)).save(meal);
        verify(dailyStatisticsService, times(1)).updateConsumedAndRemainingCalories(50.0, user.getId());
        assertEquals(2, meal.getFoodItems().size());
    }

}



