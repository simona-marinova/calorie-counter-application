package app.food.model;

import app.meal.model.Meal;
import app.myRecipe.model.MyRecipe;
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
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double quantityInGrams;

    private double calories;

    @ManyToOne
    private User user;

    @ManyToOne
    private Meal meal;

    @ManyToOne
    private MyRecipe myRecipe;

}
