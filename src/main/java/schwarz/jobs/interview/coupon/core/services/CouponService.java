package schwarz.jobs.interview.coupon.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.exception.CouponCodeAlreadyExistsException;
import schwarz.jobs.interview.coupon.core.exception.CouponNotFoundException;
import schwarz.jobs.interview.coupon.core.exception.InsufficientBasketValueException;
import schwarz.jobs.interview.coupon.core.exception.InvalidDiscountException;
import schwarz.jobs.interview.coupon.core.mapper.BasketMapper;
import schwarz.jobs.interview.coupon.core.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.core.models.Basket;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    private final CouponMapper couponMapper;

    private final BasketMapper basketMapper;

    public Mono<Coupon> getCoupon(final String code) {
        return couponRepository.findByCode(code);
    }

    public Mono<BasketDTO> applyCoupon(final ApplicationRequestDTO requestDTO) {
        String couponCode = requestDTO.getCode();
        Basket basket = basketMapper.toBasket(requestDTO.getBasket());

        return getCoupon(couponCode)
                .switchIfEmpty(Mono.error(new CouponNotFoundException(couponCode)))
                .flatMap(coupon -> {

                    if (coupon.getMinBasketValue().compareTo(basket.getValue()) > 0) {
                        return Mono.error(new InsufficientBasketValueException(coupon.getMinBasketValue().toString()));
                    }

                    final BigDecimal totalDiscount = basket.getValue().subtract(coupon.getDiscount().add(basket.getAppliedDiscount()));
                    if (BigDecimal.ZERO.compareTo(totalDiscount) > 0) {
                        return Mono.error(new InvalidDiscountException(totalDiscount.toString()));
                    }

                    basket.applyDiscount(coupon.getDiscount());

                    return Mono.just(basketMapper.toDto(basket));
                });
    }

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
