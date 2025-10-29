# Importing JDK and copying required files
FROM openjdk:17-oracle AS build
WORKDIR /events
COPY pom.xml .
COPY src src

# Copy Maven wrapper
COPY mvnw .
COPY .mvn .mvn

# Set execution permission for the Maven wrapper
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Stage 2: Create the final Docker image using OpenJDK 17
FROM openjdk:17-oracle
VOLUME /tmp

# Copy the JAR from the build stage
COPY --from=build /events/target/*.jar events.jar
ENTRYPOINT ["java","-jar","/events.jar"]
EXPOSE 6000