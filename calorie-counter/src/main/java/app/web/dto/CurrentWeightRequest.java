package app.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentWeightRequest {

    @NotNull(message = "Weight is required")
   @Min(value = 40, message = "Minimum weight is 40 kg")
    @Max(value = 300, message = "Maximum weight is 300 kg")
    private double weight;
}
