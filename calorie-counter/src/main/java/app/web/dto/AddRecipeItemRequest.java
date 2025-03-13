package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddRecipeItemRequest {

    @NotBlank(message = "Please select a recipe item")
    @NotNull(message = "Please select a recipe item")
    private String recipeItemName;

    @NotNull(message = "Recipe item quantity cannot be null")
    @Positive(message = "Recipe item quantity must be a positive number")
    private double recipeItemQuantity;


}
