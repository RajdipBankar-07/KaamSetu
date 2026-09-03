# ==============================================================================
# 🌾 KaamSetu Backend — Multi-Stage Production Dockerfile
# Base: Eclipse Temurin OpenJDK 21 (LTS) Alpine
# ==============================================================================

# Stage 1: Build & Package
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace/app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml and download dependencies for layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build jar
COPY src src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal Production JRE Image
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# Create non-root system group and user for security
RUN addgroup -S kaamsetu && adduser -S kaamsetu -G kaamsetu

# Install curl for container healthcheck
RUN apk add --no-cache curl

# Copy fat JAR from builder stage
COPY --from=builder /workspace/app/target/kaamsetu-backend-*.jar /app/kaamsetu-api.jar
RUN chown -R kaamsetu:kaamsetu /app

# Switch to non-root user
USER kaamsetu

# Expose API Port
EXPOSE 8088

# Healthcheck configuration
HEALTHCHECK --interval=15s --timeout=5s --retries=3 --start-period=30s \
  CMD curl -f http://localhost:8088/api/v1/health || exit 1

# Production JVM Tuning for Containerized Microservices
ENTRYPOINT ["java", \
  "-XX:+UseG1GC", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:InitialRAMPercentage=50.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "/app/kaamsetu-api.jar"]
