# Use a standard OpenJDK image with Maven
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Package the application (this will include the PostgreSQL driver)
RUN mvn clean package -DskipTests

# Use a smaller JRE image to run the app
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Explicitly set the port
ENV PORT=8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]