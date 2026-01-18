# Dockerfile
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.banco.digital.BancoDigitalApplication"]
