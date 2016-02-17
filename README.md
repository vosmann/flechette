# Flechette

Flechette is a Java project aimed at simplifying tests in a server-client architecture.
Flechette allows the creation of a server application and a client application with
built-in performance measurement. The metrics can be retrieved as JSON from the `/metrics` and `/system-metrics`
endpoints.
Both server and client can be scaled horizontally.

An overview of different performance factors can be seen in the following image.
![Performance factors in client-server systems](client-server-performance-factors.png)

These factors are not completely comprehensive and include a bit of overlap, but do describe
the hypothesized causes of bad performance that was inspiration for writing Fletchette.

[STUPS Senza](http://stups.readthedocs.org/en/latest/components/senza.html) templates
are provided for deployment on AWS.

## Metrics
The metrics provided through the `/metrics` or `/system-metrics` include:

* request rate,
* response rate,
* CPU load,
* JVM and system memory consumption,
* TCP connections info.

### Getting the Sigar system metrics library
Sigar exposes plenty of system information through a Java API. However, it does depend on a on some native binaries.
These binaries are downloaded and placed into `flechette-server/libs` or `flechette-client/libs`. This is the location
the Java code expects it to be in.

    cd flechette
    curl http://netix.dl.sourceforge.net/project/sigar/sigar/1.6/hyperic-sigar-1.6.4.zip > hyperic-sigar-1.6.4.zip
    unzip hyperic-sigar-1.6.4.zip
    cp -R hyperic-sigar-1.6.4/sigar-bin/lib flechette-server/
    cp -R hyperic-sigar-1.6.4/sigar-bin/lib flechette-client/
    rm -r hyperic-sigar-1.6.4

## Server
The server is a Spring Boot application. By default, Fletchette provides a default NOP service that just
returns a string response on the root path `/`. Additional implementations for this interface can be provided.
For example, more complex operations such as database reads or writes or expensive calculations we want run stress tests
on.

The embedded server, Tomcat by default, should be configured for every test.

### Building

    cd flechette-server
    mvn clean package
    docker build --no-cache -t vosmann/flechette-server:1.0-SNAPSHOT .
    docker push vosmann/flechette-server:1.0-SNAPSHOT

### Running

    java -jar target/flechette-server-1.0-SNAPSHOT.jar
    docker run -u 998 -p 11000:11000 vosmann/flechette-server:1.0-SNAPSHOT
    senza create flechette-server.yaml SERVER1 1.0-SNAPSHOT

## Client
The client is a Java application providing code for running stress tests on a server.
It allows specifying:

* the call to be made to the server,
* call timeout,
* frequency at which the calls should be executed,
* initial delay,
* thread count and
* ramp-up period.

### Building

    cd flechette-client
    mvn clean install
    docker build --no-cache -t vosmann/flechette-client:1.0-SNAPSHOT .
    docker push vosmann/flechette-client:1.0-SNAPSHOT

## Running

    java -jar target/flechette-client-1.0-SNAPSHOT.jar # Uses application.properties defaults.
    docker run -u 998 -p 11000:11000 \
               -e URL=http://192.168.99.100:11000/ \
               -e THREAD_COUNT=50 \
               -e RAMPUP_TIME=5 -e RAMPUP_TIMEUNIT=MINUTES \
               -e EXECUTION_PERIOD=2000 -e EXECUTION_PERIOD_TIMEUNIT=MILLISECONDS \
               vosmann/flechette-client:1.0-SNAPSHOT
    senza create flechette-client.yaml CLIENT1 \
                                      Url=http://localhost:11000 \
                                      ThreadCount=100 \
                                      RampUpTime=5 RampUpTimeUnit=MINUTES \
                                      ExecutionPeriod=2000 ExecutionPeriodTimeUnit=MILLISECONDS

##### Disabling services running

If by any chance you have a service, e.g. Tomcat, starting on the same port
Spring Boot tries to bind to by default, you can stop it from running on startup
with something like:

    sudo update-rc.d tomcat7 disable
