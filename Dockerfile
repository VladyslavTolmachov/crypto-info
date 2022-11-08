FROM adoptopenjdk/openjdk11:alpine-jre
ARG JAR_FILE=target/*.jar
COPY target/crypto-info-0.0.1-SNAPSHOT.jar crypto-info-server-0.0.1.jar
ENTRYPOINT ["java","-jar","/crypto-info-server-0.0.1.jar"]
