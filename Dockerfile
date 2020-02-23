FROM openjdk:13-alpine
RUN apk add maven
RUN apk add curl
RUN apk add procps
ADD mvn-settings.xml /mvn-settings.xml
ADD pom.xml /pom.xml
ADD src /src
ADD startup.sh /startup.sh
RUN chmod +x startup.sh
RUN mvn clean install --settings mvn-settings.xml
CMD ./startup.sh
#CMD java -Xms190M -Xmx400M -jar ./target/regator.jar