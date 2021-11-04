FROM openjdk:11-jre-slim
RUN addgroup --system spring
RUN adduser --system spring
RUN adduser spring spring
USER spring:spring
ARG JAR_FILE=target/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]