package schwarz.jobs.interview.coupon.web.errors;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class BaseError {
    private String message;
}
