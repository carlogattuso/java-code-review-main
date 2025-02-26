package schwarz.jobs.interview.coupon.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    private final CouponService couponService;
    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;
    private final BasketMapper basketMapper;

    CouponServiceTest(
            @Mock CouponRepository couponRepository,
            @Mock CouponMapper couponMapper,
            @Mock BasketMapper basketMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
        this.basketMapper = basketMapper;
        this.couponService = new CouponService(couponRepository, couponMapper, basketMapper);
    }

    @Test
    void filter_should_return_all_coupons() {
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
    void filter_should_return_only_existing_coupons() {
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
    void filter_should_return_empty_flux_when_empty() {
        CouponRequestDTO dto = CouponRequestDTO.builder()
                .codes(Collections.emptyList())
                .build();

        when(couponRepository.findByCodeIn(dto.getCodes()))
                .thenReturn(Flux.empty());

        Flux<CouponDTO> returnedCoupons = couponService.getCoupons(dto);

        StepVerifier.create(returnedCoupons)
                .verifyComplete();
    }

    @Test
    void create_should_throw_exception_if_coupon_exists() {
        CouponDTO couponDTO = CouponDTO.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        when(couponRepository.existsCouponByCode(couponDTO.getCode()))
                .thenReturn(Mono.just(true));

        Mono<CouponDTO> result = couponService.createCoupon(couponDTO);

        StepVerifier.create(result)
                .expectErrorMatches(CouponCodeAlreadyExistsException.class::isInstance)
                .verify();
    }

    @Test
    void create_should_be_successful_if_code_does_not_exist() {

        CouponDTO couponDTO = CouponDTO.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        Coupon savedCoupon = Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        when(couponRepository.existsCouponByCode(couponDTO.getCode()))
                .thenReturn(Mono.just(false));

        when(couponRepository.save(any(Coupon.class)))
                .thenReturn(Mono.just(savedCoupon));

        when(couponMapper.toDto(any(Coupon.class)))
                .thenReturn(CouponDTO.builder()
                        .code("1111")
                        .discount(BigDecimal.TEN)
                        .minBasketValue(BigDecimal.valueOf(50))
                        .build());

        Mono<CouponDTO> result = couponService.createCoupon(couponDTO);

        StepVerifier.create(result)
                .expectNextMatches(coupon -> {
                    assertThat(coupon.getCode()).isEqualTo("1111");
                    assertThat(coupon.getDiscount()).isEqualTo(BigDecimal.TEN);
                    assertThat(coupon.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(50));
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void apply_should_apply_discount_successfully() {
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO("COUPON123", getSampleBasketDTO(BigDecimal.TEN));
        Coupon coupon = Coupon.builder()
                .code("COUPON123")
                .discount(BigDecimal.ONE)
                .minBasketValue(BigDecimal.TEN)
                .build();

        when(basketMapper.toBasket(any())).thenReturn(getSampleBasket(BigDecimal.TEN));
        when(couponRepository.findByCode("COUPON123")).thenReturn(Mono.just(coupon));
        when(basketMapper.toDto(any())).thenReturn(getSampleBasketDTO(BigDecimal.TEN));

        Mono<BasketDTO> result = couponService.applyCoupon(requestDTO);

        StepVerifier.create(result)
                .expectNextMatches(basketDTO -> {
                    assertThat(basketDTO.getAppliedDiscount()).isEqualTo(BigDecimal.ONE);
                    assertTrue(basketDTO.isApplicationSuccessful());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void apply_should_throw_coupon_not_found() {
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO("INVALID", getSampleBasketDTO(BigDecimal.TEN));

        when(basketMapper.toBasket(any())).thenReturn(getSampleBasket(BigDecimal.TEN));
        when(couponRepository.findByCode("INVALID")).thenReturn(Mono.empty());

        Mono<BasketDTO> result = couponService.applyCoupon(requestDTO);

        StepVerifier.create(result)
                .expectError(CouponNotFoundException.class)
                .verify();
    }

    @Test
    void apply_should_throw_insufficient_basket_value() {
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO("COUPON123", getSampleBasketDTO(BigDecimal.ONE));

        Coupon coupon = Coupon.builder()
                .code("COUPON123")
                .discount(BigDecimal.ONE)
                .minBasketValue(BigDecimal.TEN)
                .build();

        when(basketMapper.toBasket(any())).thenReturn(getSampleBasket(BigDecimal.ONE));
        when(couponRepository.findByCode("COUPON123")).thenReturn(Mono.just(coupon));

        Mono<BasketDTO> result = couponService.applyCoupon(requestDTO);

        StepVerifier.create(result)
                .expectError(InsufficientBasketValueException.class)
                .verify();
    }

    @Test
    void apply_should_throw_invalid_discount() {
        ApplicationRequestDTO requestDTO = new ApplicationRequestDTO("COUPON123", getSampleBasketDTO(BigDecimal.TEN));

        Coupon coupon = Coupon.builder()
                .code("COUPON123")
                .discount(BigDecimal.valueOf(50))
                .minBasketValue(BigDecimal.TEN)
                .build();

        when(basketMapper.toBasket(any())).thenReturn(getSampleBasket(BigDecimal.TEN));
        when(couponRepository.findByCode("COUPON123")).thenReturn(Mono.just(coupon));

        Mono<BasketDTO> result = couponService.applyCoupon(requestDTO);

        StepVerifier.create(result)
                .expectError(InvalidDiscountException.class)
                .verify();
    }

    private BasketDTO getSampleBasketDTO(BigDecimal value) {
        return BasketDTO.builder()
                .value(value)
                .appliedDiscount(BigDecimal.ONE)
                .applicationSuccessful(Boolean.TRUE)
                .build();
    }

    private Basket getSampleBasket(BigDecimal value) {
        return Basket.builder()
                .value(value)
                .appliedDiscount(BigDecimal.ZERO)
                .applicationSuccessful(Boolean.FALSE)
                .build();
    }
}
