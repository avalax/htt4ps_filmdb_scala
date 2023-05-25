package de.avalax.filmdb.domain.model

import cats.implicits.*
import io.circe.*
import io.circe.Decoder.Result
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

import scala.util.chaining.scalaUtilChainingOps

final case class Film(name: String) extends AnyVal

object Film:
  given Decoder[Film] = (c: HCursor) =>
    c.downField("name").as[String].map(Film(_))

  given Encoder[Film] = (film: Film) => Json.obj("name" -> Json.fromString(film.name))

  given Encoder[Seq[Film]] = (films: Seq[Film]) =>
    films.map { film =>
      Json.obj(("name", Json.fromString(film.name)))
    }.pipe(Json.arr)

  given[F[_]]: EntityEncoder[F, Seq[Film]] =
    jsonEncoderOf[F, Seq[Film]]
