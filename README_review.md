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
