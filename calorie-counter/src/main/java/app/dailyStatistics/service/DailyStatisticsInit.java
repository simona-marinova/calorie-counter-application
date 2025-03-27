package app.dailyStatistics.service;

import app.dailyStatistics.model.DailyStatistics;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;


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

            if(!dailyStatisticsService.getAll().isEmpty()){
                return;
            }

        User user = userService.getByUsername("simona");
        User userTwo = userService.getByUsername("nikola");

        dailyStatisticsService.createDailyStatistics(user.getId());
        dailyStatisticsService.createDailyStatistics(userTwo.getId());
    }
}
