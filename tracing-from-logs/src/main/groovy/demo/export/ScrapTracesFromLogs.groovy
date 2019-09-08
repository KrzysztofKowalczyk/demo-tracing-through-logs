package demo.export


import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.transform.Canonical
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import io.jaegertracing.thrift.internal.reporters.protocols.JaegerThriftSpanConverter
import io.jaegertracing.thrift.internal.senders.HttpSender
import io.jaegertracing.thriftjava.Process
import io.jaegertracing.thriftjava.Span
import io.jaegertracing.thriftjava.SpanRef
import io.jaegertracing.thriftjava.SpanRefType

/**
 * Continuously reads traces from log/trace.log file and sends to Jaeger on http://localhost:14268 http thrift port.
 * You can start jaeger all in one docker image to test it as per readme.md.
 * Running this script multiple times over the same content would cause duplicates.
 */
@CompileStatic
class ScrapTracesFromLogs {
    static JsonSlurper slurper = new JsonSlurper()
        .setType(JsonParserType.INDEX_OVERLAY)

    static void main(String[] args) {
        //TODO: hardcoded
        def sender = new HttpSender.Builder("http://localhost:14268/api/traces")
            .build()

        //TODO: hardcoded
        File file = new File("log/trace.log")
        while(!file.exists()) {
            Thread.sleep(2000)
        }
        println "Trace file found $file.canonicalPath"

        file.withReader {
            while (true) {
                //TODO: quite inefficient, sender supports sending batches where I send one at a time
                // sender assumes only one process exist hence sending data of different services
                // can share one process data depending on api usage, send assigns one per batch

                def line = it.readLine()
                if (line) {
                    def ps = parseLine(line)
                    sender.send(ps.process, [ps.span])
                } else {
                    Thread.sleep(1000)
                }
            }
        }
    }

    @CompileDynamic
    static ProcessAndSpan parseLine(String json) {
        def logLine = slurper.parseText(json)
        def s = logLine.jaegerSpan

        def span = new Span (
            s.traceIdLow,
            s.traceIdHigh,
            s.spanId,
            s.parentId,
            s.operationName,
            s.flags,
            s.start,
            s.duration
        )

        if(s.references) {
            span.references  = s.references.collect {
                new SpanRef(
                    it.refType as SpanRefType,
                    it.traceIdLow,
                    it.traceIdHigh,
                    it.spanId
                )
            }
        }

        if(s.tags) {
            span.tags = JaegerThriftSpanConverter.buildTags(s.tags)
        }

        Process process
        if(s.process) {
            process = new Process(s.process.serviceName as String)
            process.tags = JaegerThriftSpanConverter.buildTags(s.process.tags)
        } else{
            process = new Process("unknown")
        }

        new ProcessAndSpan(process, span)
    }
}

@Canonical
@CompileStatic
class ProcessAndSpan {
    final Process process
    final Span span
}