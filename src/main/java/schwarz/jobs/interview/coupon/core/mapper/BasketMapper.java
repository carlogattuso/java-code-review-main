package schwarz.jobs.interview.coupon.core.mapper;

import schwarz.jobs.interview.coupon.core.models.Basket;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;

public interface BasketMapper {

    Basket toBasket(BasketDTO basketDTO);

    BasketDTO toDto(Basket basket);
}