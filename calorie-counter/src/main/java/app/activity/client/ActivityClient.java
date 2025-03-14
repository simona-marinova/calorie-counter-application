package app.activity.client;

import app.activity.client.dto.ActivityResponse;
import app.activity.client.dto.ActivityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;


@FeignClient(name="burned-calories-tracker", url="http://localhost:8081/api/v1/activities")
public interface ActivityClient {

    @GetMapping
    ResponseEntity<List<ActivityResponse>> getActivityHistory(@RequestParam(name = "userId") UUID userId);

    @PostMapping
    ResponseEntity<ActivityResponse> createActivity(@RequestBody ActivityRequest activityRequest);

}
