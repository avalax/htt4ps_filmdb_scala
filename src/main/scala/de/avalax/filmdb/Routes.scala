package de.avalax.filmdb

import cats.effect.{Concurrent, IO}
import cats.effect.kernel.Async
import cats.implicits.*
import de.avalax.filmdb.domain.model.Film
import de.avalax.filmdb.domain.service.FilmService
import io.circe.Json
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, UrlForm}
import io.circe.syntax.*

object Routes:
  def filmDbRoutes(filmDb: FilmService[IO]): HttpRoutes[IO] =
    implicit val decoder: EntityDecoder[IO, Film] = jsonOf[IO, Film]
    val dsl = new Http4sDsl[IO] {}
    import dsl.*
    HttpRoutes.of[IO] {
      case GET -> Root =>
        for {
          films <- filmDb.allFilms
          resp <- Ok(films)
        } yield resp
        // TODO Fehlerbehandlung
      case req@POST -> Root / "film" =>
        for {
          body <- req.as[Film]
          repoResult <- filmDb.addFilm(body)
          resp <- {
            Ok(repoResult.asJson)
          }
        } yield resp
    }
