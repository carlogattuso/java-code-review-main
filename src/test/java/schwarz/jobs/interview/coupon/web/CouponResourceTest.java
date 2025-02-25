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
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;
import schwarz.jobs.interview.coupon.web.errors.DefaultError;
import schwarz.jobs.interview.coupon.web.errors.UnprocessableEntityError;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.API_PREFIX;
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
    void should_return_all_coupons_from_controller() throws JsonProcessingException {
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
    void should_return_bad_request_when_validation_fails() throws JsonProcessingException {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .codes(null)
                .build();

        webTestClient.post().uri(API_PREFIX.concat(COUPON_FILTER_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestDTO))
                .exchange()
                .expectStatus().is4xxClientError().expectBodyList(UnprocessableEntityError.class);
    }

    @Test
    void should_log_when_unexpected_error() throws JsonProcessingException {
        CouponRequestDTO requestDTO = CouponRequestDTO.builder()
                .codes(Arrays.asList("1111", "1234"))
                .build();

        when(couponService.getCoupons(requestDTO))
                .thenReturn(Flux.error(new RuntimeException("Unexpected error")));

        webTestClient.post().uri(API_PREFIX.concat(COUPON_FILTER_PATH))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(requestDTO))
                .exchange()
                .expectStatus().is5xxServerError().expectBodyList(DefaultError.class);
    }

}
