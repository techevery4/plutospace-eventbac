# For Java 17, try this
FROM eclipse-temurin:17-jdk-alpine

VOLUME /tmp

ADD target/*.jar events.jar

EXPOSE 6000

ENTRYPOINT ["java","-jar","/events.jar"]