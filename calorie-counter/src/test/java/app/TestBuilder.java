package app;

import app.dailyStatistics.model.DailyStatistics;
import app.user.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TestBuilder {

    public static User aRandomUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("simona")
                .firstName("Simona")
                .lastName("Marinova")
                .email("simona@abv.bg")
                .password("123123")
                .height(165)
                .weight(55)
                .age(36)
                .gender(Gender.FEMALE)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .userRole(UserRole.ADMIN)
                .country(Country.BULGARIA)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .build();

        return user;
    }


    public static DailyStatistics aRandomDailyStatistics() {
        return DailyStatistics.builder()
                .id(UUID.randomUUID())
                .date(LocalDate.now())
                .consumedCalories(300)
                .burnedCaloriesDuringActivity(200.00)
                .burnedCaloriesAtRest(1200.00)
                .remainingCalories(250.00)
                .weight(55)
                .calorieGoal(1500)
                .user(aRandomUser())
                .build();
    }
}