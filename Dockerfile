# 1단계: Maven 빌드 환경
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app

# 소스 코드 복사 및 의존성 설치
COPY pom.xml ./
RUN mvn dependency:go-offline

# 소스 코드 복사 및 빌드
COPY src ./src
RUN mvn clean package -DskipTests

# 2단계: JRE 실행 환경
FROM eclipse-temurin:21-jre
WORKDIR /app

# JAR 파일 복사
COPY --from=build /app/target/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]