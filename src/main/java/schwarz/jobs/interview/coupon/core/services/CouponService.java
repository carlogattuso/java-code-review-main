package schwarz.jobs.interview.coupon.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.exception.CouponCodeAlreadyExistsException;
import schwarz.jobs.interview.coupon.core.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    private final CouponMapper couponMapper;

    public Mono<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code);
    }

    /*public Optional<Basket> apply(final Basket basket, final String code) {

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
    }*/

    public Mono<CouponDTO> createCoupon(final CouponDTO couponDTO) {
        String couponCode = couponDTO.getCode();

        return hasCoupon(couponCode)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new CouponCodeAlreadyExistsException(couponCode));
                    } else {
                        return saveCoupon(couponDTO, couponCode);
                    }
                })
                .map(couponMapper::toDto);
    }

    private Mono<Boolean> hasCoupon(String code) {
        return couponRepository.existsCouponByCode(code);
    }

    private Mono<Coupon> saveCoupon(CouponDTO couponDTO, String code) {
        Coupon newCoupon = Coupon.builder()
                .code(code)
                .discount(couponDTO.getDiscount())
                .minBasketValue(couponDTO.getMinBasketValue())
                .build();

        return couponRepository.save(newCoupon);
    }

    public Flux<CouponDTO> getCoupons(final CouponRequestDTO couponRequestDTO) {
        Flux<Coupon> coupons = couponRepository.findByCodeIn(couponRequestDTO.getCodes());
        return coupons.map(couponMapper::toDto);
    }
}
