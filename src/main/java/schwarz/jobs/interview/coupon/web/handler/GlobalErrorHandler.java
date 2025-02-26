package schwarz.jobs.interview.coupon.web.handler;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.exception.CouponCodeAlreadyExistsException;
import schwarz.jobs.interview.coupon.core.exception.CouponNotFoundException;
import schwarz.jobs.interview.coupon.core.exception.InsufficientBasketValueException;
import schwarz.jobs.interview.coupon.core.exception.InvalidDiscountException;
import schwarz.jobs.interview.coupon.web.errors.ConflictError;
import schwarz.jobs.interview.coupon.web.errors.NotFoundError;
import schwarz.jobs.interview.coupon.web.errors.UnprocessableEntityError;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public Mono<UnprocessableEntityError> handleValidationExceptions(WebExchangeBindException ex) {

        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse("Message unavailable"),
                        (existing, replacement) -> existing
                ));

        UnprocessableEntityError errorResponse = UnprocessableEntityError.builder()
                .message("Validation error")
                .errors(errors)
                .build();

        return Mono.just(errorResponse);
    }

    @ExceptionHandler(CouponCodeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ConflictError> handleCouponCodeAlreadyExistsException() {

        ConflictError errorResponse = ConflictError.builder()
                .message("Coupon code already exists")
                .build();

        return Mono.just(errorResponse);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<NotFoundError> handleCouponNotExistsException() {

        NotFoundError errorResponse = NotFoundError.builder()
                .message("Coupon code not exists")
                .build();

        return Mono.just(errorResponse);
    }

    @ExceptionHandler(InsufficientBasketValueException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ConflictError> handleCouponCodeNotApplicableException() {

        ConflictError errorResponse = ConflictError.builder()
                .message("Insufficient basket value to apply coupon")
                .build();

        return Mono.just(errorResponse);
    }

    @ExceptionHandler(InvalidDiscountException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ConflictError> handleInvalidDiscountException() {

        ConflictError errorResponse = ConflictError.builder()
                .message("Invalid discount exceeding basket value")
                .build();

        return Mono.just(errorResponse);
    }

}
