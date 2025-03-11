package app.web.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CaloriesBurnedRequest {

    @PositiveOrZero(message = "Daily calories burned must be a positive number or zero")
    private double dailyCaloriesBurned;
}
