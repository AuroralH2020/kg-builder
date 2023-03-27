FROM eclipse-temurin:17

COPY ./kg-builder-*jar /usr/src/kgb/kg-builder.jar
WORKDIR /usr/src/kgb
RUN mkdir ./views
RUN mkdir ./db


EXPOSE 9000
EXPOSE 4567

CMD ["java", "-jar", "./kg-builder.jar"]
