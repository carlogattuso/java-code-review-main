package schwarz.jobs.interview.coupon.web.errors;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class UnprocessableEntityError {

    private String message;

    private Map<String, String> errors;

}
