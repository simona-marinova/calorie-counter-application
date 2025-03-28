package app.activity.service;

import app.dailyStatistics.service.DailyStatisticsService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CaloriesBurnedRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Order(6)
public class ActivityInit implements CommandLineRunner {

    private final ActivityService activityService;
    private final UserService userService;
    private final DailyStatisticsService dailyStatisticsService;

    public ActivityInit(ActivityService activityService, UserService userService, DailyStatisticsService dailyStatisticsService) {
        this.activityService = activityService;
        this.userService = userService;
        this.dailyStatisticsService = dailyStatisticsService;
    }

    @Override
    public void run(String... args) throws Exception {

        User user = userService.getByUsername("simona");
        User userTwo = userService.getByUsername("nikola");

        try {
            if (!activityService.getActivityHistory(user.getId()).isEmpty()) {
                return;
            }
        }
        catch (Exception e) {
            log.error("[Feign call to burned calories tracker failed.] Can't get activity history for user with id = [%s]".formatted(user.getId()));
            return;
        }

        try {
           double calories = activityService.saveActivity(user.getId(), "WALKING", 30);
            CaloriesBurnedRequest caloriesBurnedRequest = CaloriesBurnedRequest.builder()
                    .activityType("WALKING")
                    .caloriesBurned(calories)
                    .duration(30)
                    .build();
          dailyStatisticsService.updateCaloriesBurned(user.getId(), caloriesBurnedRequest);
        } catch (Exception e) {
            log.error("Error adding activity of type WALKING for user %s with id [%s] because burned calories tracker svc is not working".formatted(user.getUsername(), user.getId()), e.getMessage());
        }

        try {
            double calories = activityService.saveActivity(user.getId(), "CARDIO", 15);
            CaloriesBurnedRequest caloriesBurnedRequest = CaloriesBurnedRequest.builder()
                    .activityType("CARDIO")
                    .caloriesBurned(calories)
                    .duration(15)
                    .build();
            dailyStatisticsService.updateCaloriesBurned(user.getId(), caloriesBurnedRequest);
        } catch (Exception e) {
            log.error("Error adding activity of type CARDIO for user %s with id [%s] because burned calories tracker svc is not working".formatted(user.getUsername(), user.getId()), e.getMessage());
        }

        try {
           double calories = activityService.saveActivity(user.getId(), "WEIGHT_TRAINING", 45);
            CaloriesBurnedRequest caloriesBurnedRequest = CaloriesBurnedRequest.builder()
                    .activityType("WEIGHT_TRAINING")
                    .caloriesBurned(calories)
                    .duration(45)
                    .build();
            dailyStatisticsService.updateCaloriesBurned(user.getId(), caloriesBurnedRequest);
        } catch (Exception e) {
            log.error("Error adding activity of type WEIGHT_TRAINING for user %s with id [%s] because burned calories tracker svc is not working".formatted(user.getUsername(), user.getId()), e.getMessage());
        }

        try {
            double calories = activityService.saveActivity(userTwo.getId(), "WALKING", 30);
            CaloriesBurnedRequest caloriesBurnedRequest = CaloriesBurnedRequest.builder()
                    .activityType("WALKING")
                    .caloriesBurned(calories)
                    .duration(30)
                    .build();
            dailyStatisticsService.updateCaloriesBurned(userTwo.getId(), caloriesBurnedRequest);
        } catch (Exception e) {
            log.error("Error adding activity of type WALKING for user %s with id [%s] because burned calories tracker svc is not working".formatted(userTwo.getUsername(), userTwo.getId()), e.getMessage());
        }

        try {
           double calories = activityService.saveActivity(userTwo.getId(), "CARDIO", 15);
     CaloriesBurnedRequest caloriesBurnedRequest = CaloriesBurnedRequest.builder()
                    .activityType("CARDIO")
                    .caloriesBurned(calories)
                    .duration(15)
                    .build();
            dailyStatisticsService.updateCaloriesBurned(userTwo.getId(), caloriesBurnedRequest);
        } catch (Exception e) {
            log.error("Error adding activity of type CARDIO for user %s with id [%s] because burned calories tracker svc is not working".formatted(userTwo.getUsername(), userTwo.getId()), e.getMessage());
        }

        try {
          double calories =  activityService.saveActivity(userTwo.getId(), "WEIGHT_TRAINING", 45);
            CaloriesBurnedRequest caloriesBurnedRequest = CaloriesBurnedRequest.builder()
                    .activityType("WEIGHT_TRAINING")
                    .caloriesBurned(calories)
                    .duration(45)
                    .build();
            dailyStatisticsService.updateCaloriesBurned(userTwo.getId(), caloriesBurnedRequest);
        } catch (Exception e) {
            log.error("Error adding activity of type WEIGHT_TRAINING for user %s with id [%s] because burned calories tracker svc is not working".formatted(userTwo.getUsername(), userTwo.getId()), e.getMessage());
        }

    }
}
