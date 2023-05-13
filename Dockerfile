FROM alpine:latest

RUN apk add --no-cache openjdk11-jre

COPY ./build/libs/game.jar /app/game.jar
COPY docker.config.properties /app/game.config.properties
COPY scripts/ /app/scripts/

WORKDIR /app

CMD ["java", "-jar", "game.jar"]