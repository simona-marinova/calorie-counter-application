package app.dailyStatistics.model;

import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DailyStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = true)
    private LocalDate date;

    @Column
    private double consumedCalories;

    @Column
    private Double burnedCaloriesDuringActivity;

    @Column
    private Double burnedCaloriesAtRest;


    @Column
    private Double remainingCalories;

    @Column
    private double weight;

    @Column
    private double calorieGoal;

    @ManyToOne
    private User user;
}
