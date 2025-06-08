FROM openjdk:21
WORKDIR /app
COPY build/libs/JobRecommendationSystem-0.0.1-SNAPSHOT.jar job-portal.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "job-portal.jar"]