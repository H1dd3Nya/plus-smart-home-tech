FROM eclipse-temurin:21-jre-jammy
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/config /config
ENTRYPOINT ["java","-jar","/app.jar"]