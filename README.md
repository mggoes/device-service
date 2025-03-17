Device Service
---

This is a simple service designed to manage device resources. It is built on top of `Spring Boot` framework and employs
`MongoDB` for data persistence. The following frameworks and tools were utilized in the construction of this
application:

- [Spring Boot](https://docs.spring.io/spring-boot/index.html)
- [MongoDB](https://www.mongodb.com/docs/)
- [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo.spring/tree/spring-3.x.x)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/reference/)
- [Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [Keycloak](https://www.keycloak.org/documentation)
- [Resilience4J](https://github.com/resilience4j/resilience4j)
- [Micrometer](https://docs.micrometer.io/tracing/reference/)
- [Jaeger](https://www.jaegertracing.io/docs/2.4/)
- [Lombok](https://projectlombok.org/)
- [MapStruct](https://mapstruct.org/documentation/stable/reference/html/)
- [JaCoCo](https://www.eclemma.org/jacoco/)
- [Mockito](https://site.mockito.org/)

---

### Running Locally

> Use Java 23 or above to build.

Run `docker compose` command to build and start the aplication:

```shell
docker compose --profile prod up
```

Now you can access the [API documentation](http://localhost:8080/swagger-ui/index.html).

**Note: Keycloak service takes a while to start, and it delays the service startup.**

---

### Running Unit Tests

> Use Java 23 or above to build.

Run `gradle build` command to build and execute unit tests:

```shell
./gradlew clean build
```

Now you can access the [JaCoCo Report](build/reports/jacoco/test/html/index.html).

---

### Authorization

This service is also a resource server and its endpoints are protected by `Spring Security`. To access them, we need to
pass a `JWT Bearer Token`. We can issue an access token through `Keycloak`, which acts as an authorization server:

```shell
curl --location 'http://localhost:8083/realms/master/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_id=device-service' \
--data-urlencode 'client_secret=ymWPrMbHMGvgJHLBR3gYYxAYkOSUdUHd'
```

Now we can call the service using the access token in the `Authorization` header:

```shell
curl --location 'http://localhost:8080/devices' \
--header 'Authorization: Bearer <ACCESS_TOKEN>'
```

To make requests in the `Swagger UI`, we can authenticate into [Keycloak](http://localhost:8083) using the following
credentials:

- Username: `admin`
- Password: `admin`

---

### Resilience

This service uses `Resilience4J` to improve resilience. All device resource endpoints are protected by
a [circuit breaker](https://github.com/mggoes/device-service/blob/main/src/main/resources/application.yml#L56), and
retrieval endpoints have
a [retry policy](https://github.com/mggoes/device-service/blob/main/src/main/resources/application.yml#L77).

---

### Observability

This service sends request traces using `OpenTelemetry` and `Micrometer`. You can view them
at [Jaeger UI](http://localhost:16686).

---

### Database

This service uses `MongoDB` for data persistence. You can view databases and collections
through [Mongo Express](http://localhost:8082) tool using the following credentials:

- Username: `admin`
- Password: `admin`
