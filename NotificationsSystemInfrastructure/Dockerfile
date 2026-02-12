FROM maven:3.9-eclipse-temurin-25-alpine AS builder

WORKDIR /app

COPY ./pom.xml .

COPY ./NotificationsSystemApi/pom.xml ./NotificationsSystemApi/
COPY ./NotificationsSystemApplication/pom.xml ./NotificationsSystemApplication/
COPY ./NotificationsSystemDomain/pom.xml ./NotificationsSystemDomain/
COPY ./NotificationsSystemInfrastructure/pom.xml ./NotificationsSystemInfrastructure/
COPY ./NotificationsSystemFront/pom.xml ./NotificationsSystemFront/
COPY ./NotificationsSystemDocker/pom.xml ./NotificationsSystemDocker/

RUN mvn dependency:go-offline -pl NotificationsSystemInfrastructure -am

COPY ./NotificationsSystemApi/src ./NotificationsSystemApi/src
COPY ./NotificationsSystemApplication/src ./NotificationsSystemApplication/src
COPY ./NotificationsSystemDomain/src ./NotificationsSystemDomain/src
COPY ./NotificationsSystemInfrastructure/src ./NotificationsSystemInfrastructure/src

RUN mvn clean package -pl NotificationsSystemInfrastructure -am -DskipTests

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/NotificationsSystemInfrastructure/target/NotificationsSystemInfrastructure-*-exec.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]