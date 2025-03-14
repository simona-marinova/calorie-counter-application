package app.activity.service;


import app.activity.client.ActivityClient;
import app.activity.client.dto.ActivityResponse;
import app.activity.client.dto.ActivityRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ActivityService {

    private final ActivityClient activityClient;

    @Autowired
    public ActivityService(ActivityClient activityClient) {
        this.activityClient = activityClient;
    }

    public List<ActivityResponse> getActivityHistory(UUID userId) {
        ResponseEntity<List<ActivityResponse>> httpResponse = activityClient.getActivityHistory(userId);
        return httpResponse.getBody();
    }


    public double saveActivity(UUID userId, String activityType, int duration) {
        ActivityRequest activityRequest = ActivityRequest.builder()
                .userId(userId)
                .activityType(activityType)
                .duration(duration)
                .build();

        ResponseEntity<ActivityResponse> httpResponse = activityClient.createActivity(activityRequest);

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {

            log.error("[Feign call to burned calories tracker failed.] Can't save activity for user with id = [%s]".formatted(userId));
        }
        double calories = httpResponse.getBody().getCaloriesBurned();
        return calories;
    }

    public List<ActivityResponse> getLast50Activities(List<ActivityResponse> activityHistory) {

        return activityHistory
                .stream()
                .limit(50).toList();
    }

}
