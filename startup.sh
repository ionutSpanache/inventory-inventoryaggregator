#!/usr/bin/env sh

JAVA_OPTS=$(cat <<-END
-XX:+UseG1GC
-XX:+UseStringDeduplication
-XX:MinRAMPercentage=50.0
-XX:MaxRAMPercentage=80.0
-Djava.security.egd=file:/dev/./urandom
-Ddd.agent.host=$DRP_CF_KUBERNETES_HOST_IP
-Ddd.service=$DRP_EV_DD_SOLUTION.$DRP_CF_VERTICAL.$DRP_CF_SERVICE
-Ddd.env=$DRP_CF_STAGE
-Ddd.version=$DRP_CF_VERSION:0:1
-Ddd.logs.injection=true
-Ddd.trace.analytics.enabled=true
-Ddd.jdbc.analytics.enabled=true
-Ddd.service.mapping=postgresql:$DRP_EV_DD_SOLUTION.$DRP_CF_VERTICAL.postgresql
-Ddd.tags=solution:$DRP_EV_DD_SOLUTION,vertical:$DRP_CF_VERTICAL,service:$DRP_EV_DD_SOLUTION.$DRP_CF_VERTICAL.$DRP_CF_SERVICE
-Ddd.trace.global.tags=marc_id:$DRP_EV_DD_MARC_ID,env:$DRP_CF_STAGE,solution:$DRP_EV_DD_SOLUTION,solution_id:$DRP_EV_DD_SOLUTION_ID,vertical:$DRP_CF_VERTICAL,service:$DRP_EV_DD_SOLUTION.$DRP_CF_VERTICAL.$DRP_CF_SERVICE
END
)

if [ -d /etc/secrets ]; then
    for filename in /etc/secrets/*; do
        if [ ! -d "$file" ]; then
            export $(echo $(basename "$filename") | awk '{print toupper($0)}')=$(cat "$filename");
        fi;
    done;
fi && export TZ=UTC && exec java -server -javaagent:/dd-java-agent.jar $JAVA_OPTS -jar /inventoryaggregator.jar
