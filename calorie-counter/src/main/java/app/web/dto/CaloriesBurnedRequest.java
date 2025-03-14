package app.web.dto;


import lombok.Data;

@Data
public class CaloriesBurnedRequest {

    private String activityType;

    private int duration;

    private double caloriesBurned;
}
