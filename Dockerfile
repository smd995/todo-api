# Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# 정확한 JAR 파일명으로 복사
COPY build/libs/todo-api-0.0.1-SNAPSHOT.jar app.jar

# 로그 디렉토리 생성
RUN mkdir -p /var/log/todoapi

# 포트 노출
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=aws", "app.jar"]