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
    private double dailyCaloriesConsumed;

    @Column
    private Double dailyCaloriesBurned;

    @Column
    private Double dailyCaloriesRemaining;

    @Column
    private double weight;

    @Column
    private double dailyCalorieGoal;

    @ManyToOne
    private User user;
}
