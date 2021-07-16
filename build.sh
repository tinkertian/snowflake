rm -rf ./bin
gradle build
sleep 1
cp ./scripts/sf.sh ./bin/.
chmod u+x ./bin/sf.sh
rm -rf ./gradle
