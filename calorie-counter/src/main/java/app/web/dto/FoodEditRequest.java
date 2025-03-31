package app.web.dto;

import jakarta.validation.constraints.Positive;
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
public class FoodEditRequest {

    @Size(min = 2, max = 20, message = "Food name length must be between 2 and 30 characters.")
    private String name;

    @PositiveOrZero(message = "Calories must be a positive number or zero.")
    private double caloriesPerHundredGrams;

    @URL(message = "Must contain a valid URL.")
    private String picture;


}
