FROM ubuntu:latest
FROM maven as maven
LABEL authors="nathanzjukes@gmail.com"

ENTRYPOINT ["top", "-b"]

WORKDIR /usr/src/app
COPY . /usr/src/app

# Compile and package the application to an executable JAR
RUN mvn package

# Java 17
FROM openjdk:17-alpine

ARG JAR_FILE=rater-rate-control-service.jar

WORKDIR /opt/app

# Copy the jar from the maven stage to the /opt/app directory of the current stage.
COPY --from=maven /usr/src/app/target/${JAR_FILE} /opt/app/

ENTRYPOINT ["java","-jar","rater-rate-control-service.jar"]