package app.web;

import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.service.DailyStatisticsService;
import app.meal.model.Meal;
import app.meal.service.MealService;
import app.myRecipe.service.MyRecipeService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;

@Controller
public class IndexController {

    private final UserService userService;
    private final MealService mealService;
    private final DailyStatisticsService dailyStatisticsService;

    @Autowired
    public IndexController(UserService userService, MealService mealService, DailyStatisticsService dailyStatisticsService) {
        this.userService = userService;
        this.mealService = mealService;
        this.dailyStatisticsService = dailyStatisticsService;
    }


    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }


    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        return modelAndView;
    }


    @PostMapping("/register")
    public ModelAndView processRegisterRequest(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }
        userService.register(registerRequest);
        return  new ModelAndView("redirect:/login");
    }


    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String errorParam) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        modelAndView.addObject("loginRequest", new LoginRequest());

        if (errorParam != null) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }
        return modelAndView;
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("home");
        modelAndView.addObject("user", user);
        mealService.createMealsForTheDay(user.getId());
        LocalDate today = LocalDate.now();
        List<Meal> meals = mealService.getMealsByUserIdAndDate(user.getId(), today);
        DailyStatistics dailyStatistics = dailyStatisticsService.createDailyStatistics(authenticationDetails.getUserId());
        modelAndView.addObject("dailyStatistics", dailyStatistics);
        return modelAndView;
    }

    @GetMapping("/how-to-use")
    public String getHowToUseTheAppPage() {
        return "how-to-use-the-app";
    }

    @GetMapping("/tips")
    public String getTipsPage() {
        return "tips";
    }


    @GetMapping("/calories-in-foods")
    public String getCaloriesInFoodsPage() {
        return "calories-in-foods";
    }
}
