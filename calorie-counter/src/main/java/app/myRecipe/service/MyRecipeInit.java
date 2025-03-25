package app.myRecipe.service;

import app.myRecipe.model.MyRecipe;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.AddFoodItemRequest;
import app.web.dto.CreateMyRecipeRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class MyRecipeInit implements CommandLineRunner {

    private final MyRecipeService myRecipeService;
    private final UserService userService;


    public MyRecipeInit(MyRecipeService myRecipeService, UserService userService) {
        this.myRecipeService = myRecipeService;
        this.userService = userService;
    }


    @Override
    public void run(String... args) throws Exception {


        if (!myRecipeService.getListOfAllRecipes().isEmpty()) {
            return;
        }

        User user = userService.getByUsername("simona");
        User userTwo = userService.getByUsername("nikola");

        CreateMyRecipeRequest createMyRecipeRequest = CreateMyRecipeRequest.builder()
                .name("Omelette with Tomatoes and Cheese")
                .instructions("1.Whisk the eggs in a bowl, add salt and pepper.\n" +
                        "2.Heat the oil or butter in a pan.\n" +
                        "3.Pour the eggs into the pan and spread them evenly.\n" +
                        "4.Add the chopped tomatoes and cheese on top.\n" +
                        "5.Cook until the eggs are set and the cheese is melted.\n" +
                        "6.Fold the omelette in half and serve.")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQOUxiYopvU-gMhnTkAb5EYMAQYA20LdEdrhw&s")
                .recipePublic(true)
                .build();

        MyRecipe myRecipeUser = myRecipeService.createNewRecipe(createMyRecipeRequest, user.getId());
        MyRecipe myRecipeUserTwo = myRecipeService.createNewRecipe(createMyRecipeRequest, userTwo.getId());

        AddFoodItemRequest addFoodItemRequest = AddFoodItemRequest.builder()
                .foodItemName("raw egg")
                .foodItemQuantity(100)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequest, user.getId(), myRecipeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequest, user.getId(), myRecipeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestTwo = AddFoodItemRequest.builder()
                .foodItemName("tomato")
                .foodItemQuantity(110)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestTwo, user.getId(), myRecipeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestTwo, user.getId(), myRecipeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestThree = AddFoodItemRequest.builder()
                .foodItemName("cheese")
                .foodItemQuantity(50)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestThree, user.getId(), myRecipeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestThree, user.getId(), myRecipeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestFour = AddFoodItemRequest.builder()
                .foodItemName("salt")
                .foodItemQuantity(5)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFour, user.getId(), myRecipeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFour, user.getId(), myRecipeUserTwo.getId());


        AddFoodItemRequest addFoodItemRequestFive = AddFoodItemRequest.builder()
                .foodItemName("black pepper")
                .foodItemQuantity(5)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFive, user.getId(), myRecipeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFive, user.getId(), myRecipeUserTwo.getId());


        AddFoodItemRequest addFoodItemRequestSix = AddFoodItemRequest.builder()
                .foodItemName("cooking oil")
                .foodItemQuantity(5)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSix, user.getId(), myRecipeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSix, user.getId(), myRecipeUserTwo.getId());

        CreateMyRecipeRequest createMyRecipeRequestTwo = CreateMyRecipeRequest.builder()
                .name("Avocado Bruschetta")
                .instructions("1.Slice the bread and lightly toast it in a toaster or pan.\n" +
                        "2.Mash the avocado with a fork and add the lemon juice, salt, and pepper.\n" +
                        "3.Spread the avocado mixture onto the toasted bread slices.\n" +
                        "4.Serve immediately.")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwbEI_Xq7b_dcvCA9mDnN1WO0t5PwqpoW1lw&s")
                .recipePublic(false)
                .build();

        MyRecipe myRecipeTwoUser = myRecipeService.createNewRecipe(createMyRecipeRequestTwo, user.getId());
        MyRecipe myRecipeTwoUserTwo = myRecipeService.createNewRecipe(createMyRecipeRequestTwo, userTwo.getId());


        AddFoodItemRequest addFoodItemRequestSeven = AddFoodItemRequest.builder()
                .foodItemName("white bread")
                .foodItemQuantity(70)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSeven, user.getId(), myRecipeTwoUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSeven, user.getId(), myRecipeTwoUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestEight = AddFoodItemRequest.builder()
                .foodItemName("avocado")
                .foodItemQuantity(200)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestEight, user.getId(), myRecipeTwoUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestEight, user.getId(), myRecipeTwoUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestNine = AddFoodItemRequest.builder()
                .foodItemName("lemon")
                .foodItemQuantity(30)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestNine, user.getId(), myRecipeTwoUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestNine, user.getId(), myRecipeTwoUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestTen = AddFoodItemRequest.builder()
                .foodItemName("red pepper")
                .foodItemQuantity(5)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestTen, user.getId(), myRecipeTwoUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestTen, user.getId(), myRecipeTwoUserTwo.getId());


        CreateMyRecipeRequest createMyRecipeRequestThree = CreateMyRecipeRequest.builder()
                .name("Simple Scrambled Eggs with Toast")
                .instructions("1.Prepare the eggs:\n" +
                        "Crack the eggs into a bowl.\n" +
                        "Add the milk, salt, and pepper.\n" +
                        "Whisk the eggs until they are well combined.\n" +
                        "2.Cook the eggs:\n" +
                        "Heat the butter or oil in a non-stick pan over medium heat.\n" +
                        "Pour the egg mixture into the pan.\n" +
                        "As the eggs begin to set, use a spatula to gently push the cooked portions towards the center, allowing the uncooked egg to flow underneath.\n" +
                        "Continue cooking until the eggs are softly set but still slightly moist.\n" +
                        "3.Toast the bread:\n" +
                        "While the eggs are cooking, toast the bread slices in a toaster or pan until golden brown.\n" +
                        "4.Serve:\n" +
                        "Serve the scrambled eggs immediately on top of the toast.")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtt_UdWGfX2PgwXZZeZ4nZgXfurhMqdnDCBg&s")
                .recipePublic(true)
                .build();

        MyRecipe myRecipeThreeUser = myRecipeService.createNewRecipe(createMyRecipeRequestThree, user.getId());
        MyRecipe myRecipeThreeUserTwo = myRecipeService.createNewRecipe(createMyRecipeRequestThree, userTwo.getId());


        AddFoodItemRequest addFoodItemRequestEleven = AddFoodItemRequest.builder()
                .foodItemName("raw egg")
                .foodItemQuantity(100)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestEleven, user.getId(), myRecipeThreeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestEleven, user.getId(), myRecipeThreeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestTwelve = AddFoodItemRequest.builder()
                .foodItemName("milk")
                .foodItemQuantity(20)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestTwelve, user.getId(), myRecipeThreeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestTwelve, user.getId(), myRecipeThreeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestThirteen = AddFoodItemRequest.builder()
                .foodItemName("butter")
                .foodItemQuantity(10)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestThirteen, user.getId(), myRecipeThreeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestThirteen, user.getId(), myRecipeThreeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestFourteen = AddFoodItemRequest.builder()
                .foodItemName("salt")
                .foodItemQuantity(5)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFourteen, user.getId(), myRecipeThreeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFourteen, user.getId(), myRecipeThreeUserTwo.getId());

        AddFoodItemRequest addFoodItemRequestFifteen = AddFoodItemRequest.builder()
                .foodItemName("white bread")
                .foodItemQuantity(70)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFifteen, user.getId(), myRecipeThreeUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestFifteen, user.getId(), myRecipeThreeUserTwo.getId());


        CreateMyRecipeRequest createMyRecipeRequestFour = CreateMyRecipeRequest.builder()
                .name("Simple Banana, Peanut Butter, and Honey Snack")
                .instructions("1.Slice the banana.\n" +
                        "2.Spread peanut butter on the banana slices.\n" +
                        "3.Drizzle honey over the peanut butter.")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRN632SHWICiPg9XePEPN2kd_vNp6pyNQJYNg&s")
                .recipePublic(false)
                .build();

        MyRecipe myRecipeFourUser = myRecipeService.createNewRecipe(createMyRecipeRequestFour, user.getId());
        MyRecipe myRecipeFourUserTwo = myRecipeService.createNewRecipe(createMyRecipeRequestFour, userTwo.getId());


        AddFoodItemRequest addFoodItemRequestSixteen = AddFoodItemRequest.builder()
                .foodItemName("banana")
                .foodItemQuantity(120)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSixteen, user.getId(), myRecipeFourUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSixteen, user.getId(), myRecipeFourUserTwo.getId());


        AddFoodItemRequest addFoodItemRequestSeventeen = AddFoodItemRequest.builder()
                .foodItemName("peanut butter")
                .foodItemQuantity(32)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSeventeen, user.getId(), myRecipeFourUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestSeventeen, user.getId(), myRecipeFourUserTwo.getId());


        AddFoodItemRequest addFoodItemRequestEighteen = AddFoodItemRequest.builder()
                .foodItemName("honey")
                .foodItemQuantity(21)
                .build();

        myRecipeService.addFoodItemToRecipe(addFoodItemRequestEighteen, user.getId(), myRecipeFourUser.getId());
        myRecipeService.addFoodItemToRecipe(addFoodItemRequestEighteen, user.getId(), myRecipeFourUserTwo.getId());


    }
}