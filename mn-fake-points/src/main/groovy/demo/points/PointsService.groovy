package demo.points

import demo.export.LogTraceReporter
import groovy.transform.CompileStatic
import io.jaegertracing.internal.samplers.ConstSampler
import io.jaegertracing.spi.Reporter
import io.jaegertracing.spi.Sampler
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.runtime.Micronaut

@CompileStatic
@Factory
class PointsService {

    static void main(String[] args) {
        Micronaut.run()
    }

    @Bean
    Reporter reporter() {
        new LogTraceReporter()
    }

    @Bean
    Sampler sampler(){
        new ConstSampler(true)
    }
}