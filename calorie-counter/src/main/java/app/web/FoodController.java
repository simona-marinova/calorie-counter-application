package app.web;

import app.food.model.Food;
import app.food.service.FoodService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateFoodRequest;
import app.web.dto.FoodEditRequest;
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
@RequestMapping("/foods")
public class FoodController {

    private final UserService userService;
    private final FoodService foodService;

    @Autowired
    public FoodController(UserService userService, FoodService foodService) {
        this.userService = userService;
        this.foodService = foodService;
    }

    @GetMapping("/new")
    public ModelAndView addNewFoodPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {

        User user = userService.getById(authenticationDetails.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-food");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createFoodRequest", new CreateFoodRequest());
        return modelAndView;
    }


    @PostMapping()
    public String createNewFood(@Valid CreateFoodRequest createFoodRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        if (bindingResult.hasErrors()) {
            return "add-food";
        }
        User user = userService.getById(authenticationDetails.getUserId());
        foodService.createNewFood(createFoodRequest, user);

        return "redirect:/foods";
    }

    @GetMapping("")
    public ModelAndView readFoods(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("foods");
        List<Food> foods=foodService.getListOfAllFoodsForUserId(authenticationDetails.getUserId());
        modelAndView.addObject("foods", foods);
        return modelAndView;
    }


    @GetMapping("/{id}")
    public ModelAndView editFood(@PathVariable UUID id) {
        Food food = foodService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-food");
        modelAndView.addObject("food", food);
        modelAndView.addObject("foodEditRequest", DtoMapper.mapFoodToFoodEditRequest(food));
        return modelAndView;
    }



    @PutMapping("/{id}")
    public ModelAndView updateFood(@PathVariable UUID id, @Valid FoodEditRequest foodEditRequest, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            Food food = foodService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-food");
            modelAndView.addObject("food", food);
            modelAndView.addObject("foodEditRequest",foodEditRequest);
            return modelAndView;
        }
        foodService.editFoodDetails(id, foodEditRequest);
        return new ModelAndView("redirect:/foods");

    }


    @DeleteMapping("/{id}")
    public String deleteFood(@PathVariable UUID id) {
        foodService.deleteFoodById(id);

        return "redirect:/foods";

    }

}
