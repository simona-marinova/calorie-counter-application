package app.web.mapper;

import app.food.model.Food;
import app.myRecipe.model.MyRecipe;
import app.user.model.ActivityLevel;
import app.user.model.Country;
import app.user.model.User;
import app.user.model.WeightGoal;
import app.web.dto.EditMyRecipeRequest;
import app.web.dto.FoodEditRequest;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DtoMapperUTest {

    @Test
    void givenHappyPath_whenMappingUserToUserEditRequest(){
        User user = User.builder()
                .firstName("Simona")
                .lastName("Marinova")
                .email("simona@abv.bg")
                .height(165)
                .weight(55)
                .age(36)
                .activityLevel(ActivityLevel.VERY_ACTIVE)
                .country(Country.BULGARIA)
                .weightGoal(WeightGoal.WEIGHT_LOSS)
                .build();
        UserEditRequest resultDto = DtoMapper.mapUserToUserEditRequest(user);
        assertEquals(user.getFirstName(), resultDto.getFirstName());
        assertEquals(user.getLastName(), resultDto.getLastName());
        assertEquals(user.getEmail(), resultDto.getEmail());
        assertEquals(user.getHeight(), resultDto.getHeight());
        assertEquals(user.getWeight(), resultDto.getWeight());
        assertEquals(user.getAge(), resultDto.getAge());
        assertEquals(user.getActivityLevel(), resultDto.getActivityLevel());
        assertEquals(user.getCountry(), resultDto.getCountry());
        assertEquals(user.getWeightGoal(), resultDto.getWeightGoal());
    }

    @Test
    void givenHappyPath_whenMappingFoodToFoodEditRequest(){
        Food food = Food.builder()
                .name("apple")
                .caloriesPerHundredGrams(52.0)
                .picture("apple.jpg")
                .build();
        FoodEditRequest foodEditRequest = DtoMapper.mapFoodToFoodEditRequest(food);
        assertEquals(food.getName(), foodEditRequest.getName());
        assertEquals(food.getCaloriesPerHundredGrams(), foodEditRequest.getCaloriesPerHundredGrams());
        assertEquals(food.getPicture(), foodEditRequest.getPicture());
    }

    @Test
    void givenHappyPath_whenMappingMyRecipeToEditMyRecipeRequest(){
            MyRecipe myRecipe = MyRecipe.builder()
                    .name("Spaghetti Bolognese")
                    .instructions("1. Cook pasta. 2. Make sauce. 3. Combine.")
                    .picture("spaghetti.jpg")
                    .build();
            EditMyRecipeRequest editMyRecipeRequest = DtoMapper.mapMyRecipeToEditMyRecipeRequest(myRecipe);
            assertEquals(myRecipe.getName(), editMyRecipeRequest.getName());
            assertEquals(myRecipe.getInstructions(), editMyRecipeRequest.getInstructions());
            assertEquals(myRecipe.getPicture(), editMyRecipeRequest.getPicture());
        }
    }

