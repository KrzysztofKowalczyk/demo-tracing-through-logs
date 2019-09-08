package demo.points

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

@Client('http://localhost:8086')
interface IdentityClient {

    @Get("/validateToken/{token}")
    Map validateToken(String token)
}
