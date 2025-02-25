package schwarz.jobs.interview.coupon.core.models;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
public class Basket {

    @NotNull
    private BigDecimal value;

    private BigDecimal appliedDiscount;

    private boolean applicationSuccessful;

    public void applyDiscount(final BigDecimal discount) {
        this.applicationSuccessful = false;
        this.appliedDiscount = discount;
    }

}
