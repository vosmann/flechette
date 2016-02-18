FROM zalando/openjdk:8u45-b14-5

WORKDIR /opt/flechette

EXPOSE 11000

ENV VERSION 1.0-SERVER-SNAPSHOT

ADD lib lib
ADD target/flechette-server-$VERSION.jar flechette-server.jar
RUN chmod -R 770 /opt/flechette

ENTRYPOINT ["java", "-jar", "flechette-server.jar"]
