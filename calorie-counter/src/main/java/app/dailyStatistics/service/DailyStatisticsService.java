package app.dailyStatistics.service;

import app.consumedCaloriesTracking.ConsumedCaloriesTrackingService;
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
import app.web.dto.DailyCalorieGoalExceededEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DailyStatisticsService {


    private final DailyStatisticsRepository dailyStatisticsRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final ConsumedCaloriesTrackingService consumedCaloriesTrackingService;


    @Autowired
    public DailyStatisticsService(DailyStatisticsRepository dailyStatisticsRepository, UserService userService, ApplicationEventPublisher eventPublisher, ConsumedCaloriesTrackingService consumedCaloriesTrackingService) {
        this.dailyStatisticsRepository = dailyStatisticsRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
        this.consumedCaloriesTrackingService = consumedCaloriesTrackingService;
    }

    public void calculateCalorieGoalAndCaloriesAtRest(CalculateCalorieRequest calculateCalorieRequest, UUID userId) {

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
            dailyCalorieGoal = totalDailyEnergyExpenditure - calculateCalorieRequest.getCalorieDeficit();
            if (dailyCalorieGoal < 1200) {
                dailyCalorieGoal = 1200;
            }
        } else {
            dailyCalorieGoal = totalDailyEnergyExpenditure;
        }


        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(),userId);
        dailyStatistics.setCalorieGoal(Math.round(dailyCalorieGoal * 100.0) / 100.0);
        dailyStatistics.setBurnedCaloriesAtRest(Math.round(basalMetabolicRate * 100.0) / 100.0);
        dailyStatisticsRepository.save(dailyStatistics);
    }


   public void updateCaloriesBurned(UUID userId, CaloriesBurnedRequest caloriesBurnedRequest) {
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        double caloriesBurned = caloriesBurnedRequest.getCaloriesBurned();
       double updatedCaloriesBurned = dailyStatistics.getBurnedCaloriesDuringActivity() + caloriesBurned;
        dailyStatistics.setBurnedCaloriesDuringActivity(Math.round(updatedCaloriesBurned * 100.0) / 100.0);
        dailyStatisticsRepository.save(dailyStatistics);}


   public void updateRemainingCalories(UUID userId,double calories) {
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
       double updatedRemainingCalories = dailyStatistics.getRemainingCalories()+calories;
        dailyStatistics.setRemainingCalories(Math.round(updatedRemainingCalories * 100.0) / 100.0);
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
                dailyStatistics.setRemainingCalories(optionalDailyStatisticsYesterday.get().getCalorieGoal());
            }
            if(optionalDailyStatisticsYesterday.isPresent() && optionalDailyStatisticsYesterday.get().getBurnedCaloriesAtRest()!=0.00){
                dailyStatistics.setBurnedCaloriesAtRest(optionalDailyStatisticsYesterday.get().getBurnedCaloriesAtRest());

            }
           dailyStatisticsRepository.save(dailyStatistics);
        }

     return dailyStatisticsRepository.findByDateAndUserId(today, userId).get();

    }


    public void updateConsumedAndRemainingCalories(double calories, UUID userId) {

        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        double consumedCalories = dailyStatistics.getConsumedCalories() + calories;
        dailyStatistics.setConsumedCalories(Math.round(consumedCalories * 100.0) / 100.0);

        if(consumedCalories>dailyStatistics.getCalorieGoal()){
            DailyCalorieGoalExceededEvent event = DailyCalorieGoalExceededEvent.builder()
                    .userId(userId)
                    .exceededCalories((Math.round(consumedCalories-dailyStatistics.getCalorieGoal()) * 100.0) / 100.0)
                    .message("User with id [%s] with daily calorie goal [%.2f] exceeded daily calorie goal with [%.2f] calories.".formatted(userId, dailyStatistics.getCalorieGoal(), (Math.round(consumedCalories-dailyStatistics.getCalorieGoal()) * 100.0) / 100.0))
                    .date(dailyStatistics.getDate())
                    .build();
            eventPublisher.publishEvent(event);
        }

        double dailyCaloriesRemaining = dailyStatistics.getCalorieGoal() - dailyStatistics.getConsumedCalories();
        if (dailyCaloriesRemaining < 0) {
            dailyCaloriesRemaining = 0;
        }
        dailyStatistics.setRemainingCalories(Math.round(dailyCaloriesRemaining * 100.0) / 100.0);

        dailyStatisticsRepository.save(dailyStatistics);
    }

    public void updateCurrentWeight(UUID userId, CurrentWeightRequest currentWeightRequest) {
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        dailyStatistics.setWeight(currentWeightRequest.getWeight());
        userService.updateWeight(userId, currentWeightRequest.getWeight());
        dailyStatisticsRepository.save(dailyStatistics);

    }

    public DailyStatistics getDailyStatisticsByDateAndUserId(LocalDate date, UUID userId) {

        return dailyStatisticsRepository.findByDateAndUserId(date, userId).orElseThrow(() -> new DomainException("Daily statistics on this date [%s] does not exist.".formatted(date)));

    }

    public DailyStatistics getById(UUID id) {
        return dailyStatisticsRepository.findById(id).orElseThrow(() -> new DomainException("Daily statistics with id [%s] does not exist.".formatted(id)));
    }


    public void lowerCaloriesConsumed(UUID userId, double calories) {
        DailyStatistics dailyStatistics = getDailyStatisticsByDateAndUserId(LocalDate.now(), userId);
        dailyStatistics.setConsumedCalories(dailyStatistics.getConsumedCalories() - calories);
        dailyStatisticsRepository.save(dailyStatistics);
    }


    public List<DailyStatistics> getDailyStatisticsHistory(UUID userId) {
        return dailyStatisticsRepository.findAllByUserIdOrderByDateDesc(userId).stream().limit(30).toList();
    }
}
