# Runtime stage
FROM eclipse-temurin:17-jre

# Install curl for health checks and create non-root user
RUN apt-get update && apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --gid 1001 appuser

# Set working directory
WORKDIR /app

# Copy only the main executable JAR file from build stage
COPY --from=build /app/target/EventifiedBackend-*.jar ./
RUN find . -name "EventifiedBackend-*.jar" ! -name "*javadoc*" -exec mv {} app.jar \;

# Change ownership to non-root user
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=180s --retries=3 \
    CMD curl --fail http://localhost:8080/actuator/health || exit 1

# JVM optimization for containers and faster startup
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom -XX:TieredStopAtLevel=1"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]