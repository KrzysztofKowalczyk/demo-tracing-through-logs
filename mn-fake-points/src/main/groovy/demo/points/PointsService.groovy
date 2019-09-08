package demo.points

import demo.export.LogTraceSender
import groovy.transform.CompileStatic
import io.jaegertracing.Configuration
import io.jaegertracing.spi.Sender
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
    Configuration.SenderConfiguration jaegerSenderConfiguration() {
        def sender = new LogTraceSender()

        new Configuration.SenderConfiguration() {
            @Override
            Sender getSender() {
                sender
            }
        }
    }
}