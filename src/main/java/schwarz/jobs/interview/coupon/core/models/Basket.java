package schwarz.jobs.interview.coupon.core.models;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Basket {

    private BigDecimal value;

    private BigDecimal appliedDiscount;

    private boolean applicationSuccessful;

    public void applyDiscount(final BigDecimal discount) {
        this.applicationSuccessful = true;
        this.appliedDiscount = this.appliedDiscount.add(discount);
    }

}
