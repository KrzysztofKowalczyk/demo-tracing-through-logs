package demo.export

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import io.jaegertracing.internal.JaegerSpan
import io.jaegertracing.internal.Reference
import io.jaegertracing.internal.exceptions.SenderException
import io.jaegertracing.spi.Sender
import io.jaegertracing.thriftjava.SpanRefType
import io.opentracing.References
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Canonical
@CompileStatic
class LogTraceSender implements Sender {
    static final Logger LOG = LoggerFactory.getLogger(LogTraceSender)

    @Override
    int append(JaegerSpan span) throws SenderException {
        LOG.info(null, [
            jaegerSpan: extractFields(span)
        ])
        return 1
    }

    @Override
    int flush() throws SenderException {
        return 0
    }

    @Override
    int close() throws SenderException {
        return 0
    }

    Map extractFields(JaegerSpan span) {
        def context = span.context()

        def oneChildOfParent = span.references.size() == 1 &&
            References.CHILD_OF == span.references.first().type

        def result = [
            traceIdLow: context.traceIdLow,
            traceIdHigh: context.traceIdHigh,
            spanId: context.spanId,
            parentId: oneChildOfParent ? context.parentId : 0,
            operationName: span.operationName,
            flags: context.flags,
            start: span.start,
            duration: span.duration,
            tags: span.tags,
            process: [
                serviceName: span.tracer.serviceName,
                tags: span.tracer.tags()
            ]
        ] as Map


        if(!oneChildOfParent) {
            result.references = buildReferences(span.references)
        }

        //TODO: logs

        return result
    }

    static List<? extends Map> buildReferences(List<Reference> references) {
         references.collect { reference ->
            def thriftRefType = References.CHILD_OF == reference.type
                ? SpanRefType.CHILD_OF
                : SpanRefType.FOLLOWS_FROM

            [
                refType: thriftRefType.toString(),
                traceIdLow: reference.spanContext.traceIdLow,
                traceIdHigh: reference.spanContext.traceIdHigh,
                spanId: reference.spanContext.spanId
            ]
        }
    }
}