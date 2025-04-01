# Usa una imagen base de Java
FROM openjdk:17-jdk-slim

# Copia tu archivo JAR al contenedor
COPY target/mi-aplicacion.jar /app.jar

# Expón el puerto en el que la aplicación escuchará
EXPOSE 8080

# Ejecuta la aplicación cuando se inicie el contenedor
ENTRYPOINT ["java", "-jar", "/app.jar"]
