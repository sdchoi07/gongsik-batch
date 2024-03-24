FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/gongsik-batch-0.0.1-SNAPSHOT.jar /app/gongsik-batch-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "gongsik-batch-0.0.1-SNAPSHOT.jar"]
