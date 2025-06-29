# Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/todo-api-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /var/log/todoapi

EXPOSE 8080

# JVM 메모리 최적화
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "-Dspring.profiles.active=aws", "app.jar"]