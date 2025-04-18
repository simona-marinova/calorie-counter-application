package app.web.dto;

import jakarta.validation.constraints.NotBlank;
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
public class EditMyRecipeRequest {

    @NotBlank(message = "Name of the recipe cannot be blank")
    @Size(min = 2, max = 100, message = "Name of the recipe must be between 2 and 100 characters")
    private String name;

    @Size(max = 1000, message = "Instructions cannot exceed 1000 characters")
    private String instructions;

    @URL(message = "Must contain a valid URL.")
    private String picture;

}
