FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar --no-daemon

FROM openjdk:11
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/hse.db.lab.server-*-all.jar /app/hse_db_lab_server.jar
ENTRYPOINT ["java","-jar","/app/hse_db_lab_server.jar"]
