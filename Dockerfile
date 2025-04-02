# Etapa 1: Construcción con Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . . 

# Instalar dependencias y compilar con logs detallados
RUN mvn dependency:resolve
RUN mvn clean install -U -X -DskipTests

# Etapa 2: Ejecución con una imagen ligera
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
