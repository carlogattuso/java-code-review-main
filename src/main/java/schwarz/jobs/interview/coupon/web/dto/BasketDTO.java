package schwarz.jobs.interview.coupon.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(name = "BasketDTO", description = "")
public class BasketDTO {

    @NotNull(message = "Basket value cannot be null")
    @PositiveOrZero(message = "Basket value must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum 8 integer digits and 2 decimal places")
    @Schema(description = "Basket value with up to 2 decimal places", example = "100.25")
    private BigDecimal value;

    @NotNull(message = "Applied discount value cannot be null")
    @PositiveOrZero(message = "Applied discount must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum 8 integer digits and 2 decimal places")
    @Schema(description = "Applied discount with up to 2 decimal places", example = "10.50")
    private BigDecimal appliedDiscount;

    @AssertFalse(message = "A coupon has already been applied to this basket. No further applications are allowed.")
    private boolean applicationSuccessful;

}
