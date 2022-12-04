FROM gradle:7.6.0 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build

FROM bellsoft/liberica-openjdk-alpine:17

EXPOSE 8080

COPY --from=build /home/gradle/src/entrypoint/build/libs/*.jar /app/app.jar

WORKDIR /app

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=container", "app.jar"]