FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/ms-usuario-0.0.1-SNAPSHOT.jar /app/ms-usuario.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/ms-usuario.jar"]
