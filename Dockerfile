#Base build image with maven and DDAgent
#FROM registry.metroscales.io/inventory/mstore-alpine-jre11:latest

#Base build image with maven and DDAgent
FROM adoptopenjdk/openjdk15

ENV LANG en_GB.UTF-8

WORKDIR /opt


### Download datatog agent library
RUN apt update
RUN apt-get install -y wget
RUN wget -O dd-java-agent.jar https://dtdg.co/latest-java-tracer

LABEL maintainer="cristiangabriel.jena@metrosystems.net"

ARG CACHEBUST

ENV TZ=UTC

ADD  /target/inventoryaggregator.jar /app.jar

CMD [ "sh", "-c", "java \
-jar \
-XX:+UnlockExperimentalVMOptions \
-XX:+UseShenandoahGC \
-XX:ShenandoahUncommitDelay=5000 \
-XX:ShenandoahGuaranteedGCInterval=50000 \
-XX:+UseStringDeduplication \
-XX:MinRAMPercentage=50.0 \
-XX:MaxRAMPercentage=80.0 \
-Djava.security.egd=file:/dev/./urandom \
-Dlog4j2.formatMsgNoLookups=True \
-Ddd.agent.host=${DRP_CF_KUBERNETES_HOST_IP} \
-Ddd.service=${DRP_EV_DD_SOLUTION}.${DRP_CF_VERTICAL}.${DRP_CF_SERVICE} \
-Ddd.env=${DRP_CF_STAGE} \
-Ddd.version=${COMMIT_SHA} \
-Ddd.logs.injection=true \
-Ddd.trace.analytics.enabled=true \
-Ddd.jdbc.analytics.enabled=true \
-Ddd.service.mapping=postgresql:${DRP_EV_DD_SOLUTION}.${DRP_CF_VERTICAL}.postgresql \
-Ddd.tags=solution:${DRP_EV_DD_SOLUTION},vertical:${DRP_CF_VERTICAL},service:${DRP_EV_DD_SOLUTION}.${DRP_CF_VERTICAL}.${DRP_CF_SERVICE} \
-Ddd.trace.global.tags=marc_id:${DRP_EV_DD_MARC_ID},env:${DRP_CF_STAGE},solution:${DRP_EV_DD_SOLUTION},solution_id:${DRP_EV_DD_SOLUTION_ID},vertical:${DRP_CF_VERTICAL},service:${DRP_EV_DD_SOLUTION}.${DRP_CF_VERTICAL}.${DRP_CF_SERVICE} \
-javaagent:/opt/dd-java-agent.jar \
/app.jar" ]