package app.myRecipe.model;

import app.food.model.FoodItem;
import app.meal.model.Meal;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MyRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String instructions;


    @Column
    private double caloriesPerHundredGrams;

    @Column
    private double allCalories;

    @ManyToOne
    private User user;

    @ManyToOne
    private Meal meal;

    @OneToMany(mappedBy = "myRecipe", fetch = FetchType.EAGER)
    private List<FoodItem> foodItems;

}
