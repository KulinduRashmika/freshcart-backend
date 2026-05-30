# Build stage
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - using eclipse-temurin (actively maintained)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Set the port
ENV PORT=8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]