# Multi-stage build for employee_service
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy pom.xml and mvnw files first for dependency caching
COPY pom.xml mvnw mvnw.cmd ./
COPY src ./src


# Final stage - minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Copy the built artifact from build stage
COPY target/employee_service-0.0.1-SNAPSHOT.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose Employee service port
EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

