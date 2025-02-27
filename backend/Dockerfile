# Use OpenJDK 17 as the base image
FROM openjdk:17-jdk-slim AS build

# Set working directory
WORKDIR /app

# Copy Maven Wrapper files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Grant execution permission to Maven Wrapper
RUN chmod +x mvnw

# Download dependencies (this step helps cache dependencies)
RUN ./mvnw dependency:go-offline

# Copy the entire project and build the application
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Create a minimal image for the runtime
FROM openjdk:17-jdk-slim

# Set working directory for runtime
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
