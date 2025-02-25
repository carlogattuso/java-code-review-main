package schwarz.jobs.interview.coupon.web.errors;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DefaultError {
    private Instant timestamp;
    private String path;
    private int status;
    private String error;
    private String requestId;
}
