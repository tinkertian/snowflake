version=1.0.0-SNAPSHOT

if [ -d './bin' ]; then
  rm -rf ./bin
fi
gradle clean
gradle build
sleep 3
mkdir ./bin
cp ./server/build/libs/tinkertian-snowflake-server-${version}.jar ./bin/.
cp ./scripts/sf.sh ./bin/.
chmod u+x ./bin/sf.sh
rm -rf ./gradle
rm -rf ./build
cd ./bin
./sf.sh start
sleep 3
tail -f ./logs/spring.log