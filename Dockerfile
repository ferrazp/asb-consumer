FROM eclipse-temurin:19-jre-alpine
WORKDIR /app
COPY build/libs/asb-consumer-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
