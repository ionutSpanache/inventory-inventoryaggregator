#Base build image with maven and DDAgent
FROM europe-docker.pkg.dev/cf-inventory-development-qq/inventory/mstore-alpine-jre25:latest
LABEL maintainer="andrei.meran@metro-external.digital"

ARG CACHEBUST

ENV TZ=UTC

ADD  /target/inventoryaggregator.jar /app.jar

CMD [ "sh", "-c", "java \
-jar \
-XX:+UseG1GC \
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
-Ddd.trace.analytics.enabled=false \
-Ddd.jdbc.analytics.enabled=false \
-Ddd.service.mapping=postgresql:${DRP_EV_DD_SOLUTION}.${DRP_CF_VERTICAL}.postgresql \
-Ddd.tags=marc_id:${DRP_EV_DD_MARC_ID},solution:${DRP_EV_DD_SOLUTION},solution_id:${DRP_EV_DD_SOLUTION_ID},vertical:${DRP_CF_VERTICAL},project_id:${DRP_CF_PROJECT_ID},env:${DRP_CF_STAGE},service:${DRP_EV_DD_SOLUTION}.${DRP_CF_VERTICAL}.${DRP_CF_SERVICE},version:${COMMIT_SHA} \
-javaagent:/opt/dd-java-agent.jar \
/app.jar" ]
