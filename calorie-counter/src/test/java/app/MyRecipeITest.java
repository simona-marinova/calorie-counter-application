package app;

import app.food.service.FoodService;
import app.myRecipe.model.MyRecipe;
import app.myRecipe.repository.MyRecipeItemRepository;
import app.myRecipe.repository.MyRecipeRepository;
import app.myRecipe.service.MyRecipeService;
import app.user.model.Country;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateMyRecipeRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class MyRecipeITest {

    @Autowired
    private MyRecipeRepository myRecipeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private MyRecipeService myRecipeService;

    @Test
    public void createNewRecipe_shouldCreateRecipeSuccessfully() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("doroteya");
        registerRequest.setPassword("123123");
        registerRequest.setEmail("doroteya@abv.bg");
        registerRequest.setCountry(Country.BULGARIA);
        User user = userService.register(registerRequest);

        CreateMyRecipeRequest createMyRecipeRequest = CreateMyRecipeRequest.builder()
                .name("Pasta Carbonara")
                .instructions("Boil pasta, add eggs, bacon, and cheese.")
                .picture("pasta.jpg")
                .recipePublic(true)
                .build();

        MyRecipe myRecipe = myRecipeService.createNewRecipe(createMyRecipeRequest, user.getId());

        assertNotNull(myRecipe);
        assertEquals(createMyRecipeRequest.getName(), myRecipe.getName());
        assertEquals(createMyRecipeRequest.getInstructions(), myRecipe.getInstructions());
        assertEquals(createMyRecipeRequest.getPicture(), myRecipe.getPicture());
        assertTrue(myRecipe.isPublicRecipe());
        assertTrue(myRecipe.getFoodItems().isEmpty());

        MyRecipe foundRecipe = myRecipeRepository.findById(myRecipe.getId()).orElse(null);
        assertNotNull(foundRecipe);
    }

}



