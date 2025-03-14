package app.dailyStatistics.service;

import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.repository.DailyStatisticsRepository;
import app.exception.DomainException;
import app.user.model.Gender;
import app.user.model.User;
import app.user.model.WeightGoal;
import app.user.service.UserService;
import app.web.dto.CalculateDailyCalorieGoalRequest;
import app.web.dto.CaloriesBurnedRequest;
import app.web.dto.CurrentWeightRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DailyStatisticsService {


    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final UserService userService;


    @Autowired
    public DailyStatisticsService(DailyStatisticsRepository dailyStatisticsRepository, UserService userService) {
        this.dailyStatisticsRepository = dailyStatisticsRepository;
        this.userService = userService;
    }

    public void calculateCalorieGoal(CalculateDailyCalorieGoalRequest calculateDailyCalorieGoalRequest, User user) {

        double basalMetabolicRate = 0;

        WeightGoal weightGoal = calculateDailyCalorieGoalRequest.getWeightGoal();

        if (calculateDailyCalorieGoalRequest.getGender() == Gender.MALE) {
            basalMetabolicRate = (10 * calculateDailyCalorieGoalRequest.getWeight()) +
                    (6.25 * calculateDailyCalorieGoalRequest.getHeight()) -
                    (5 * calculateDailyCalorieGoalRequest.getAge()) + 5;
        } else {
            basalMetabolicRate = (10 * calculateDailyCalorieGoalRequest.getWeight()) +
                    (6.25 * calculateDailyCalorieGoalRequest.getHeight()) -
                    (5 * calculateDailyCalorieGoalRequest.getAge()) - 161;
        }

        double activityFactor = 0;

        switch (calculateDailyCalorieGoalRequest.getActivityLevel()) {
            case SEDENTARY:
                activityFactor = 1.2;
                break;
            case MODERATELY_ACTIVE:
                activityFactor = 1.55;
                break;
            case VERY_ACTIVE:
                activityFactor = 1.725;
                break;
        }

        double totalDailyEnergyExpenditure = basalMetabolicRate * activityFactor;

        double dailyCalorieGoal = 0;

        if (weightGoal == WeightGoal.WEIGHT_LOSS) {
            dailyCalorieGoal = Math.round(totalDailyEnergyExpenditure - 500);
            if (dailyCalorieGoal < 1200) {
                dailyCalorieGoal = 1200;
            }
        } else {
            dailyCalorieGoal = Math.round(totalDailyEnergyExpenditure);
        }


        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), user.getId());
        dailyStatistics.setDailyCalorieGoal(dailyCalorieGoal);
        //  calculateRemainingCalories(user.getId(), dailyStatistics);
        dailyStatistics.setDailyCaloriesBurned(basalMetabolicRate);
        dailyStatisticsRepository.save(dailyStatistics);
    }


    public void updateCaloriesBurned(UUID userId, CaloriesBurnedRequest caloriesBurnedRequest) {

        User user = userService.getById(userId);

        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);

        double caloriesBurned = caloriesBurnedRequest.getCaloriesBurned();
        dailyStatistics.setDailyCaloriesBurned(dailyStatistics.getDailyCaloriesBurned() + caloriesBurned);
        //  calculateRemainingCalories(userId, dailyStatistics);
        dailyStatisticsRepository.save(dailyStatistics);
    }


    public void calculateRemainingCalories(UUID userId, DailyStatistics dailyStatistics) {
        User user = userService.getById(userId);
        double caloriesBurned = 0;
        if (dailyStatistics.getDailyCaloriesBurned() != null) {
            caloriesBurned = dailyStatistics.getDailyCaloriesBurned();
        }
        dailyStatistics.setDailyCaloriesBurned(caloriesBurned);
        double dailyCaloriesConsumed = dailyStatistics.getDailyCaloriesConsumed();
        double dailyCalorieGoal = dailyStatistics.getDailyCalorieGoal();
        double dailyCaloriesRemaining = (dailyCalorieGoal + caloriesBurned) - dailyCaloriesConsumed;
        if (dailyCaloriesRemaining < 0) {
            dailyCaloriesRemaining = 0;
        }
        dailyStatistics.setDailyCaloriesRemaining(dailyCaloriesRemaining);

        dailyStatisticsRepository.save(dailyStatistics);
    }


    public DailyStatistics createDailyStatistics(UUID userId) {
        LocalDate today = LocalDate.now();
        User user = userService.getById(userId);

        Optional<DailyStatistics> optionalDailyStatistics = dailyStatisticsRepository.findByDateAndUserId(today, userId);

        if (optionalDailyStatistics.isEmpty()) {
            DailyStatistics dailyStatistics = new DailyStatistics();
            dailyStatistics.setDate(today);
            dailyStatistics.setUser(user);
            dailyStatistics.setDailyCaloriesBurned(0.00);
            dailyStatistics.setDailyCaloriesRemaining(0.00);
            dailyStatistics.setDailyCaloriesConsumed(0.00);
            LocalDate yesterday = today.minusDays(1);
            Optional<DailyStatistics> optionalDailyStatisticsYesterday = dailyStatisticsRepository.findByDateAndUserId(yesterday, userId);
            if (optionalDailyStatisticsYesterday.isPresent() && optionalDailyStatisticsYesterday.get().getDailyCalorieGoal() != 0.00) {
                dailyStatistics.setDailyCalorieGoal(optionalDailyStatisticsYesterday.get().getDailyCalorieGoal());
                dailyStatistics.setDailyCaloriesRemaining(optionalDailyStatisticsYesterday.get().getDailyCalorieGoal());
            }
            dailyStatisticsRepository.save(dailyStatistics);
        }


        return dailyStatisticsRepository.findByDateAndUserId(today, userId).get();
    }


    public void updateConsumedAndRemainingCalories(double calories, UUID userId) {
        User user = userService.getById(userId);
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        double caloriesToAdd = Math.round(dailyStatistics.getDailyCaloriesConsumed() + calories) * 100;
        dailyStatistics.setDailyCaloriesConsumed(caloriesToAdd / 100);
        double dailyCaloriesRemaining = dailyStatistics.getDailyCalorieGoal() - dailyStatistics.getDailyCaloriesConsumed();
        if (dailyCaloriesRemaining < 0) {
            dailyCaloriesRemaining = 0;
        }
        dailyStatistics.setDailyCaloriesRemaining(dailyCaloriesRemaining);

        dailyStatisticsRepository.save(dailyStatistics);
    }

    public void updateCurrentWeight(UUID userId, CurrentWeightRequest currentWeightRequest) {
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        dailyStatistics.setWeight(currentWeightRequest.getWeight());
        userService.updateUserWeight(userId, currentWeightRequest.getWeight());
        dailyStatisticsRepository.save(dailyStatistics);

    }

    public DailyStatistics getDailyStatisticsByDateAndUserId(LocalDate date, UUID userId) {

        return dailyStatisticsRepository.findByDateAndUserId(date, userId).orElseThrow(() -> new DomainException("Daily statistics on this date [%s] does not exist.".formatted(date)));

    }

    public DailyStatistics getById(UUID id) {
        return dailyStatisticsRepository.findById(id).orElseThrow(() -> new DomainException("Daily statistics with id [%s] does not exist.".formatted(id)));
    }


    public void lowerCaloriesConsumed(UUID userId, DailyStatistics dailyStatistics, double calories) {
        dailyStatistics.setDailyCaloriesConsumed(dailyStatistics.getDailyCaloriesConsumed() - calories);
        dailyStatisticsRepository.save(dailyStatistics);
    }

    public List<DailyStatistics> getDailyStatisticsHistory(UUID userId) {
        return dailyStatisticsRepository.findAllByUserIdOrderByDateDesc(userId).stream().limit(14).toList();
    }
}
