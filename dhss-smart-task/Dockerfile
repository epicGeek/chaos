FROM openjdk:8u131-jre-alpine
ENV DHSS_MODULE_NAME dhss-smart-task

# COPY target/${DHSS_MODULE_NAME}.jar /usr/local/dhss-solution/${DHSS_MODULE_NAME}/${DHSS_MODULE_NAME}.jar
# COPY src/main/resources/* /usr/local/dhss-solution/${DHSS_MODULE_NAME}/config/

VOLUME /var/log/dhss-solution/${DHSS_MODULE_NAME}/
VOLUME /usr/local/dhss-solution/${DHSS_MODULE_NAME}/

WORKDIR /usr/local/dhss-solution/${DHSS_MODULE_NAME}/

ENTRYPOINT ["java","-jar","./"${DHSS_MODULE_NAME}".jar"]
