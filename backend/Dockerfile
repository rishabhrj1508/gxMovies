# Build stage using Temurin JDK 17
FROM eclipse-temurin:17-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven Wrapper files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Grant execution permission to Maven Wrapper
RUN chmod +x mvnw

# Download dependencies (cache improvement)
RUN ./mvnw dependency:go-offline

# Copy source and build the app
COPY src ./src
RUN ./mvnw clean package -DskipTests


# ----------------------
# Runtime stage
# ----------------------
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
