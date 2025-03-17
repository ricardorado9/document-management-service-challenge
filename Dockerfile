FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/document-management-service-challenge-0.0.1-SNAPSHOT-LOCAL.jar app.jar
COPY src/main/resources/application.properties /app/config/
ENTRYPOINT ["java", "-Dspring.config.location=/app/config/application.properties", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
