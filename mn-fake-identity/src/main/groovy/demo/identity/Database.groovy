package demo.identity

import io.micronaut.tracing.annotation.NewSpan
import io.micronaut.tracing.annotation.SpanTag

class Database {

    @NewSpan
    Map findUserByToken(@SpanTag String token) { // don't do that in real life !
        Thread.sleep(123)
        [ userId: "user1" ]
    }
}
