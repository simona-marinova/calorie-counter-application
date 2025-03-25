package app;


import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.repository.DailyStatisticsRepository;
import app.dailyStatistics.service.DailyStatisticsService;
import app.user.model.Country;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class DailyStatisticsITest {

    @Autowired
    private UserService userService;
    @Autowired
    private DailyStatisticsService dailyStatisticsService;


    @Test
    public void createDailyStatistics_shouldCreateNewDailyStatisticsWhenNoneExist() {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("doroteya");
        registerRequest.setPassword("123123");
        registerRequest.setEmail("doroteya@abv.bg");
        registerRequest.setCountry(Country.BULGARIA);
        User user = userService.register(registerRequest);
        LocalDate today = LocalDate.now();
        DailyStatistics createdStatistics = dailyStatisticsService.createDailyStatistics(user.getId());
        assertNotNull(createdStatistics);
        assertEquals(today, createdStatistics.getDate());
        assertEquals(user.getId(), createdStatistics.getUser().getId());
        assertEquals(0.00, createdStatistics.getBurnedCaloriesDuringActivity());
        assertEquals(0.00, createdStatistics.getBurnedCaloriesAtRest());
        assertEquals(0.00, createdStatistics.getRemainingCalories());
        assertEquals(0.00, createdStatistics.getConsumedCalories());
    }
}
