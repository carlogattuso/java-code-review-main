package schwarz.jobs.interview.coupon.core.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import schwarz.jobs.interview.coupon.core.domain.Coupon;

import java.util.List;

@Repository
public interface CouponRepository extends R2dbcRepository<Coupon, Long> {

    Mono<Coupon> findByCode(final String code);

    Flux<Coupon> findByCodeIn(List<String> codes);

}
