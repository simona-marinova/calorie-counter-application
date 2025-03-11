package app.web;

import app.food.model.Food;
import app.food.service.FoodService;
import app.myRecipe.model.MyRecipe;
import app.myRecipe.service.MyRecipeService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.CreateMyRecipeRequest;
import app.web.dto.EditMyRecipeRequest;
import app.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/my-recipes")
public class MyRecipeController {

    private final MyRecipeService myRecipeService;
    private final UserService userService;
    private final FoodService foodService;

    @Autowired
    public MyRecipeController(MyRecipeService myRecipeService, UserService userService, FoodService foodService) {
        this.myRecipeService = myRecipeService;
        this.userService = userService;
        this.foodService = foodService;
    }


    @GetMapping("")
    public ModelAndView viewMyRecipes() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("my-recipes");
        List<MyRecipe> myRecipes = myRecipeService.getAllRecipes();
        modelAndView.addObject("myRecipes", myRecipes);
        return modelAndView;
    }

    @GetMapping("/new")
    public ModelAndView createNewRecipe(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-my-recipe");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createMyRecipeRequest", new CreateMyRecipeRequest());

        return modelAndView;
    }

    @PostMapping()
    public String processCreateNewRecipeRequest(@Valid CreateMyRecipeRequest createMyRecipeRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (bindingResult.hasErrors()) {
            return "add-my-recipe";
        }
        User user = userService.getById(authenticationDetails.getUserId());
        myRecipeService.createNewRecipe(createMyRecipeRequest, user);

        return "redirect:/my-recipes";
    }


    @GetMapping("/{id}/add-food-items")
    public ModelAndView addFoodItemsToRecipe(@PathVariable UUID id) {
        MyRecipe myRecipe = myRecipeService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-food-items-to-recipe");
        List<Food> foods = foodService.getListOfAllFoods();
        modelAndView.addObject("foods", foods);
        modelAndView.addObject("addFoodItemRequest", new AddFoodItemRequest());
        modelAndView.addObject("myRecipe", myRecipe);
        return modelAndView;
    }

    @PutMapping("/{id}/add-food-items")
    public ModelAndView processAddFoodItemsToRecipeRequest(@PathVariable UUID id, @Valid AddFoodItemRequest addFoodItemRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (bindingResult.hasErrors()) {
            MyRecipe myRecipe = myRecipeService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-food-items-to-recipe");
            List<Food> foods = foodService.getListOfAllFoods();
            modelAndView.addObject("foods", foods);
            modelAndView.addObject("addFoodItemRequest", addFoodItemRequest);
            modelAndView.addObject("myRecipe", myRecipe);
            return modelAndView;
        }
        MyRecipe myRecipe = myRecipeService.getById(id);
        User user = userService.getById(authenticationDetails.getUserId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequest, user, myRecipe);

        return new ModelAndView("redirect:/my-recipes/{id}/add-food-items");
    }


    @GetMapping("/{id}/edit")
    public ModelAndView editMyRecipe(@PathVariable UUID id) {
        MyRecipe myRecipe = myRecipeService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-my-recipe");
        modelAndView.addObject("myRecipe", myRecipe);
        modelAndView.addObject("editMyRecipeRequest", DtoMapper.mapMyRecipeToEditMyRecipeRequest(myRecipe));
        return modelAndView;
    }


    @PutMapping("/{id}/edit")
    public ModelAndView processEditMyRecipeRequest(@PathVariable UUID id, @Valid EditMyRecipeRequest editMyRecipeRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            MyRecipe myRecipe = myRecipeService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-my-recipe");
            modelAndView.addObject("myRecipe", myRecipe);
            modelAndView.addObject("editMyRecipeRequest", editMyRecipeRequest);
            return modelAndView;
        }
        myRecipeService.editMyRecipe(id, editMyRecipeRequest);
        return new ModelAndView("redirect:/my-recipes");

    }


    @GetMapping("/{id}/view")
    public ModelAndView viewRecipe(@PathVariable UUID id) {
        MyRecipe myRecipe = myRecipeService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view-recipe");
        modelAndView.addObject("myRecipe", myRecipe);
        return modelAndView;
    }


    @PutMapping("/{id}/edit/food-items/{foodItemId}/delete")
    public String removeFoodItemFromRecipe(@PathVariable UUID id, @PathVariable UUID foodItemId) {
        myRecipeService.removeFoodItemFromRecipe(id, foodItemId);

        return "redirect:/my-recipes/{id}/edit";
    }


    @DeleteMapping("{id}/delete")
    public String deleteMyRecipe(@PathVariable UUID id) {

        myRecipeService.deleteMyRecipe(id);
        return "redirect:/my-recipes";
    }

}
