# ==========================================
# Stage 1: Build the application using Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies (speeds up future builds)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests
# (Note: We skip tests here because they already run in our CI/CD pipeline later. 
# Building should be fast.)

# ==========================================
# Stage 2: Create the lightweight runtime image
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy ONLY the built jar from Stage 1
COPY --from=build /app/target/transaction-api-1.0.0.jar app.jar

# Expose port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]