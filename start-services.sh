#!/bin/bash

# Start the discovery-server
java -jar /app/discovery-server/target/*.jar &

# Wait for the discovery-server to start (adjust the sleep time as needed)
sleep 20

# Start the other services
java -jar /app/identity-service/target/*.jar &
java -jar /app/api-gateway/target/*.jar &
java -jar /app/file-service/target/*.jar
