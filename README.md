Device Service
---

This is a simple service designed to manage device resources. It is built on the `Spring Boot` framework and employs
`MongoDB` for data persistence. The following frameworks were utilized in the construction of this application:

- [Spring Boot](https://docs.spring.io/spring-boot/index.html)
- [MongoDB](https://www.mongodb.com/docs/)
- [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo.spring/tree/spring-3.x.x)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/reference/)
- [Resilience4J](https://github.com/resilience4j/resilience4j)
- [Micrometer](https://docs.micrometer.io/tracing/reference/)
- [Lombok](https://projectlombok.org/)
- [MapStruct](https://mapstruct.org/documentation/stable/reference/html/)
- [JaCoCo](https://www.eclemma.org/jacoco/)
- [Mockito](https://site.mockito.org/)

---

### Running Locally

> Use Java 23 or above to build.

Run `docker compose` command to build and start the aplication:

```shell
docker compose up
```

Now you can access the [API documentation](http://localhost:8080/swagger-ui/index.html).

---

### Running Unit Tests

> Use Java 23 or above to build.

Run `gradle build` command to build and execute unit tests:

```shell
./gradlew clean build
```

Now you can access the [JaCoCo Report](build/reports/jacoco/test/html/index.html).

---

### Observability

| Feature           | Tool                                                      |
|-------------------|-----------------------------------------------------------|
| API Documentation | [Swagger UI](http://localhost:8080/swagger-ui/index.html) |
| Tracing           | TODO                                                      |

---

### Database

This service uses `MongoDB` for data persistence. You can view databases and collections
through [Mongo Express](http://localhost:8082) tool using the following credentials:

- Username: `admin`
- Password: `admin`
