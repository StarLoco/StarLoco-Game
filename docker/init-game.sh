#!/usr/bin/env sh

# check if config file exists
if ! test -f "${STARLOCO_CONFIG_PATH}"; then
  echo "Config file does not exist, using default file"
  cp /embedded/game.config.properties ${STARLOCO_CONFIG_PATH}
fi


# check if scripts directory exists
if ! test -f /app/scripts; then
  echo "Script directory does not exist, using default package"
  cp -r /embedded/scripts /app/
fi

# Run game server
exec java -jar game.jar