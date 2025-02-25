package schwarz.jobs.interview.coupon.core.mapper.impl;

import org.springframework.stereotype.Component;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

@Component
public class CouponMapperImpl implements CouponMapper {

    @Override
    public CouponDTO toDto(Coupon coupon) {
        return CouponDTO.builder()
                .code(coupon.getCode())
                .discount(coupon.getDiscount())
                .minBasketValue(coupon.getMinBasketValue())
                .build();
    }
    
}
