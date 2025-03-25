package app.dailyStatistics;

import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.repository.DailyStatisticsRepository;
import app.dailyStatistics.service.DailyStatisticsService;
import app.exception.DomainException;
import app.user.model.ActivityLevel;
import app.user.model.Gender;
import app.user.model.User;
import app.user.model.WeightGoal;
import app.user.service.UserService;
import app.web.dto.CalculateCalorieRequest;
import app.web.dto.CaloriesBurnedRequest;
import app.web.dto.CurrentWeightRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyStatisticsUTest {

    @Mock
    private DailyStatisticsRepository dailyStatisticsRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private DailyStatisticsService dailyStatisticsService;


    @Test
    public void getDailyStatisticsHistory_shouldReturnLimitedList() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statisticsOne = DailyStatistics.builder()
                .id(UUID.randomUUID())
                .user(user)
                .date(LocalDate.now()).build();
        DailyStatistics statisticsTwo = DailyStatistics.builder()
                .id(UUID.randomUUID())
                .user(user)
                .date(LocalDate.now().minusDays(1))
                .build();
        DailyStatistics statisticsThree = DailyStatistics.builder()
                .id(UUID.randomUUID())
                .user(user)
                .date(LocalDate.now().minusDays(2))
                .build();
        List<DailyStatistics> dailyStatisticsList = new ArrayList<>();
        dailyStatisticsList.add(statisticsOne);
        dailyStatisticsList.add(statisticsTwo);
        dailyStatisticsList.add(statisticsThree);
        when(dailyStatisticsRepository.findAllByUserIdOrderByDateDesc(userId)).thenReturn(dailyStatisticsList);
        List<DailyStatistics> result = dailyStatisticsService.getDailyStatisticsHistory(userId);
        assertEquals(3, result.size());
        assertEquals(dailyStatisticsList, result);
        verify(dailyStatisticsRepository, times(1)).findAllByUserIdOrderByDateDesc(userId);
    }


    @Test
    public void getDailyStatisticsHistory_thenLimitTo30() {
        UUID userId = UUID.randomUUID();
        List<DailyStatistics> dailyStatisticsList = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            User user = User.builder().id(UUID.randomUUID()).build();
            dailyStatisticsList.add(DailyStatistics.builder().id(UUID.randomUUID()).user(user).date(LocalDate.now().minusDays(i)).build());
        }
        when(dailyStatisticsRepository.findAllByUserIdOrderByDateDesc(userId)).thenReturn(dailyStatisticsList);
        List<DailyStatistics> result = dailyStatisticsService.getDailyStatisticsHistory(userId);
        assertEquals(30, result.size());
        verify(dailyStatisticsRepository, times(1)).findAllByUserIdOrderByDateDesc(userId);
    }

    @Test
    public void getDailyStatisticsHistory_whenNoDailyStatistics_thenReturnEmptyList() {
        UUID userId = UUID.randomUUID();
        List<DailyStatistics> dailyStatisticsList = new ArrayList<>();
        when(dailyStatisticsRepository.findAllByUserIdOrderByDateDesc(userId)).thenReturn(dailyStatisticsList);
        List<DailyStatistics> result = dailyStatisticsService.getDailyStatisticsHistory(userId);
        assertEquals(0, result.size());
        verify(dailyStatisticsRepository, times(1)).findAllByUserIdOrderByDateDesc(userId);
    }

    @Test
    public void getById_whenStatisticsExists_thenReturnStatistics_() {
        UUID statisticsId = UUID.randomUUID();
        DailyStatistics statistics = DailyStatistics.builder()
                .id(statisticsId)
                .date(LocalDate.now()).build();
        when(dailyStatisticsRepository.findById(statisticsId)).thenReturn(Optional.of(statistics));
        DailyStatistics result = dailyStatisticsService.getById(statisticsId);
        assertEquals(statistics, result);
        verify(dailyStatisticsRepository, times(1)).findById(statisticsId);
    }

    @Test
    public void getById_whenStatisticsDoesNotExist_thenException() {
        UUID statisticsId = UUID.randomUUID();
        when(dailyStatisticsRepository.findById(statisticsId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> dailyStatisticsService.getById(statisticsId));
        verify(dailyStatisticsRepository, times(1)).findById(statisticsId);
    }

    @Test
    public void getDailyStatisticsByDateAndUserId_thenReturnStatistics_whenStatisticsExists() {
        LocalDate date = LocalDate.now();
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .date(date)
                .user(user)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(date, userId)).thenReturn(Optional.of(statistics));
        DailyStatistics result = dailyStatisticsService.getDailyStatisticsByDateAndUserId(date, userId);
        assertEquals(statistics, result);
        verify(dailyStatisticsRepository, times(1)).findByDateAndUserId(date, userId);
    }

    @Test
    public void getDailyStatisticsByDateAndUserId_whenStatisticsDoesNotExist_thenException() {
        LocalDate date = LocalDate.now();
        UUID userId = UUID.randomUUID();
        when(dailyStatisticsRepository.findByDateAndUserId(date, userId)).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> dailyStatisticsService.getDailyStatisticsByDateAndUserId(date, userId));
        verify(dailyStatisticsRepository, times(1)).findByDateAndUserId(date, userId);
    }

    @Test
    public void updateCurrentWeight_shouldUpdateWeightAndSaveStatistics() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        CurrentWeightRequest request = CurrentWeightRequest
                .builder()
                .weight(75.5)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .weight(70.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateCurrentWeight(userId, request);
        assertEquals(75.5, statistics.getWeight());
        verify(userService, times(1)).updateWeight(userId, 75.5);
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }


    @Test
    public void updateConsumedAndRemainingCalories_shouldUpdateCaloriesAndSaveStatistics() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .consumedCalories(500.0)
                .calorieGoal(2000.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateConsumedAndRemainingCalories(300.0, userId);
        assertEquals(800.0, statistics.getConsumedCalories());
        assertEquals(1200.0, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateConsumedAndRemainingCalories_whenNegativeRemainingCalories() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .consumedCalories(1800.0)
                .calorieGoal(2000.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateConsumedAndRemainingCalories(500.0, userId);
        assertEquals(2300.0, statistics.getConsumedCalories());
        assertEquals(0.0, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateConsumedAndRemainingCalories_whenRounding() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .consumedCalories(100.33)
                .calorieGoal(2000.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateConsumedAndRemainingCalories(200.66, userId);
        assertEquals(300.99, statistics.getConsumedCalories());
        assertEquals(1699.01, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateConsumedAndRemainingCalories__whenCaloriesRemainingBelowZero_thenCaloriesRemainingZero() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .consumedCalories(1800.0)
                .calorieGoal(2000.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateConsumedAndRemainingCalories(500.0, userId);
        assertEquals(2300.0, statistics.getConsumedCalories());
        assertEquals(0.0, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void lowerCaloriesConsumed_whenZeroCaloriesReduction() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .consumedCalories(800.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.lowerCaloriesConsumed(userId, 0.0);
        assertEquals(800.0, statistics.getConsumedCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateCaloriesBurned_thenUpdateBurnedCaloriesAndSaveStatistics() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        CaloriesBurnedRequest request = CaloriesBurnedRequest.builder().caloriesBurned(250.0).build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .burnedCaloriesDuringActivity(100.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateCaloriesBurned(userId, request);
        assertEquals(350.0, statistics.getBurnedCaloriesDuringActivity());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateCaloriesBurned_whenZeroCaloriesBurned() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        CaloriesBurnedRequest request = CaloriesBurnedRequest.builder().caloriesBurned(0.0).build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .burnedCaloriesDuringActivity(100.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateCaloriesBurned(userId, request);
        assertEquals(100.0, statistics.getBurnedCaloriesDuringActivity());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }


    @Test
    public void updateCaloriesBurned_whenDecimalCaloriesBurned() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        CaloriesBurnedRequest request = CaloriesBurnedRequest.builder().caloriesBurned(125.75).build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .burnedCaloriesDuringActivity(100.25)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateCaloriesBurned(userId, request);
        assertEquals(226.0, statistics.getBurnedCaloriesDuringActivity());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }


    @Test
    public void updateRemainingCalories_thenUpdateRemainingCaloriesAndSaveStatistics() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        double calories = 150.50;
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .remainingCalories(200.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateRemainingCalories(userId, calories);
        assertEquals(350.50, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateRemainingCalories_whenZeroCalories() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        double calories = 0.0;
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .remainingCalories(200.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateRemainingCalories(userId, calories);
        assertEquals(200.0, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }

    @Test
    public void updateRemainingCalories_thenRoundCorrectly() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        double calories = 100.3333;
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .remainingCalories(200.0)
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.updateRemainingCalories(userId, calories);
        assertEquals(300.33, statistics.getRemainingCalories());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }


    @Test
    public void calculateCalorieGoalAndCaloriesAtRest_whenMaleWeightLossSedentary() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        CalculateCalorieRequest request = CalculateCalorieRequest.builder()
                .gender(Gender.MALE)
                .weight(80.0)
                .height(180.0)
                .age(30)
                .activityLevel(ActivityLevel.SEDENTARY)
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .calorieDeficit(500)
                .build();

        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .build();

        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));

        dailyStatisticsService.calculateCalorieGoalAndCaloriesAtRest(request, userId);

        assertEquals(1636.0, statistics.getCalorieGoal());
        assertEquals(1780.0, statistics.getBurnedCaloriesAtRest());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }


    @Test
    public void calculateCalorieGoalAndCaloriesAtRest_whenFemaleWeightMaintenanceVeryActive() {
            UUID userId = UUID.randomUUID();
            User user = User.builder()
                    .id(userId)
                    .build();
            CalculateCalorieRequest request = CalculateCalorieRequest.builder()
                    .gender(Gender.FEMALE)
                    .weight(65.0)
                    .height(165.0)
                    .age(25)
                    .activityLevel(ActivityLevel.VERY_ACTIVE)
                    .weightGoal(WeightGoal.WEIGHT_MAINTENANCE)
                    .calorieDeficit(300)
                    .build();
            DailyStatistics statistics = DailyStatistics.builder()
                    .user(user)
                    .date(LocalDate.now())
                    .build();
            when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
            dailyStatisticsService.calculateCalorieGoalAndCaloriesAtRest(request, userId);
            assertEquals(2406.81, statistics.getCalorieGoal());
            assertEquals(1395.25, statistics.getBurnedCaloriesAtRest());
            verify(dailyStatisticsRepository, times(1)).save(statistics);
        }


    @Test
    public void calculateCalorieGoalAndCaloriesAtRest_whenCalorieGoalUnder1200_thenSetTo1200() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        CalculateCalorieRequest request = CalculateCalorieRequest.builder()
                .gender(Gender.FEMALE)
                .weight(40.0)
                .height(150.0)
                .age(60)
                .activityLevel(ActivityLevel.MODERATELY_ACTIVE)
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .calorieDeficit(500)
                .build();
        DailyStatistics statistics = DailyStatistics.builder()
                .user(user)
                .date(LocalDate.now())
                .build();
        when(dailyStatisticsRepository.findByDateAndUserId(LocalDate.now(), userId)).thenReturn(Optional.of(statistics));
        dailyStatisticsService.calculateCalorieGoalAndCaloriesAtRest(request, userId);
        assertEquals(1200.0, statistics.getCalorieGoal());
        verify(dailyStatisticsRepository, times(1)).save(statistics);
    }


}









