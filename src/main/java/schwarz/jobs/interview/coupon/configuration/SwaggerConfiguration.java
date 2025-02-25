package schwarz.jobs.interview.coupon.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon Application API Docs")
                        .description("This API provides endpoints for creating and retrieving coupons, as well as applying coupons to shopping baskets.")
                        .version("1.0"));
    }

}
