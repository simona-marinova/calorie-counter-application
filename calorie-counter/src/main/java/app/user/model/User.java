package app.user.model;

import app.dailyStatistics.model.DailyStatistics;
import app.food.model.Food;
import app.food.model.FoodItem;
import app.meal.model.Meal;
import app.myRecipe.model.MyRecipe;
import app.myRecipe.model.MyRecipeItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users_users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private double height;

    @Column
    private double weight;

    @Column
    private int age;

    @Enumerated(EnumType.STRING)
    @Column
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private Country country;

    @Column
    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Column
    @Enumerated(EnumType.STRING)
    private WeightGoal weightGoal;

    @OneToMany(mappedBy = "user")
    private List<DailyStatistics> dailyStatistics = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Meal> meals = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private List<MyRecipe> myRecipes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Food> foods = new ArrayList<>();;

    @OneToMany(mappedBy = "user")
    private List<FoodItem> foodItems = new ArrayList<>();;

    @OneToMany(mappedBy = "user")
    private List<MyRecipeItem> myRecipeItems = new ArrayList<>();;



}
