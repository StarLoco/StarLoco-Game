#!/bin/bash
set -e
gradle jar
cp ./build/libs/game.jar "$(dirname "$0")"