FROM openjdk:8u131-jre-alpine

COPY target/dhss-web-topology.jar /usr/local/dhss-solution/dhss-web-topology/dhss-web-topology.jar

VOLUME /var/log/dhss-solution/dhss-web-topology/
VOLUME /usr/local/dhss-solution/dhss-web-topology/

WORKDIR /usr/local/dhss-solution/dhss-web-topology/

EXPOSE 8080

ENTRYPOINT ["java","-jar","./dhss-web-topology.jar"]
