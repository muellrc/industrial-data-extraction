FROM maven:3.6.3 AS maven

WORKDIR /usr/src/app
COPY . /usr/src/app
# Compile and package the application to an executable JAR
RUN mvn package 

# For Java 11
FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /opt/app

COPY --from=maven data-extractor/industrialdataextraction-1.0.jar /opt/app/

ENTRYPOINT ["java","-jar","industrialdataextraction-1.0.jar"]