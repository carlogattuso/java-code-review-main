package schwarz.jobs.interview.coupon.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.mapper.CouponMapper;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    CouponServiceTest(@Mock CouponRepository couponRepository, @Mock CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
        this.couponService = new CouponService(couponRepository, couponMapper);
    }

    @Test
    public void createCoupon() {
        CouponDTO dto = CouponDTO.builder()
                .code("12345")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        couponService.createCoupon(dto);

        verify(couponRepository, times(1)).save(any());
    }

    @Test
    public void test_apply_coupon_method() {

        final Basket firstBasket = Basket.builder()
                .value(BigDecimal.valueOf(100))
                .build();

        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));

        Optional<Basket> optionalBasket = couponService.apply(firstBasket, "1111");

        assertThat(optionalBasket).hasValueSatisfying(b -> {
            assertThat(b.getAppliedDiscount()).isEqualTo(BigDecimal.TEN);
            assertThat(b.isApplicationSuccessful()).isTrue();
        });

        final Basket secondBasket = Basket.builder()
                .value(BigDecimal.valueOf(0))
                .build();

        optionalBasket = couponService.apply(secondBasket, "1111");

        assertThat(optionalBasket).hasValueSatisfying(b -> {
            assertThat(b).isEqualTo(secondBasket);
            assertThat(b.isApplicationSuccessful()).isFalse();
        });

        final Basket thirdBasket = Basket.builder()
                .value(BigDecimal.valueOf(-1))
                .build();

        assertThatThrownBy(() -> {
            couponService.apply(thirdBasket, "1111");
        }).isInstanceOf(RuntimeException.class)
                .hasMessage("Can't apply negative discounts");
    }

    @Test
    void should_return_all_coupons() {
        CouponRequestDTO dto = CouponRequestDTO.builder()
                .codes(Arrays.asList("1111", "1234"))
                .build();

        List<Coupon> mockCoupons = Arrays.asList(
                Coupon.builder().code("1111").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build(),
                Coupon.builder().code("1234").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build()
        );

        when(couponRepository.findByCodeIn(dto.getCodes()))
                .thenReturn(Flux.fromIterable(mockCoupons));

        when(couponMapper.toDto(any()))
                .thenAnswer(invocation -> {
                    Coupon coupon = invocation.getArgument(0);
                    return CouponDTO.builder()
                            .code(coupon.getCode())
                            .discount(coupon.getDiscount())
                            .minBasketValue(coupon.getMinBasketValue())
                            .build();
                });

        Flux<CouponDTO> returnedCoupons = couponService.getCoupons(dto);

        StepVerifier.create(returnedCoupons)
                .expectNextMatches(coupon -> {
                    assertThat(coupon.getCode()).isEqualTo("1111");
                    assertThat(coupon.getDiscount()).isEqualTo(BigDecimal.TEN);
                    assertThat(coupon.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(50));
                    return true;
                })
                .expectNextMatches(coupon -> {
                    assertThat(coupon.getCode()).isEqualTo("1234");
                    assertThat(coupon.getDiscount()).isEqualTo(BigDecimal.TEN);
                    assertThat(coupon.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(50));
                    return true;
                }).verifyComplete();
    }

    @Test
    void should_return_only_existing_coupons() {
        CouponRequestDTO dto = CouponRequestDTO.builder()
                .codes(Arrays.asList("1111", "9999"))
                .build();

        List<Coupon> mockCoupons = List.of(
                Coupon.builder().code("1111").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build()
        );

        when(couponRepository.findByCodeIn(dto.getCodes()))
                .thenReturn(Flux.fromIterable(mockCoupons));

        when(couponMapper.toDto(any()))
                .thenAnswer(invocation -> {
                    Coupon coupon = invocation.getArgument(0);
                    return CouponDTO.builder()
                            .code(coupon.getCode())
                            .discount(coupon.getDiscount())
                            .minBasketValue(coupon.getMinBasketValue())
                            .build();
                });

        Flux<CouponDTO> returnedCoupons = couponService.getCoupons(dto);

        StepVerifier.create(returnedCoupons)
                .expectNextMatches(coupon -> {
                    assertThat(coupon.getCode()).isEqualTo("1111");
                    assertThat(coupon.getDiscount()).isEqualTo(BigDecimal.TEN);
                    assertThat(coupon.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(50));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void should_return_empty_flux_when_empty() {
        CouponRequestDTO dto = CouponRequestDTO.builder()
                .codes(Collections.emptyList())
                .build();

        when(couponRepository.findByCodeIn(dto.getCodes()))
                .thenReturn(Flux.empty());

        Flux<CouponDTO> returnedCoupons = couponService.getCoupons(dto);

        StepVerifier.create(returnedCoupons)
                .verifyComplete();
    }
}
