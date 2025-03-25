package app.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaloriesBurnedRequest {

    private String activityType;

    private int duration;

    private double caloriesBurned;
}
