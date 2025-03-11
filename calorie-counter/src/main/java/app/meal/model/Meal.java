package app.meal.model;

import app.food.model.FoodItem;
import app.myRecipe.model.MyRecipeItem;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MealType mealType;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private double calories;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "meal", fetch = FetchType.EAGER)
    private List<FoodItem> foodItems;
    
    @OneToMany(mappedBy="meal", fetch = FetchType.EAGER)
    private List<MyRecipeItem> myRecipeItems;


}
