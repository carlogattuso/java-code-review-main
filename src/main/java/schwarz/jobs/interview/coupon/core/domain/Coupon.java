package schwarz.jobs.interview.coupon.core.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@Table("COUPONS")
public class Coupon {

    @Id
    private Long id;

    @Column("CODE")
    private String code;

    @Column("DISCOUNT")
    private BigDecimal discount;

    @Column("MIN_BASKET_VALUE")
    private BigDecimal minBasketValue;

}
