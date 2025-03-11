package app.myRecipe.repository;

import app.myRecipe.model.MyRecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MyRecipeItemRepository extends JpaRepository<MyRecipeItem, UUID> {

    Optional<MyRecipeItem> findByUserIdAndName (UUID userId, String name);
}
