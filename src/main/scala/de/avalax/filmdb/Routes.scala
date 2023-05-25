package de.avalax.filmdb

import cats.effect.Concurrent
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
  def filmDbRoutes[F[_] : Concurrent](filmDb: FilmService[F]): HttpRoutes[F] =
    implicit val decoder: EntityDecoder[F, Film] = jsonOf[F, Film]
    val dsl = new Http4sDsl[F] {}
    import dsl.*
    HttpRoutes.of[F] {
      case GET -> Root =>
        for {
          films <- filmDb.allFilms
          resp <- Ok(films)
        } yield resp
      case req@POST -> Root / "film" =>
        for {
          body <- req.as[Film]
          repoResult <- filmDb.addFilm(body)
          resp <- {
            Ok(repoResult.asJson)
          }
        } yield resp
    }
