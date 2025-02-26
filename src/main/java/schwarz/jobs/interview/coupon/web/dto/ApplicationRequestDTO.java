package schwarz.jobs.interview.coupon.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApplicationRequestDTO", description = "")
public class ApplicationRequestDTO {

    @NotNull(message = "Coupon code cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{8}$", message = "Code must be exactly 8 alphanumeric characters")
    @Schema(description = "Unique 8-character alphanumeric coupon code", example = "DISC2024")
    private String code;

    @NotNull(message = "Basket cannot be null")
    @Valid
    private BasketDTO basket;

}
