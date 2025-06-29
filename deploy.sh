#!/bin/bash
# deploy.sh

echo "=== TodoAPI 배포 시작 ==="

# 환경변수 설정
export DB_HOST=$1
export DB_PASSWORD=$2

if [ -z "$DB_HOST" ] || [ -z "$DB_PASSWORD" ]; then
    echo "사용법: ./deploy.sh [DB_HOST] [DB_PASSWORD]"
    exit 1
fi

echo "DB_HOST: $DB_HOST"
echo "환경변수 설정 완료"

# 기존 컨테이너 중지 및 제거
echo "기존 컨테이너 중지 중..."
docker-compose down

# 애플리케이션 빌드
echo "애플리케이션 빌드 중..."
./gradlew clean build -x test

# 빌드 확인
if [ ! -f build/libs/*.jar ]; then
    echo "❌ 빌드 실패 - JAR 파일을 찾을 수 없습니다"
    exit 1
fi

# Docker 이미지 빌드 및 실행
echo "Docker 컨테이너 시작 중..."
docker-compose up -d --build

# Health Check
echo "배포 완료! Health Check 중..."
sleep 30

for i in {1..5}; do
    echo "Health Check 시도 $i/5..."
    if curl -f http://localhost:8080/api/health; then
        echo ""
        echo "✅ 배포 성공!"
        echo "API 접속: http://$(curl -s ifconfig.me):8080/api/health"
        exit 0
    else
        echo "⏳ Health Check 재시도..."
        sleep 10
    fi
done

echo "❌ 배포 실패 - Health Check 실패"
echo "로그 확인: docker-compose logs"
exit 1