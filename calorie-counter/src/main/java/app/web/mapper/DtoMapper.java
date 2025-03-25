package app.web.mapper;


import app.food.model.Food;
import app.myRecipe.model.MyRecipe;
import app.user.model.User;
import app.web.dto.EditMyRecipeRequest;
import app.web.dto.FoodEditRequest;
import app.web.dto.UserEditRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static UserEditRequest mapUserToUserEditRequest(User user) {
        return UserEditRequest.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .height(user.getHeight())
                .weight(user.getWeight())
                .age(user.getAge())
                .activityLevel(user.getActivityLevel())
                .country(user.getCountry())
                .weightGoal(user.getWeightGoal())
                .build();
    }


    public static FoodEditRequest mapFoodToFoodEditRequest(Food food) {
        return FoodEditRequest.builder()
                .name(food.getName())
                .caloriesPerHundredGrams(food.getCaloriesPerHundredGrams())
                .picture(food.getPicture())
                .build();
    }

    public static EditMyRecipeRequest mapMyRecipeToEditMyRecipeRequest(MyRecipe myRecipe) {
        return EditMyRecipeRequest.builder()
                .name(myRecipe.getName())
                .instructions(myRecipe.getInstructions())
                .picture(myRecipe.getPicture())
                .build();
    }
}