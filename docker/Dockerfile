FROM amazoncorretto:21-alpine

ENV STARLOCO_CONFIG_PATH /app/config/game.config.properties

COPY ./docker/init-game.sh /init-game.sh
COPY ./docker/game.config.properties /embedded/game.config.properties
COPY ./scripts /embedded/scripts/

COPY ./build/libs/game.jar /app/game.jar

VOLUME ["/app/config"]
WORKDIR /app

CMD ["/init-game.sh"]