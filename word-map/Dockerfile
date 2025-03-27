FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY word-map /app
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/word-map-0.0.1-SNAPSHOT.jar /app/word-map-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/word-map-0.0.1-SNAPSHOT.jar"]
