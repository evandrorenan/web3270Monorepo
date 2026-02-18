# Use a lightweight JRE image for runtime
FROM eclipse-temurin:21-jre

# Set the working directory
WORKDIR /app

# Add a non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the pre-built fat jar from the target directory
# This assumes the jar has been built locally (e.g., via mvn clean package)
COPY target/*.jar app.jar

# Ensure the app jar and logs directory (if it exists) are owned by the spring user
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]