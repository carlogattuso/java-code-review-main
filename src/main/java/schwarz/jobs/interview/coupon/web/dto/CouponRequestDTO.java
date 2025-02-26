package schwarz.jobs.interview.coupon.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CouponRequestDTO", description = "Request object containing a list of coupon codes")
public class CouponRequestDTO {

    @NotNull(message = "Code list cannot be null")
    @Schema(description = "List of 8-character alphanumeric coupon codes", example = "[\"DISC2024\", \"SUMMER24\"]")
    private List<@NotNull String> codes;

}
