package app.activity.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ActivityResponse {

    @NotBlank
    private String activityType;

    @NotNull
    private int duration;

    @NotNull
    private double burnedCalories;

    @NotBlank
    private String createdOn;


}
