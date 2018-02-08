FROM openjdk:8u131-jre-alpine

COPY target/dhss-web.jar /usr/local/dhss-solution/dhss-web/dhss-web.jar
COPY src/main/resources/* /usr/local/dhss-solution/dhss-web/config/

VOLUME /var/log/dhss-solution/dhss-web/
VOLUME /usr/local/dhss-solution/dhss-web/

WORKDIR /usr/local/dhss-solution/dhss-web/

EXPOSE 8080

ENTRYPOINT ["java","-jar","./dhss-web.jar"]
