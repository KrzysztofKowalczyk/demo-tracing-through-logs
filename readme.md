# Demo of tracing with logs

This code run 2 separate service, where one is depending on another.
Both service dump tracing information to log file in json format.
This file is read by another process which parse it and exports traces to Jaeger through HTTP port.

It is a PoC and could be extended to import data from Splunk or ElasticSearch instead.

# How to run the demo 

First run:

```sh
docker run -d --name jaeger \
   -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 \
   -p 5775:5775/udp \
   -p 6831:6831/udp \
   -p 6832:6832/udp \
   -p 5778:5778 \
   -p 16686:16686 \
   -p 14268:14268 \
   -p 9411:9411 \
   jaegertracing/all-in-one:1.14
```

This runs all-in-one Jaeger in memory docker image that can be accessed at http://localhost:16686/search

Next run:

```sh
./gradlew startService
```

Which would start 3 separate processes:
- a fake points service - which you can call with http://localhost:8080/points/someAccessToken
- a fake identity service - which you can call with http://localhost:8086/validateToken/anotherAccessToken
- a groovy script continously uploading traces from a file

It will keep running until Enter is clicked.
Some data should be now available on http://localhost:16686/search.

Gradle will keep those processes running until enter is clicked.
You can call services api (try the links above) and see how the results look like in Jaeger.
Fortunately Identity is really relaxed and will accept any access token. 

Services will append traces to log file `log/trace.log` in root folder.
A Groovy script will be loading traces from that file and uploading it to Jaeger thrift http endpoint on port 14268.

Because Micronaut provides Jaeger integration, context is properly propagated between those services through http calls without any extra effort. Some frameworks and protocols require manual context propagation.

## Notes 

- I'm serializing JaegerSpan not OpenTracing Span and I use Jaeger Sender instead of some standard OpenTracing class
- it is not using any known Json format (i.e. there are some formats in the wild: Jaeger Protobuf Json format, Jaeger ElasticSearch)
- serialising with ElasticSearch compatible Jaeger format could make it easy to implement Jaeger on top of Splunk?
- by the time of writing OpenTelemetry does not expose any artifacts so it was not feasible to use it yet

## Todo

- move to OpenTelemetry once available
    - it has implementation, not only api and would allow to use standard classes instead of Jaeger
    - OpenTelemetry has nice concept of exporters which should work well for that
    - OpenTelemetry has shim for OpenTracing
    - OpenTelemetry has Jaeger protobuf exporter
 - Splunk based exporter ?
 - Splunk based Jaeger UI ?
