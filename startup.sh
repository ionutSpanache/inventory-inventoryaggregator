#!/usr/bin/env sh
#if [ -d /etc/secrets ]; then
#    for filename in /etc/secrets/*; do
#        if [ ! -d "$file" ]; then
#            export $(echo $(basename "$filename") | awk '{print toupper($0)}')=$(cat "$filename");
#        fi;
#    done;
#fi
#export IDAM_CLIENT_CLIENT_SECRET=$(cat /etc/secrets/IDAM_CLIENT_CLIENT_SECRET)
#export ARTICLE_CACHE_PASSWORD=$(cat /etc/secrets/ARTICLE_CACHE_PASSWORD)
#java -Xms190M -Xmx600M -jar ./target/inventoryaggregator.jar
if [ -d /etc/secrets ]; then
    for filename in /etc/secrets/*; do
        if [ ! -d "$file" ]; then
            export $(echo $(basename "$filename") | awk '{print toupper($0)}')=$(cat "$filename");
        fi;
    done;
fi && export TZ=UTC && java -XX:+UseG1GC -Xms190M -Xmx600M -server -jar /target/inventoryaggregator-0.0.1.jar ;