# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew :buildFatJar --no-daemon

# Final Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S zorvyn && adduser -S zorvyn -G zorvyn
USER zorvyn
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]