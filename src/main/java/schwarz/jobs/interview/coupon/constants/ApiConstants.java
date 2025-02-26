package schwarz.jobs.interview.coupon.constants;

public class ApiConstants {

    private ApiConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String API_PREFIX = "/api/coupons";
    public static final String COUPON_FILTER_PATH = "/filter";
    public static final String COUPON_CREATE_PATH = "/create";
    public static final String COUPON_APPLY_PATH = "/apply";
}
