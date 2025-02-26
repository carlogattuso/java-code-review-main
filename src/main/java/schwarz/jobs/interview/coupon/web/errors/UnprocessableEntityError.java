package schwarz.jobs.interview.coupon.web.errors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UnprocessableEntityError extends BaseError {
    private Map<String, String> errors;
}
