package app.web;

import app.activity.client.dto.ActivityResponse;
import app.activity.service.ActivityService;
import app.dailyStatistics.service.DailyStatisticsService;
import app.food.model.Food;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CaloriesBurnedRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final UserService userService;
    private final DailyStatisticsService dailyStatisticsService;

    @Autowired
    public ActivityController(ActivityService activityService, UserService userService, DailyStatisticsService dailyStatisticsService) {
        this.activityService = activityService;
        this.userService = userService;
        this.dailyStatisticsService = dailyStatisticsService;
    }

    @GetMapping("/new")
    public ModelAndView getCaloriesBurnedPage(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-activity");
        modelAndView.addObject("user", user);
        modelAndView.addObject("caloriesBurnedRequest", new CaloriesBurnedRequest());
        return modelAndView;
    }

    @PostMapping("/new")
    public ModelAndView createActivity(@AuthenticationPrincipal AuthenticationDetails authenticationDetails,  @Valid CaloriesBurnedRequest caloriesBurnedRequest, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            User user = userService.getById(authenticationDetails.getUserId());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("add-activity");
            modelAndView.addObject("user", user);
            modelAndView.addObject("caloriesBurnedRequest", caloriesBurnedRequest);
            return modelAndView;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-activity");
        modelAndView.addObject("caloriesBurnedRequest", caloriesBurnedRequest);
        double calories = activityService.saveActivity(authenticationDetails.getUserId(), caloriesBurnedRequest.getActivityType(), caloriesBurnedRequest.getDuration());
        caloriesBurnedRequest.setCaloriesBurned(calories);
        modelAndView.addObject("calories", calories);
        dailyStatisticsService.updateCaloriesBurned(authenticationDetails.getUserId(), caloriesBurnedRequest);
        return modelAndView;
    }

    @GetMapping()
    public ModelAndView getActivitiesHistory(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        List<ActivityResponse> activityHistory = activityService.getActivityHistory(authenticationDetails.getUserId());
        List<ActivityResponse> list = activityService.getLast30Activities(activityHistory);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity-history");
        modelAndView.addObject("user", user);
        modelAndView.addObject("list", list);
        return modelAndView;
    }
}
