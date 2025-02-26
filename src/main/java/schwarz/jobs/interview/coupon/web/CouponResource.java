package schwarz.jobs.interview.coupon.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;
import schwarz.jobs.interview.coupon.web.errors.ConflictError;
import schwarz.jobs.interview.coupon.web.errors.DefaultError;
import schwarz.jobs.interview.coupon.web.errors.NotFoundError;
import schwarz.jobs.interview.coupon.web.errors.UnprocessableEntityError;

import javax.validation.Valid;
import java.net.URI;

import static schwarz.jobs.interview.coupon.constants.ApiConstants.API_PREFIX;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_APPLY_PATH;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_CREATE_PATH;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_FILTER_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PREFIX)
@Tag(name = "Coupon Resource")
@Slf4j
public class CouponResource {

    private final CouponService couponService;

    @PutMapping(COUPON_APPLY_PATH)
    @Operation(summary = "Apply discount coupon to a specific basket")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CouponDTO.class)))
    @ApiResponse(responseCode = "404", description = "Coupon code not exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotFoundError.class)))
    @ApiResponse(responseCode = "409", description = "Conflict - Invalid discount",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConflictError.class)))
    @ApiResponse(responseCode = "422", description = "Unprocessable entity",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnprocessableEntityError.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultError.class)))
    @ApiResponse(responseCode = "500", description = "Unexpected Error",
            content = @Content(schema = @Schema(implementation = DefaultError.class)))
    public ResponseEntity<Mono<BasketDTO>> apply(
            @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO) {

        Mono<BasketDTO> basket = couponService.applyCoupon(applicationRequestDTO)
                .doOnSuccess(basketDTO ->
                        log.info("Discount applied to basket: {}", basketDTO.toString()));

        return ResponseEntity.ok(basket);
    }

    @PostMapping(COUPON_CREATE_PATH)
    @Operation(summary = "Create a new coupon")
    @ApiResponse(responseCode = "201", description = "Success",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CouponDTO.class)))
    @ApiResponse(responseCode = "409", description = "Conflict - Coupon code already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConflictError.class)))
    @ApiResponse(responseCode = "422", description = "Unprocessable entity",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnprocessableEntityError.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultError.class)))
    @ApiResponse(responseCode = "500", description = "Unexpected Error",
            content = @Content(schema = @Schema(implementation = DefaultError.class)))
    public ResponseEntity<Mono<CouponDTO>> create(@RequestBody @Valid final CouponDTO couponDTO) {

        Mono<CouponDTO> savedCoupon = couponService.createCoupon(couponDTO)
                .doOnSuccess(savedCouponDTO ->
                        log.info("New coupon created: {}", savedCouponDTO.toString()));

        URI location = URI.create(API_PREFIX.concat(COUPON_CREATE_PATH).concat(String.format("/%s", couponDTO.getCode())));
        return ResponseEntity.created(location).body(savedCoupon);
    }

    @PostMapping(COUPON_FILTER_PATH)
    @Operation(summary = "Filter coupons based on a code list request")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CouponDTO.class)))
    @ApiResponse(responseCode = "422", description = "Unprocessable entity",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UnprocessableEntityError.class)))
    @ApiResponse(responseCode = "400", description = "Bad Request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DefaultError.class)))
    @ApiResponse(responseCode = "500", description = "Unexpected Error",
            content = @Content(schema = @Schema(implementation = DefaultError.class)))
    public ResponseEntity<Flux<CouponDTO>> getCoupons(
            @RequestBody @Valid final CouponRequestDTO couponRequestDTO) {

        Flux<CouponDTO> coupons = couponService.getCoupons(couponRequestDTO);

        return ResponseEntity.ok(coupons);
    }

}
