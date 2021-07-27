#!/bin/bash

cd ./bin
./sf.sh start
sleep 3
tail -f ./logs/spring.log