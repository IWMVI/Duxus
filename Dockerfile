FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /workspace

# Cache: download dependencies before copying source
COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline --no-transfer-progress

# Build application
COPY src src
RUN mvn --batch-mode clean package --no-transfer-progress

FROM eclipse-temurin:8-jre

WORKDIR /app

RUN groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring

COPY --from=build --chown=spring:spring /workspace/target/duxusdesafio-*.war app.war

USER spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.war"]
