package schwarz.jobs.interview.coupon.core.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import schwarz.jobs.interview.coupon.core.mapper.impl.BasketMapperImpl;
import schwarz.jobs.interview.coupon.core.models.Basket;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BasketMapperImplTest {

    private final BasketMapperImpl basketMapper;

    BasketMapperImplTest() {
        this.basketMapper = new BasketMapperImpl();
    }

    @Test
    void should_map_basket_to_dto() {
        Basket basket = Basket.builder()
                .value(BigDecimal.TEN)
                .appliedDiscount(BigDecimal.ONE)
                .applicationSuccessful(Boolean.TRUE)
                .build();

        BasketDTO dto = basketMapper.toDto(basket);

        assertThat(dto.getValue()).isEqualTo(BigDecimal.TEN);
        assertThat(dto.getAppliedDiscount()).isEqualTo(BigDecimal.ONE);
        assertThat(dto.isApplicationSuccessful()).isEqualTo(Boolean.TRUE);
    }

    @Test
    void should_map_dto_to_basket() {
        BasketDTO dto = BasketDTO.builder()
                .value(BigDecimal.TEN)
                .appliedDiscount(BigDecimal.ONE)
                .applicationSuccessful(Boolean.TRUE)
                .build();

        Basket basket = basketMapper.toBasket(dto);

        assertThat(basket.getValue()).isEqualTo(BigDecimal.TEN);
        assertThat(basket.getAppliedDiscount()).isEqualTo(BigDecimal.ONE);
        assertThat(basket.isApplicationSuccessful()).isEqualTo(Boolean.TRUE);
    }

}

