package app.web;

import app.food.model.Food;
import app.food.service.FoodService;
import app.meal.model.Meal;
import app.meal.service.MealService;
import app.myRecipe.model.MyRecipe;
import app.myRecipe.service.MyRecipeService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.AddRecipeItemRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/meals")
public class MealController {

    private final UserService userService;
    private final MealService mealService;
    private final FoodService foodService;
    private final MyRecipeService myRecipeService;

    @Autowired
    public MealController(UserService userService, MealService mealService, FoodService foodService, MyRecipeService myRecipeService) {
        this.userService = userService;
        this.mealService = mealService;
        this.foodService = foodService;
        this.myRecipeService = myRecipeService;
    }


    @GetMapping()
    public ModelAndView viewMeals(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("meals");
        LocalDate today = LocalDate.now();
        List<Meal> meals = mealService.getMealsByUserIdAndDate(authenticationDetails.getUserId(), today);
        modelAndView.addObject("today", today);
        modelAndView.addObject("meals", meals);
        return modelAndView;
    }


    @GetMapping("/{id}/add-food-items")
    public ModelAndView addFoodItemsToMeal(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        Meal meal = mealService.getMealById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-food-items-to-meal");
        modelAndView.addObject("meal", meal);
        modelAndView.addObject("addFoodItemRequest", new AddFoodItemRequest());
        List<Food> foods = foodService.getListOfAllFoodsForUserId(authenticationDetails.getUserId());
        modelAndView.addObject("foods", foods);
        return modelAndView;
    }


    @PutMapping("/{id}/add-food-items")
    public ModelAndView processAddFoodItemsToMeal(@PathVariable UUID id, @Valid AddFoodItemRequest addFoodItemRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (bindingResult.hasErrors()) {
            Meal meal = mealService.getMealById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-food-items-to-meal");
            modelAndView.addObject("meal", meal);
            modelAndView.addObject("addFoodItemRequest", addFoodItemRequest);
            List<Food> foods = foodService.getListOfAllFoodsForUserId(authenticationDetails.getUserId());
            modelAndView.addObject("foods", foods);
            return modelAndView;
        }
        mealService.addFoodItemsToMeal(addFoodItemRequest, authenticationDetails.getUserId(), id);
        return new ModelAndView("redirect:/meals/{id}/add-food-items");
    }

    @GetMapping("/{id}/add-recipe-items")
    public ModelAndView addRecipeItemsToMeal(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        Meal meal = mealService.getMealById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-recipe-items-to-meal");
        modelAndView.addObject("meal", meal);
        modelAndView.addObject("addRecipeItemRequest", new AddRecipeItemRequest());
        List<MyRecipe> myRecipes = myRecipeService.getListOfAllRecipes();
        modelAndView.addObject("myRecipes", myRecipes);
        return modelAndView;
    }


    @PutMapping("/{id}/add-recipe-items")
    public ModelAndView processAddRecipeItemsToMeal(@PathVariable UUID id, @Valid AddRecipeItemRequest addRecipeItemRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (bindingResult.hasErrors()) {
            Meal meal = mealService.getMealById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-recipe-items-to-meal");
            modelAndView.addObject("meal", meal);
            modelAndView.addObject("addRecipeItemRequest", addRecipeItemRequest);
            List<MyRecipe> myRecipes = myRecipeService.getListOfAllRecipes();
            modelAndView.addObject("myRecipes", myRecipes);
            return modelAndView;
        }
        User user = userService.getById(authenticationDetails.getUserId());
        mealService.addRecipeItemsToMeal(addRecipeItemRequest, authenticationDetails.getUserId(), id);
        return new ModelAndView("redirect:/meals/{id}/add-recipe-items");
    }


    @DeleteMapping("/delete/{id}")
    public String removeMeal(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        UUID userId = authenticationDetails.getUserId();
        mealService.deleteMealById(id, userId);

        return "redirect:/meals";
    }


    @GetMapping("/view/{id}")
    public ModelAndView viewMeal(@PathVariable UUID id) {
        Meal meal = mealService.getMealById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view-meal");
        modelAndView.addObject("meal", meal);
        return modelAndView;
    }


}
