# Maven build stage
FROM maven:3.6.3-jdk-8 as build
WORKDIR /app

LABEL maintainer="switchover@gmail.com"

COPY pom.xml .
COPY src ./src
COPY lib ./lib

RUN mvn clean package -DskipTests

RUN export code_analyst_version=$(mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec) \
    && cp target/Code-Analyst-${code_analyst_version}.jar Code-Analyst.jar

# JDK run stage
FROM openjdk:8-jre
WORKDIR /project

COPY --from=build /app/Code-Analyst.jar /app/Code-Analyst.jar

ENTRYPOINT ["java","-jar","/app/Code-Analyst.jar"]
