package demo.points

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.tracing.annotation.NewSpan

import javax.inject.Inject

@Controller("/")
class MainController {

    @Inject IdentityClient identityClient
    @Inject FakeDatabase fakeDatabase

    @NewSpan
    @Get("/points/{token}")
    Map validateToken(String token) {
        def validatedUser = identityClient.validateToken(token)
        [
            points: fakeDatabase.findPointsByUserId(validatedUser.userId)
        ]
    }
}