package app.dailyStatistics.service;

import app.dailyStatistics.model.DailyStatistics;
import app.dailyStatistics.repository.DailyStatisticsRepository;
import app.exception.DomainException;
import app.user.model.Gender;
import app.user.model.User;
import app.user.model.WeightGoal;
import app.user.service.UserService;
import app.web.dto.CalculateCalorieRequest;
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

    public void calculateCalorieGoalAndCaloriesAtRest(CalculateCalorieRequest calculateCalorieRequest, User user) {

        double basalMetabolicRate = 0;

        WeightGoal weightGoal = calculateCalorieRequest.getWeightGoal();

        if (calculateCalorieRequest.getGender() == Gender.MALE) {
            basalMetabolicRate = (10 * calculateCalorieRequest.getWeight()) +
                    (6.25 * calculateCalorieRequest.getHeight()) -
                    (5 * calculateCalorieRequest.getAge()) + 5;
        } else {
            basalMetabolicRate = (10 * calculateCalorieRequest.getWeight()) +
                    (6.25 * calculateCalorieRequest.getHeight()) -
                    (5 * calculateCalorieRequest.getAge()) - 161;
        }

        double activityFactor = 0;

        switch (calculateCalorieRequest.getActivityLevel()) {
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
            dailyCalorieGoal = Math.round(totalDailyEnergyExpenditure - calculateCalorieRequest.getCalorieDeficit());
            if (dailyCalorieGoal < 1200) {
                dailyCalorieGoal = 1200;
            }
        } else {
            dailyCalorieGoal = Math.round(totalDailyEnergyExpenditure);
        }


        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), user.getId());
        dailyStatistics.setCalorieGoal(dailyCalorieGoal);
        dailyStatistics.setBurnedCaloriesAtRest(basalMetabolicRate);
        dailyStatisticsRepository.save(dailyStatistics);
    }


    public void updateCaloriesBurned(UUID userId, CaloriesBurnedRequest caloriesBurnedRequest) {

        User user = userService.getById(userId);

        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);

        double caloriesBurned = caloriesBurnedRequest.getCaloriesBurned();
        dailyStatistics.setBurnedCaloriesDuringActivity(dailyStatistics.getBurnedCaloriesDuringActivity() + caloriesBurned);
        dailyStatisticsRepository.save(dailyStatistics);
    }


    public void calculateRemainingCalories(UUID userId, DailyStatistics dailyStatistics) {
        User user = userService.getById(userId);
        double caloriesBurned = 0;
        if (dailyStatistics.getBurnedCaloriesDuringActivity()!= null) {
            caloriesBurned = dailyStatistics.getBurnedCaloriesDuringActivity();
        }
        dailyStatistics.setBurnedCaloriesDuringActivity(caloriesBurned);
        double dailyCaloriesConsumed = dailyStatistics.getConsumedCalories();
        double dailyCalorieGoal = dailyStatistics.getCalorieGoal();
        double dailyCaloriesRemaining = (dailyCalorieGoal + caloriesBurned) - dailyCaloriesConsumed;
        if (dailyCaloriesRemaining < 0) {
            dailyCaloriesRemaining = 0;
        }
        dailyStatistics.setRemainingCalories(dailyCaloriesRemaining);

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
            dailyStatistics.setBurnedCaloriesDuringActivity(0.00);
            dailyStatistics.setBurnedCaloriesAtRest(0.00);
            dailyStatistics.setRemainingCalories(0.00);
            dailyStatistics.setConsumedCalories(0.00);
            LocalDate yesterday = today.minusDays(1);
            Optional<DailyStatistics> optionalDailyStatisticsYesterday = dailyStatisticsRepository.findByDateAndUserId(yesterday, userId);
            if (optionalDailyStatisticsYesterday.isPresent() && optionalDailyStatisticsYesterday.get().getCalorieGoal() != 0.00) {
                dailyStatistics.setCalorieGoal(optionalDailyStatisticsYesterday.get().getCalorieGoal());
            }
            if(optionalDailyStatisticsYesterday.isPresent() && optionalDailyStatisticsYesterday.get().getBurnedCaloriesAtRest()!=0.00){
                dailyStatistics.setBurnedCaloriesAtRest(optionalDailyStatistics.get().getBurnedCaloriesAtRest());
            }
            dailyStatisticsRepository.save(dailyStatistics);
        }


        return dailyStatisticsRepository.findByDateAndUserId(today, userId).get();
    }


    public void updateConsumedAndRemainingCalories(double calories, UUID userId) {
        User user = userService.getById(userId);
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        double caloriesToAdd = Math.round(dailyStatistics.getConsumedCalories() + calories) * 100;
        dailyStatistics.setConsumedCalories(caloriesToAdd / 100);
        double dailyCaloriesRemaining = dailyStatistics.getCalorieGoal() - dailyStatistics.getConsumedCalories();
        if (dailyCaloriesRemaining < 0) {
            dailyCaloriesRemaining = 0;
        }
        dailyStatistics.setRemainingCalories(dailyCaloriesRemaining);

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
        dailyStatistics.setConsumedCalories(dailyStatistics.getConsumedCalories() - calories);
        dailyStatisticsRepository.save(dailyStatistics);
    }

    public List<DailyStatistics> getDailyStatisticsHistory(UUID userId) {
        return dailyStatisticsRepository.findAllByUserIdOrderByDateDesc(userId).stream().limit(30).toList();
    }
}
