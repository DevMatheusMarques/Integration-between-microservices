FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/ms-notify-0.0.1-SNAPSHOT.jar /app/ms-notify.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/ms-notify.jar"]
