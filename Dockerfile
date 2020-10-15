#Build image
FROM openjdk:11-slim-sid as builder

# Add Maintainer Info
LABEL maintainer="cristiangabriel.jena@metrosystems.net"

# Needed to fix 'Fontconfig warning: ignoring C.UTF-8: not a valid language tag'
ENV LANG en_GB.UTF-8

RUN apt-get update && apt-get -y install maven && apt-get install -y wget && wget -O dd-java-agent.jar https://dtdg.co/latest-java-tracer

ARG CACHEBUST

ADD src /src
ADD pom.xml /pom.xml
ADD scripts /scripts
ADD mvn-settings.xml /mvn-settings.xml
RUN mvn clean install --settings mvn-settings.xml

#Runtim image
FROM openjdk:11-slim-sid
ADD startup.sh /startup.sh
RUN chmod +x /startup.sh
COPY --from=builder /target/inventoryaggregator.jar /inventoryaggregator.jar
COPY --from=builder /dd-java-agent.jar /dd-java-agent.jar
CMD /startup.sh
