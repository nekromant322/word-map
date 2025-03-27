FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY word-map /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/word-map-0.0.1-SNAPSHOT.jar /app/word-map-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/word-map-0.0.1-SNAPSHOT.jar"]
