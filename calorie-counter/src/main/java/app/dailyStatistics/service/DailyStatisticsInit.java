package app.dailyStatistics.service;
import app.dailyStatistics.model.DailyStatistics;
import app.user.model.ActivityLevel;
import app.user.model.Gender;
import app.user.model.User;
import app.user.model.WeightGoal;
import app.user.service.UserService;
import app.web.dto.CalculateCalorieRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
@Order(4)
public class DailyStatisticsInit implements CommandLineRunner {

private final DailyStatisticsService dailyStatisticsService;
private final UserService userService;

    public DailyStatisticsInit(DailyStatisticsService dailyStatisticsService, UserService userService) {
        this.dailyStatisticsService = dailyStatisticsService;
        this.userService = userService;
    }


    @Override
    public void run(String... args) throws Exception {

        User user = userService.getByUsername("simona");
        User userTwo = userService.getByUsername("nikola");

        dailyStatisticsService.createDailyStatistics(user.getId());
        dailyStatisticsService.createDailyStatistics(userTwo.getId());

        DailyStatistics dailyStatisticsUser = dailyStatisticsService.getDailyStatisticsByDateAndUserId(LocalDate.now(), user.getId());

        if(dailyStatisticsUser.getCalorieGoal()!=0.00) {
            return;
        }

        CalculateCalorieRequest requestUser = CalculateCalorieRequest.builder()
                .age(36)
                .gender(Gender.FEMALE)
                .height(165)
                .weight(54)
                .calorieDeficit(300)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .build();

        CalculateCalorieRequest requestUserTwo = CalculateCalorieRequest.builder()
                .age(25)
                .gender(Gender.MALE)
                .height(175)
                .weight(90)
                .calorieDeficit(400)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .weightGoal(WeightGoal.WEIGHT_MAINTENANCE)
                .build();


        dailyStatisticsService.calculateCalorieGoalAndCaloriesAtRest(requestUser, user.getId());
        dailyStatisticsService.calculateCalorieGoalAndCaloriesAtRest(requestUserTwo, userTwo.getId());


    }
}
