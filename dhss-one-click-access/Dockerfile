FROM openjdk:8u141-jre

COPY target/dhss-one-click-access.jar /usr/local/dhss-solution/dhss-one-click-access/dhss-one-click-access.jar
COPY src/main/resources/* /usr/local/dhss-solution/dhss-one-click-access/config/

VOLUME /var/log/dhss-solution/dhss-one-click-access/
VOLUME /usr/local/dhss-solution/dhss-one-click-access/

WORKDIR /usr/local/dhss-solution/dhss-one-click-access/

EXPOSE 8080

ENTRYPOINT ["java","-jar","./dhss-one-click-access.jar"]
