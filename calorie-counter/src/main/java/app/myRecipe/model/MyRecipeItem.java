package app.myRecipe.model;

import app.meal.model.Meal;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MyRecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double quantityInGrams;

    private double calories;

    @ManyToOne
    private Meal meal;

    @ManyToOne
    private User user;
}
