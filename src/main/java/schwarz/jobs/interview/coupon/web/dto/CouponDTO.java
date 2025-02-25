package schwarz.jobs.interview.coupon.web.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@Builder
public class CouponDTO {

    @NotNull
    @Positive
    private BigDecimal discount;

    @NotNull
    private String code;

    @NotNull
    @PositiveOrZero
    private BigDecimal minBasketValue;

}
