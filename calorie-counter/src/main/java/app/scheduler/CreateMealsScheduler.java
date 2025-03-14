package app.scheduler;

import app.meal.service.MealService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class CreateMealsScheduler {

    private final UserService userService;
    private final MealService mealService;

    @Autowired
    public CreateMealsScheduler(UserService userService, MealService mealService) {
        this.userService = userService;
        this.mealService = mealService;
    }

    //тук трябва да има една звезда отпред за да работи да проверя дали е така
    @Scheduled(cron = "0 0 0 * * *")
   // @Scheduled(cron = "0 50 7 * * *")
    public void createMealsForTheDay() {
        List<User> allUsers = userService.getAllActiveUsers();
        for (User user : allUsers) {
            mealService.createMealsForTheDay(user.getId());
        }
    }
}
