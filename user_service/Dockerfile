# -------- Build Stage --------
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and project files
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# -------- Runtime Stage --------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Expose service port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
 CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Start application
ENTRYPOINT ["java","-jar","app.jar"]