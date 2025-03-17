FROM gradle:8.13-jdk23 AS builder

ARG BUILD_CACHE_PATH=.gradle

WORKDIR /app
COPY . .
RUN gradle -g $BUILD_CACHE_PATH --build-cache build -x test

FROM amazoncorretto:23-alpine3.21

EXPOSE 8080
EXPOSE 8081

COPY --from=builder /app/build/libs/device-service-1.0.0.jar /app.jar

ENTRYPOINT java $JAVA_OPTS -jar /app.jar
