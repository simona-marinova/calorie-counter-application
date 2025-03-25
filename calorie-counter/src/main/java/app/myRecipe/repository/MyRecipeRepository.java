package app.myRecipe.repository;

import app.myRecipe.model.MyRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MyRecipeRepository extends JpaRepository<MyRecipe, UUID> {

    Optional<MyRecipe> findByUserIdAndName( UUID userId, String name );

    List<MyRecipe> findAllByPublicRecipe(boolean publicRecipe);

    List<MyRecipe> findAllByUserId(UUID userId);

   Optional<MyRecipe> findByName(String name);
}
