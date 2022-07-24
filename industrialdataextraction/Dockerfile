FROM maven:3.8.6-openjdk-18 AS build
WORKDIR /app
COPY . /app
#RUN mvn -q clean package -DskipTests
#FROM maven:3.8.6-openjdk-18
#COPY --from=build app/industrialdataextraction-1.0.jar industrialdataextraction.jar
#RUN chmod +x industrialdataextraction.jar
ENTRYPOINT ["java","-jar","/app/industrialdataextraction-1.0.jar"]