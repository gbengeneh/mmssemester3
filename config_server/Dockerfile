# Multi-stage build for config_server
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy pom.xml and mvnw files first for dependency caching
COPY pom.xml mvnw mvnw.cmd ./
COPY src ./src

# Build the application
RUN apk add --no-cache maven
RUN ./mvnw clean package -DskipTests

# Final stage - minimal runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -s /bin/sh -D appuser

# Copy the built artifact from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose Config server port
EXPOSE 8888

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8888/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

