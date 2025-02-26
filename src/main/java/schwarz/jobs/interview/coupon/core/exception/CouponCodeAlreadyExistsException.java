package schwarz.jobs.interview.coupon.core.exception;

public class CouponCodeAlreadyExistsException extends RuntimeException {
    public CouponCodeAlreadyExistsException(String message) {
        super(message);
    }
}
