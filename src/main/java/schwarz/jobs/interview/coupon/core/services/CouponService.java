package schwarz.jobs.interview.coupon.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    private final CouponMapper couponMapper;

    public Optional<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code);
    }

    public Optional<Basket> apply(final Basket basket, final String code) {

        return getCoupon(code).map(coupon -> {

            if (basket.getValue().doubleValue() >= 0) {

                if (basket.getValue().doubleValue() > 0) {

                    basket.applyDiscount(coupon.getDiscount());

                } else if (basket.getValue().doubleValue() == 0) {
                    return basket;
                }

            } else {
                System.out.println("DEBUG: TRIED TO APPLY NEGATIVE DISCOUNT!");
                throw new RuntimeException("Can't apply negative discounts");
            }

            return basket;
        });
    }

    public Coupon createCoupon(final CouponDTO couponDTO) {

        Coupon coupon = null;

        try {
            coupon = Coupon.builder().code(couponDTO.getCode().toLowerCase()).discount(couponDTO.getDiscount())
                    .minBasketValue(couponDTO.getMinBasketValue()).build();

        } catch (final NullPointerException e) {

            // Don't coupon when code is null
        }

        return coupon;
    }

    public Flux<CouponDTO> getCoupons(final CouponRequestDTO couponRequestDTO) {
        Flux<Coupon> coupons = couponRepository.findByCodeIn(couponRequestDTO.getCodes());
        return coupons.map(couponMapper::toDto);
    }
}
