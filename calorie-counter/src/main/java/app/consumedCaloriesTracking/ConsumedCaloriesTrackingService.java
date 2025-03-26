package app.consumedCaloriesTracking;

import app.web.dto.DailyCalorieGoalExceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConsumedCaloriesTrackingService {

    @EventListener
    public void trackConsumedCalories(DailyCalorieGoalExceededEvent event) {
        log.info(event.getMessage());
    }
}

