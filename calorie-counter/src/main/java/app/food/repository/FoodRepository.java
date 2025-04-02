package app.food.repository;

import app.food.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID> {

   Optional<Food> findByUserIdAndName(UUID userId, String name);

   List<Food> findAllByUserIdOrderByName(UUID userId);

   List<Food> findByUserIdAndNameContainingIgnoreCase(UUID userId, String keyword);
}
