package app.food.repository;

import app.food.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FoodItemRepository extends JpaRepository<FoodItem, UUID> {

  List<FoodItem> findAllByMealId(UUID mealId);

  List<FoodItem> findAllByMyRecipeId(UUID recipeId);

  Optional<FoodItem> findByMyRecipeIdAndName(UUID recipeId, String foodItemName);
}
