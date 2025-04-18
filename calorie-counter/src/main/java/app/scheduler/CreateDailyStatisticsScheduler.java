package app.scheduler;

import app.dailyStatistics.service.DailyStatisticsService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateDailyStatisticsScheduler {

    private final UserService userService;
    private final DailyStatisticsService dailyStatisticsService;

    public CreateDailyStatisticsScheduler(UserService userService, DailyStatisticsService dailyStatisticsService) {
        this.userService = userService;
        this.dailyStatisticsService = dailyStatisticsService;
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void createDailyStatisticsForTheDay() {
        List<User> allUsers = userService.getAllActiveUsers();
        for (User user : allUsers) {
            dailyStatisticsService.createDailyStatistics(user.getId());
        }
    }
}
