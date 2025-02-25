package schwarz.jobs.interview.coupon.core.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.mapper.impl.CouponMapperImpl;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CouponMapperImplTest {

    private final CouponMapperImpl couponMapper;

    CouponMapperImplTest() {
        this.couponMapper = new CouponMapperImpl();
    }

    @Test
    void should_map_coupon_to_dto() {
        Coupon coupon = Coupon.builder()
                .code("1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        CouponDTO dto = couponMapper.toDto(coupon);

        assertThat(dto.getCode()).isEqualTo("1234");
        assertThat(dto.getDiscount()).isEqualTo(BigDecimal.TEN);
        assertThat(dto.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(50));
    }

}

