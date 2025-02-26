package schwarz.jobs.interview.coupon.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.exception.CouponCodeAlreadyExistsException;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;
import schwarz.jobs.interview.coupon.web.errors.ConflictError;
import schwarz.jobs.interview.coupon.web.errors.DefaultError;
import schwarz.jobs.interview.coupon.web.errors.UnprocessableEntityError;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.API_PREFIX;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_CREATE_PATH;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_FILTER_PATH;

@WebFluxTest(CouponResource.class)
class CouponResourceTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CouponService couponService;

    @Test
    void filter_should_return_all_coupons_from_controller() throws JsonProcessingException {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .codes(Arrays.asList("1111", "1234"))
                .build();

        Flux<CouponDTO> mockCoupons = Flux.just(
                CouponDTO.builder().code("1111").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build(),
                CouponDTO.builder().code("1234").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build()
        );

        when(couponService.getCoupons(any(CouponRequestDTO.class))).thenReturn(mockCoupons);

        webTestClient.post().uri(API_PREFIX.concat(COUPON_FILTER_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestDTO))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CouponDTO.class)
                .hasSize(2)
                .contains(
                        CouponDTO.builder().code("1111").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build(),
                        CouponDTO.builder().code("1234").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build()
                );
    }

    @Test
    void filter_should_return_422_when_validation_fails() throws JsonProcessingException {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .codes(null)
                .build();

        webTestClient.post().uri(API_PREFIX.concat(COUPON_FILTER_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestDTO))
                .exchange()
                .expectStatus().is4xxClientError().expectBody(UnprocessableEntityError.class);
    }

    @Test
    void filter_should_return_500_when_unexpected_error() throws JsonProcessingException {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .codes(Arrays.asList("1111", "1234"))
                .build();

        when(couponService.getCoupons(requestDTO))
                .thenReturn(Flux.error(new RuntimeException("Unexpected error")));

        webTestClient.post().uri(API_PREFIX.concat(COUPON_FILTER_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestDTO))
                .exchange()
                .expectStatus().is5xxServerError().expectBody(DefaultError.class);
    }

    @Test
    void create_should_return_201_when_new_coupon() throws JsonProcessingException {
        CouponDTO couponDTO = CouponDTO.builder()
                .code("TEST1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        Mono<CouponDTO> mockCouponDTO = Mono.just(CouponDTO.builder()
                .code("TEST1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build());

        when(couponService.createCoupon(any(CouponDTO.class))).thenReturn(mockCouponDTO);

        webTestClient.post().uri(API_PREFIX.concat(COUPON_CREATE_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(couponDTO))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CouponDTO.class)
                .isEqualTo(
                        CouponDTO.builder().code("TEST1234").discount(BigDecimal.TEN).minBasketValue(BigDecimal.valueOf(50)).build()
                );
    }

    @Test
    void create_should_return_409_when_coupon_code_already_exists() throws JsonProcessingException {
        CouponDTO couponDTO = CouponDTO.builder()
                .code("TEST1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        when(couponService.createCoupon(any(CouponDTO.class)))
                .thenReturn(Mono.error(new CouponCodeAlreadyExistsException("TEST1234")));

        webTestClient.post().uri(API_PREFIX.concat(COUPON_CREATE_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(couponDTO))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ConflictError.class);
    }

    @Test
    void create_should_return_422_when_coupon_request_is_invalid() throws JsonProcessingException {
        CouponDTO couponDTO = CouponDTO.builder()
                .code("")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        webTestClient.post().uri(API_PREFIX.concat(COUPON_CREATE_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(couponDTO))
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(UnprocessableEntityError.class);
    }

    @Test
    void create_should_return_500_when_unexpected_error() throws JsonProcessingException {
        CouponDTO couponDTO = CouponDTO.builder()
                .code("TEST1234")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        when(couponService.createCoupon(couponDTO))
                .thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        webTestClient.post().uri(API_PREFIX.concat(COUPON_CREATE_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(couponDTO))
                .exchange()
                .expectStatus().is5xxServerError().expectBody(DefaultError.class);
    }

}
