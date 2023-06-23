package de.avalax.filmdb

import cats.effect.{Async, IO}
import cats.syntax.all.*
import com.comcast.ip4s.*
import de.avalax.filmdb.domain.model.FilmRepository
import de.avalax.filmdb.domain.model.FilmRepository.FilmMockRepository
import de.avalax.filmdb.domain.service.FilmService
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.Logger

object Server:

  def run(filmRepository: FilmRepository[IO]): IO[Nothing] = {
    val filmDb = FilmService.impl[IO](filmRepository)
    val httpApp = Routes.filmDbRoutes(filmDb).orNotFound
    // With Middlewares in place
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    EmberServerBuilder.default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(finalHttpApp)
      .build
      .map(_ => ())
  }.useForever
