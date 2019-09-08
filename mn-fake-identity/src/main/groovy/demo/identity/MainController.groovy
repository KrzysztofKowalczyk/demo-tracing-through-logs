package demo.identity

import groovy.transform.CompileStatic
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.tracing.annotation.NewSpan
import io.micronaut.tracing.annotation.SpanTag

import javax.inject.Inject

@CompileStatic
@Controller("/")
class MainController {

    @Inject Database database

    @NewSpan
    @Get("/validateToken/{token}")
    Map validateToken(@SpanTag String token) { // don't do that in real life !
        def user = database.findUserByToken(token)
        Thread.sleep(13)
        if(user) {
            [
                valid: true,
                userId: user.userId
            ]
        }
    }
}
