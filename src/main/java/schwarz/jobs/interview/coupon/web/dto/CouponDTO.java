package schwarz.jobs.interview.coupon.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(name = "CouponDTO", description = "Data transfer object for discount coupons")
public class CouponDTO {

    @NotNull(message = "Coupon discount value cannot be null")
    @Positive(message = "Discount must be a positive number")
    @Digits(integer = 8, fraction = 2, message = "Maximum 8 integer digits and 2 decimal places")
    @Schema(description = "Discount value with up to 2 decimal places", example = "10.50")
    private BigDecimal discount;

    @NotNull(message = "Coupon code cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9]{8}$", message = "Code must be exactly 8 alphanumeric characters")
    @Schema(description = "Unique 8-character alphanumeric coupon code", example = "DISC2024")
    private String code;

    @NotNull(message = "Minimum basket value cannot be null")
    @PositiveOrZero(message = "Minimum basket value must be zero or positive")
    @Digits(integer = 8, fraction = 2, message = "Maximum 8 integer digits and 2 decimal places")
    @Schema(description = "Minimum purchase amount required to apply the coupon", example = "50.00")
    private BigDecimal minBasketValue;
}
