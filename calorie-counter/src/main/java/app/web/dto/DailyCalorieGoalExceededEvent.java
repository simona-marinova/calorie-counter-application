package app.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import java.util.UUID;

@Data
@Builder
public class DailyCalorieGoalExceededEvent {

    private UUID userId;

    private String message;

    private double exceededCalories;

    private LocalDate date;

}
