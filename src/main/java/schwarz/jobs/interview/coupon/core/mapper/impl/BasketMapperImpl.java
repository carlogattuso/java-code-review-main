package schwarz.jobs.interview.coupon.core.mapper.impl;

import org.springframework.stereotype.Component;
import schwarz.jobs.interview.coupon.core.mapper.BasketMapper;
import schwarz.jobs.interview.coupon.core.models.Basket;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;

@Component
public class BasketMapperImpl implements BasketMapper {

    @Override
    public Basket toBasket(BasketDTO basketDTO) {
        return Basket.builder()
                .value(basketDTO.getValue())
                .appliedDiscount(basketDTO.getAppliedDiscount())
                .applicationSuccessful(basketDTO.isApplicationSuccessful())
                .build();
    }

    @Override
    public BasketDTO toDto(Basket basket) {
        return BasketDTO.builder()
                .value(basket.getValue())
                .appliedDiscount(basket.getAppliedDiscount())
                .applicationSuccessful(basket.isApplicationSuccessful())
                .build();
    }

}
