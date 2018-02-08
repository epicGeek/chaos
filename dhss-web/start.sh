docker run --name dhss-web-api -d \
-v /home/dhss/GR1/dhss-solution/dhss-web/dhss-web.jar:/usr/local/dhss-solution/dhss-web/dhss-web.jar \
-v /var/log/dhss-solution/dhss-web/:/var/log/dhss-solution/dhss-web/ \
-v /home/dhss/GR1/dhss-solution/dhss-web/config/:/usr/local/dhss-solution/dhss-web/config/ \
-v /etc/hosts:/etc/hosts \
-v /etc/localtime:/etc/localtime \
-w /usr/local/dhss-solution/dhss-web \
--network=host \
-p 8080:8080 \
--restart=always \
openjdk:8u131-jre \
java -jar ./dhss-web.jar 
