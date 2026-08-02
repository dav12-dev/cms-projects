# Stage 1: Build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Runtime with Java
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional, for documentation)
EXPOSE 8080

# Run the application with the PORT environment variable from Render
# This ensures the app binds to the correct port (either Render's PORT or 8080 locally)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]