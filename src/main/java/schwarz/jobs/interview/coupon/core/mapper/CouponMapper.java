package schwarz.jobs.interview.coupon.core.mapper;

import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

public interface CouponMapper {

    CouponDTO toDto(Coupon coupon);

}