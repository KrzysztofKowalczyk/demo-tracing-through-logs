# Demo of tracing with logs

This code run 2 separate can dump tracing info to logs in json format that later can be 
read from file / Splunk / ElasticSearch and imported to Jaeger.

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

This runs all-in--one Jaeger in memory docker image that can be accessed
at http://localhost:16686/search

Next run:

```sh
./gradlew startServices
```

Which would start 3 separate processes:
- fake points service - which you can call with http://localhost:8080/points/{string access token}
- fake identity service - which you can call with http://localhost:8086/validate/{string access token}
- groovy script uploading traces 

Gradle will keep those processes running until enter is clicked.
You can call services api and see how results look like Jaeger.
Fortunately Identity is really relaxed and will accept any access token. 

The services will append traces to log file `log/trace.log` in root folder.
Groovy script will be loading those traces from the same file and uploading it to jaeger docker image.

Because Micronaut provides Jaeger integration, context is properly propagated through http clients and http servers without 
any extra effort. Some frameworks and protocols require manual context propagation.

## Notes 

- I'm serializing JaegerSpan not OpenTracing Span and I use Jaeger Sender instead of some standard OpenTracing class
- it is not using any known Json format (i.e. there are some formats in the wild: Jaeger Protobuf Json format, Jaeger ElasticSearch)
- serialising with ElasticSearch compatible Jaeger format could make it easy to implement Jaeger on top of Splunk?
- by the time of writing OpenTelemetry does not expose any artifacts so it was not feasible to use it yet

## Todo

- add process data
- add tags
- move to OpenTelemetry once available
    - it has implementation, not only api and would allow to use standard classes instead of Jaeger
    - OpenTelemetry has nice concept of exporters which should work well for that
    - OpenTelemetry has shim for OpenTracing
    - OpenTelemetry has Jaeger protobuf exporter