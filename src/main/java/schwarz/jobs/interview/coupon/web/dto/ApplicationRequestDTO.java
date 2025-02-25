package schwarz.jobs.interview.coupon.web.dto;

import lombok.Builder;
import lombok.Data;
import schwarz.jobs.interview.coupon.core.models.Basket;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ApplicationRequestDTO {

    @NotBlank
    private String code;

    @NotNull
    private Basket basket;

}
