package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AddFoodItemRequest {

    @NotBlank(message = "Please select a food item")
    @NotNull(message = "Please select a food item")
    private String foodItemName;

    @NotNull(message = "Food item quantity cannot be null")
    @Positive(message = "Food item quantity must be a positive number")
    private double foodItemQuantity;
}
