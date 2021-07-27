#!/bin/bash

version=1.0.0-SNAPSHOT

if [ -d './bin' ]; then
  rm -rf ./bin
fi
gradle clean
gradle build
mkdir ./bin
cp ./server/build/libs/tinkertian-snowflake-server-${version}.jar ./bin/.
cp ./scripts/sf.sh ./bin/.
chmod u+x ./bin/sf.sh
rm -rf ./gradle
rm -rf ./build