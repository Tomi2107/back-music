# Etapa 1: Construcción con Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn dependency:resolve
RUN mvn dependency:tree
RUN mvn clean install -DskipTests

# Etapa 2: Ejecución del JAR con una imagen más ligera
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
