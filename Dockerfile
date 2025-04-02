# Etapa 1: Construcción con Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar solo archivos esenciales primero para aprovechar la cache de Docker
COPY pom.xml . 
RUN mvn dependency:resolve

# Luego, copiar el código fuente
COPY src ./src

# Instalar dependencias y compilar la aplicación
RUN mvn clean install -DskipTests

# Etapa 2: Ejecución del JAR con una imagen más ligera
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto 8080 para la aplicación
EXPOSE 8080

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
