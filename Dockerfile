# First stage: build the application using Maven with JDK 23
FROM maven:3.9.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Second stage: run the application using JDK 23
FROM eclipse-temurin:23-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/jobportal-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
