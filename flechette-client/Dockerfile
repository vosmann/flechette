FROM zalando/openjdk:8u45-b14-5

WORKDIR /opt/flechette

EXPOSE 22000

ENV VERSION 1.0-CLIENT-SNAPSHOT

ADD lib lib
ADD target/flechette-client-$VERSION.jar flechette-client.jar
RUN chmod -R 770 /opt/flechette

ENTRYPOINT ["java", "-jar", "flechette-client.jar"]
