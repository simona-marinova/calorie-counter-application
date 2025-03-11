package app.web.dto;

import app.user.model.ActivityLevel;
import app.user.model.Country;
import app.user.model.WeightGoal;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {

    @Size(min = 2, max = 20, message = "First name length must be between 2 and 20 characters.")
    private String firstName;

    @Size(min = 2, max = 20, message = "Last name length must be between 2 and 20 characters.")
    private String lastName;

    @Email(message = "Please enter a valid email address")
    private String email;

    @NotNull(message = "Height is required")
    @Min(value = 120, message = "Minimum height is 120 cm")
    @Max(value = 270, message = "Maximum height is 270 cm")
    private double height;


    @NotNull(message = "Weight is required")
    @Min(value = 40, message = "Minimum weight is 40 kg")
    @Max(value = 300, message = "Maximum weight is 300 kg")
    private double weight;


    @NotNull(message = "Age is required")
    @Min(value = 18, message = "You must be at least 18 years old")
    @Max(value = 100, message = "Maximum age is 100 years")
    private int age;

    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;

    @NotNull(message = "Country is required")
    private Country country;

    @NotNull(message = "Weight goal is required")
    private WeightGoal weightGoal;


}

