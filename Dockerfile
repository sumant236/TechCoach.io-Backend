# Stage 1: Build the application using Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application using lightweight JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render assigns a dynamic port, so we use PORT environment variable with default 8082
ENV PORT=8082
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]