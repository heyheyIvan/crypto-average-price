FROM openjdk:8-jre-alpine

ENV APPLICATION_USER crypto-producer
RUN adduser -D -g '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER
COPY ./build/libs/crypto-producer-1.0-SNAPSHOT-all.jar /app/crypto-producer.jar
WORKDIR /app

CMD ["java", "-jar", "crypto-producer.jar"]