package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFoodRequest {

    @NotBlank(message = "Name of the food cannot be blank")
    @Size(min = 2, max = 100, message = "Name of the food must be between 2 and 100 characters")
    private String name;

    @URL(message = "Picture must be a valid URL")
    private String picture;

    @PositiveOrZero(message = "Calories per 100 grams must be a positive number or zero")
    private double caloriesPerHundredGrams;

}
