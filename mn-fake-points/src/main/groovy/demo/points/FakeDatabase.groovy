package demo.points

import io.micronaut.tracing.annotation.NewSpan
import io.micronaut.tracing.annotation.SpanTag

class FakeDatabase {

    @NewSpan
    Long findPointsByUserId(@SpanTag String userId) {
        Thread.sleep(73)
        return 10_013
    }
}
