FROM eclipse-temurin:21-jre
WORKDIR /app

COPY infra/eureka/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
