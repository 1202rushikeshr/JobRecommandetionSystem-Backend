FROM openjdk:21
WORKDIR /app
COPY ./target/job-recommendation-system.jar job-portal.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "job-portal.jar"]