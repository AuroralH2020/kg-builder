FROM eclipse-temurin:17

COPY ./helio-publisher-*jar /usr/src/helio-publisher/helio-publisher.jar
RUN mkdir /usr/src/helio-publisher/views
COPY ./views/ /usr/src/helio-publisher/views/
WORKDIR /usr/src/helio-publisher

EXPOSE 9000
EXPOSE 4567

CMD ["java", "-jar", "./helio-publisher.jar", "-d", "-config.port=4567", "-publisher.port=9000"]
