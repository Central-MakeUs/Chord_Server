FROM eclipse-temurin:21-jre
WORKDIR /app

COPY service/auth/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]