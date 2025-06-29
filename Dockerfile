# Dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

# JAR 파일 복사
COPY build/libs/*.jar app.jar

# 로그 디렉토리 생성
RUN mkdir -p /var/log/todoapi

# 포트 노출
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=aws", "app.jar"]