FROM eclipse-temurin:17

COPY ../target/helio-publisher-*jar /usr/src/helio-publisher/helio-publisher.jar

WORKDIR /usr/src/helio-publisher

EXPOSE 9000
EXPOSE 4567

CMD ["java", "-jar", "./helio-publisher.jar"]
