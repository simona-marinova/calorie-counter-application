package app.web;

import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.service.DailyStatisticsService;
import app.security.AuthenticationDetails;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CalculateDailyCalorieGoalRequest;
import app.web.dto.CurrentWeightRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/daily-statistics")
public class DailyStatisticsController {

    private final UserService userService;
    private final DailyStatisticsService dailyStatisticsService;

    @Autowired
    public DailyStatisticsController(UserService userService, DailyStatisticsService dailyStatisticsService) {
        this.userService = userService;
        this.dailyStatisticsService = dailyStatisticsService;
    }



    @GetMapping("/{id}/daily-calorie-goal")
    public ModelAndView calculateDailyCalorieGoal(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("daily-calorie-goal");
        DailyStatistics dailyStatistics = dailyStatisticsService.getById(id);
        modelAndView.addObject("calculateDailyCalorieGoalRequest", new CalculateDailyCalorieGoalRequest());
        modelAndView.addObject("dailyStatistics", dailyStatistics);
        return modelAndView;
    }

    @PostMapping("/{id}/daily-calorie-goal")
    public ModelAndView processCalculateCalorieRequest(@PathVariable UUID id, @Valid CalculateDailyCalorieGoalRequest calculateDailyCalorieGoalRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("daily-calorie-goal");
            DailyStatistics dailyStatistics = dailyStatisticsService.getById(id);
            modelAndView.addObject("calculateDailyCalorieGoalRequest", calculateDailyCalorieGoalRequest);
            modelAndView.addObject("dailyStatistics", dailyStatistics);
            return modelAndView;
        }
        DailyStatistics dailyStatistics = dailyStatisticsService.getById(id);
        User user = userService.getById(dailyStatistics.getUser().getId());
        dailyStatisticsService.calculateCalorieGoal(calculateDailyCalorieGoalRequest, user);

        return new ModelAndView("redirect:daily-calorie-goal");
    }


    @GetMapping("/weight")
    public ModelAndView addCurrentWeight(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        User user = userService.getById(authenticationDetails.getUserId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-current-weight");
        modelAndView.addObject("user", user);
        modelAndView.addObject("currentWeightRequest", new CurrentWeightRequest());
        return modelAndView;
    }

    @PostMapping("/weight")
    public String processCurrentWeightRequest(@AuthenticationPrincipal AuthenticationDetails authenticationDetails, @Valid CurrentWeightRequest currentWeightRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "add-current-weight";
        }
        User user = userService.getById(authenticationDetails.getUserId());

        dailyStatisticsService.updateCurrentWeight(authenticationDetails.getUserId(), currentWeightRequest);

        return "redirect:/home";
    }

    @GetMapping("/history")
    public ModelAndView showDailyStatisticsHistory(@AuthenticationPrincipal AuthenticationDetails authenticationDetails) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("daily-statistics-history");
        List<DailyStatistics> dailyStatisticsHistory = dailyStatisticsService.getDailyStatisticsHistory(authenticationDetails.getUserId());
        modelAndView.addObject("dailyStatisticsHistory", dailyStatisticsHistory);
        return modelAndView;
    }

}
