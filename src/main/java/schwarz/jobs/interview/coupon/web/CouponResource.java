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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.models.Basket;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;
import schwarz.jobs.interview.coupon.web.errors.ConflictError;
import schwarz.jobs.interview.coupon.web.errors.DefaultError;
import schwarz.jobs.interview.coupon.web.errors.UnprocessableEntityError;

import javax.validation.Valid;
import java.net.URI;

import static schwarz.jobs.interview.coupon.constants.ApiConstants.API_PREFIX;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_CREATE_PATH;
import static schwarz.jobs.interview.coupon.constants.ApiConstants.COUPON_FILTER_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_PREFIX)
@Tag(name = "Coupon Resource")
@Slf4j
public class CouponResource {

    private final CouponService couponService;

    /**
     * @param applicationRequestDTO
     * @return
     */
    //@ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 1")
    @PostMapping(value = "/apply")
    public ResponseEntity<Basket> apply(
            //@ApiParam(value = "Provides the necessary basket and customer information required for the coupon application", required = true)
            @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO) {

        log.info("Applying coupon");

        /*final Optional<Basket> basket =
                couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode());

        if (basket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!applicationRequestDTO.getBasket().isApplicationSuccessful()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        log.info("Applied coupon");*/

        return ResponseEntity.ok().body(applicationRequestDTO.getBasket());
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

        Mono<CouponDTO> savedCoupon = couponService.createCoupon(couponDTO);

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
