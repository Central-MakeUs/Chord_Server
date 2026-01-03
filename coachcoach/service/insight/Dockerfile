FROM eclipse-temurin:21-jre
WORKDIR /app

COPY service/insight/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]