FROM openjdk:8-jre-alpine

ENV APPLICATION_USER crypto-consumer
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER
COPY ./build/libs/crypto-consumer-0.0.1-SNAPSHOT.jar /app/crypto-consumer.jar
WORKDIR /app

CMD ["java", "-jar", "crypto-consumer.jar"]