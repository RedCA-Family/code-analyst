# Maven build stage
FROM maven:3.6.3-jdk-8 as build
WORKDIR /app

LABEL maintainer="switchover@gmail.com"

COPY pom.xml .
COPY src ./src
COPY lib ./lib

RUN mvn clean package -DskipTests

# JDK run stage
FROM openjdk:8-jre
WORKDIR /app

ENTRYPOINT ["java","-jar","target/Code-Analyst-[0-9]*.[0-9]*.[0-9]*.jar"]
