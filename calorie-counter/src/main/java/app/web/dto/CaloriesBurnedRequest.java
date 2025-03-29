package app.web.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaloriesBurnedRequest {

    @NotNull(message = "Activity type is required")
    private String activityType;

    @Positive(message = "Duration must be a positive number.")
    private int duration;

    private double caloriesBurned;
}
