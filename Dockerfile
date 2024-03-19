# Use the base image
FROM pedrombmachado/ntu_lubuntu:soft40051

# Set the working directory
WORKDIR /app

# Copy the pom.xml file
COPY pom.xml .

# Copy the source code
COPY discovery-server ./discovery-server
COPY identity-service ./identity-service
COPY api-gateway ./api-gateway
COPY file-service ./file-service

# Install Java 21 & Maven
RUN apt-get update \
    && apt install -y openjdk-21-jdk \
    && apt-get install -y maven \
    && apt-get clean

# Build the application
RUN  mvn clean package spring-boot:repackage

# Debugging commands
RUN ls -al
RUN ls -al file-service/target

# Expose ports
EXPOSE 8080

# Copy startup script
COPY start-services.sh /app/start-services.sh

# Grant execute permissions to the startup script
RUN chmod +x /app/start-services.sh

# Start the application
CMD ["./start-services.sh"]