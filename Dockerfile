#Base build image with maven and DDAgent
FROM registry.metroscales.io/inventory/inventory-base:latest as builder
LABEL maintainer="cristiangabriel.jena@metrosystems.net"

ARG CACHEBUST

ADD src /src
ADD pom.xml /pom.xml
ADD mvn-settings.xml /mvn-settings.xml
RUN mvn --no-transfer-progress clean install --settings mvn-settings.xml

#Runtime image
FROM registry.metroscales.io/inventory/inventory-runtime:latest
ADD startup.sh /startup.sh
RUN chmod +x /startup.sh
COPY --from=builder /target/inventoryaggregator.jar /inventoryaggregator.jar
COPY --from=builder /dd-java-agent.jar /dd-java-agent.jar
CMD /startup.sh
