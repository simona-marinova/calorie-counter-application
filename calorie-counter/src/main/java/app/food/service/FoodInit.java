package app.food.service;

import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.CreateFoodRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class FoodInit implements CommandLineRunner {

    private final FoodService foodService;
   private final UserService userService;

    public FoodInit(FoodService foodService, UserService userService) {
        this.foodService = foodService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (!foodService.getAllFoods().isEmpty()) {
            return;
        }
        User user = userService.getByUsername("simona");
        User userTwo = userService.getByUsername("nikola");


        CreateFoodRequest createFoodRequest = CreateFoodRequest.builder()
                .name("raw egg")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRzxed2TEhccp1M9VoFwwKIw5WJ7vHgjf-58A&s")
                .caloriesPerHundredGrams(155)
                .build();

        foodService.createNewFood(createFoodRequest, user.getId());
        foodService.createNewFood(createFoodRequest, userTwo.getId());

        CreateFoodRequest createFoodRequestTwo = CreateFoodRequest.builder()
                .name("cooking oil")
                .picture("https://latourangelle.com/cdn/shop/articles/55_1200x1200.jpg?v=1732254040")
                .caloriesPerHundredGrams(884)
                .build();

        foodService.createNewFood(createFoodRequestTwo, user.getId());
        foodService.createNewFood(createFoodRequestTwo, userTwo.getId());

        CreateFoodRequest createFoodRequestThree = CreateFoodRequest.builder()
                .name("tomato")
                .picture("https://www.vincenzosonline.com/userContent/images/Blog/Tomatoes/tomatoes-5.jpg")
                .caloriesPerHundredGrams(18)
                .build();

        foodService.createNewFood(createFoodRequestThree, user.getId());
        foodService.createNewFood(createFoodRequestThree, userTwo.getId());

        CreateFoodRequest createFoodRequestFour = CreateFoodRequest.builder()
                .name("cheese")
                .picture("https://www.arditairko.lt/uploads/images/catalog_src/white-cheese-types_src_1.jpg")
                .caloriesPerHundredGrams(264)
                .build();

        foodService.createNewFood(createFoodRequestFour, user.getId());
        foodService.createNewFood(createFoodRequestFour, userTwo.getId());


        CreateFoodRequest createFoodRequestFive = CreateFoodRequest.builder()
                .name("black pepper")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSiDoa69S90J-u2-X8o-UqA4Ko0VCPecMFsng&s")
                .caloriesPerHundredGrams(0)
                .build();

        foodService.createNewFood(createFoodRequestFive, user.getId());
        foodService.createNewFood(createFoodRequestFive, userTwo.getId());


        CreateFoodRequest createFoodRequestSix = CreateFoodRequest.builder()
                .name("salt")
                .picture("https://www.seasaltsuperstore.com/cdn/shop/articles/38.png?v=1731433853")
                .caloriesPerHundredGrams(0)
                .build();

        foodService.createNewFood(createFoodRequestSix, user.getId());
        foodService.createNewFood(createFoodRequestSix, userTwo.getId());


        CreateFoodRequest createFoodRequestSeven = CreateFoodRequest.builder()
                .name("white bread")
                .picture("https://www.backerhausveit.com/wp-content/uploads/2021/03/17783-1.jpg.webp")
                .caloriesPerHundredGrams(265)
                .build();

        foodService.createNewFood(createFoodRequestSeven, user.getId());
        foodService.createNewFood(createFoodRequestSeven, userTwo.getId());

        CreateFoodRequest createFoodRequestEight = CreateFoodRequest.builder()
                .name("avocado")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcREl9n2tNkrH2h85FLYOmLpdcztqp51aICwcw&s")
                .caloriesPerHundredGrams(160)
                .build();

        foodService.createNewFood(createFoodRequestEight, user.getId());
        foodService.createNewFood(createFoodRequestEight, userTwo.getId());

        CreateFoodRequest createFoodRequestNine = CreateFoodRequest.builder()
                .name("lemon")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSRqq1eXvQy-sJTC6B4TjblTFhtcrInYivmEg&s")
                .caloriesPerHundredGrams(29)
                .build();

        foodService.createNewFood(createFoodRequestNine, user.getId());
        foodService.createNewFood(createFoodRequestNine, userTwo.getId());


        CreateFoodRequest createFoodRequestTen = CreateFoodRequest.builder()
                .name("red pepper")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGXrRDkgQ6HOfuiu-RhNEjDd8o-0yCoZFj7A&s")
                .caloriesPerHundredGrams(0)
                .build();

        foodService.createNewFood(createFoodRequestTen, user.getId());
        foodService.createNewFood(createFoodRequestTen, userTwo.getId());

        CreateFoodRequest createFoodRequestEleven = CreateFoodRequest.builder()
                .name("milk")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-J3O2-Y56cFlGEo-lIHah9l_mPYc1iKcFBg&s")
                .caloriesPerHundredGrams(42)
                .build();

        foodService.createNewFood(createFoodRequestEleven, user.getId());
        foodService.createNewFood(createFoodRequestEleven, userTwo.getId());

        CreateFoodRequest createFoodRequestTwelve = CreateFoodRequest.builder()
                .name("butter")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTyqbVeUAVFpDxLYwalyhhgigYImA2EzQVPhw&s")
                .caloriesPerHundredGrams(717)
                .build();

        foodService.createNewFood(createFoodRequestTwelve, user.getId());
        foodService.createNewFood(createFoodRequestTwelve, userTwo.getId());


        CreateFoodRequest createFoodRequestThirteen = CreateFoodRequest.builder()
                .name("banana")
                .picture("https://media.gettyimages.com/id/980474902/vector/banana-flat-design-fruit-icon.jpg?s=612x612&w=gi&k=20&c=BycMdFYcTrWcH2UIBH8wRjKEXQa0btBq5D1l4RN7Auk=")
                .caloriesPerHundredGrams(89)
                .build();

        foodService.createNewFood(createFoodRequestThirteen, user.getId());
        foodService.createNewFood(createFoodRequestThirteen, userTwo.getId());


        CreateFoodRequest createFoodRequestFourteen = CreateFoodRequest.builder()
                .name("apple")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRl2Gq0i3YPdVSmQWKZ7M8TToTdlNAupeR8KQ&s")
                .caloriesPerHundredGrams(52)
                .build();

        foodService.createNewFood(createFoodRequestFourteen, user.getId());
        foodService.createNewFood(createFoodRequestFourteen, userTwo.getId());


        CreateFoodRequest createFoodRequestFifteen = CreateFoodRequest.builder()
                .name("honey")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7_DN0OrqpbYcyEj8H0ZuHgiHEcLIeNYG56g&s")
                .caloriesPerHundredGrams(304)
                .build();

        foodService.createNewFood(createFoodRequestFifteen, user.getId());
        foodService.createNewFood(createFoodRequestFifteen, userTwo.getId());


        CreateFoodRequest createFoodRequestSixteen = CreateFoodRequest.builder()
                .name("kiwi")
                .picture("https://img.freepik.com/free-photo/fresh-kiwi-fruit-isolated_144627-30034.jpg")
                .caloriesPerHundredGrams(61)
                .build();

        foodService.createNewFood(createFoodRequestSixteen, user.getId());
        foodService.createNewFood(createFoodRequestSixteen, userTwo.getId());


        CreateFoodRequest createFoodRequestSeventeen = CreateFoodRequest.builder()
                .name("peanut butter")
                .picture("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRN632SHWICiPg9XePEPN2kd_vNp6pyNQJYNg&s")
                .caloriesPerHundredGrams(588)
                .build();

        foodService.createNewFood(createFoodRequestSeventeen, user.getId());
        foodService.createNewFood(createFoodRequestSeventeen, userTwo.getId());



    }
}
