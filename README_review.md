# Commit [Reactive WebFlux r2dbc setup]

This commit transitions the application's data access and web layer from a traditional synchronous JPA/JDBC approach to
a reactive asynchronous R2DBC/WebFlux architecture. This shift enables non-blocking operations and improved scalability
by leveraging reactive streams and asynchronous database interactions:

* **Code Style Standardization:** Consistent code formatting and style improvements for better readability and
  maintainability.
* **Gradle and Spring Boot Dependencies Upgrade:** Updated dependencies to ensure compatibility with WebFlux and R2DBC
  reactive features, including necessary Spring Boot and Gradle version bumps.
* **Migration from JPA/JDBC to R2DBC:** Replaced the existing JPA/JDBC data access layer with R2DBC to facilitate
  reactive database interactions, enabling asynchronous and non-blocking data access.
* **H2 Database Configuration Bean:** Introduced a configuration bean to manage the H2 in-memory database, specifically
  tailored for R2DBC connectivity.
* **YAML Modification for H2 Console Disablement:** Configured the application's `application.yml` to disable the H2
  console for incompatibility with jdbc connector

# Commit [Coupon entity fixing + enhancement]

This commit addresses several issues and enhances the `Coupon` entity and its associated database schema:

* **Table Naming Convention:** Updated the `Coupon` table name
* **Snake Case Schema Definition Convention:** Enforced snake case for all schema definitions
* **Unique Index for Coupon Code:** Added a unique index to the `coupon_code` column. This constraint guarantees that
  each coupon code is unique, preventing duplicate entries and ensuring data integrity.
* **Bigint Fix for Coupon Schema:** Corrected the data type for relevant columns in the `Coupon` schema, likely changing
  them to `bigint` where appropriate. This fix addresses potential issues with integer overflow or insufficient range
  for certain coupon-related data.
* **Delegation of Data Validation Responsibility to the DTO:** The responsibility for validating input data has been
  moved to the Data Transfer Object (DTO). This means the DTO now includes logic to ensure that received data meets
  business requirements before being processed by the entity. This promotes early validation, reduces the load on the
  service layer, and improves code cohesion.

# Commit [Retrieve coupons from a code list]

This commit introduces the functionality to retrieve a list of coupons based on a provided list of coupon codes.

* **R2dbcRepository Switch + `findByCodeIn`:** Transitioned the data access layer to utilize R2DBC repositories for
  reactive database interactions.
* **OpenAPI SpringDoc for Documentation Enhancement**
* **Coupon Service - `getCoupons`**
* **CouponDTO Mapper Implementation:** Implemented a mapper to convert Coupon entities to CouponDTOs, ensuring that only
  necessary data is exposed to the
  API.
* **API Operation Docs:** Added comprehensive API operation documentation, including request parameters, response codes,
  and response
  schemas, to provide clear guidance to API consumers.
* **Testing:** Coupon service, resource and mapper
* **Global exception handler for validation errors**