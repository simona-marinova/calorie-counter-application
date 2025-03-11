package app.web.dto;

import app.user.model.ActivityLevel;
import app.user.model.Gender;
import app.user.model.WeightGoal;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CalculateDailyCalorieGoalRequest {

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "You must be at least 18 years old")
    @Max(value = 100, message = "Maximum age is 100 years")
    private int age;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Height is required")
    @Min(value = 120, message = "Minimum height is 120 cm")
    @Max(value = 270, message = "Maximum height is 270 cm")
    private double height;

    @NotNull(message = "Weight is required")
    @Min(value = 40, message = "Minimum weight is 40 kg")
    @Max(value = 300, message = "Maximum weight is 300 kg")
    private double weight;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Weight goal is required")
    private WeightGoal weightGoal;
}
