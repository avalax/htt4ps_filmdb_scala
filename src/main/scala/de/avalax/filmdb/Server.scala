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

  def run[F[_]: Async](filmRepository: FilmRepository[F]): F[Nothing] = {
    val filmDb = FilmService.impl[F](filmRepository)
    val httpApp = Routes.filmDbRoutes[F](filmDb).orNotFound
    // With Middlewares in place
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    EmberServerBuilder.default[F]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(finalHttpApp)
      .build
      .map(_ => ())
  }.useForever
