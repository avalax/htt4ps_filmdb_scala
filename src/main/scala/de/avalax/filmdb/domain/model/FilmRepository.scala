package de.avalax.filmdb.domain.model

import cats.effect.IO
import de.avalax.filmdb.domain.model.Film

trait FilmRepository[F[_]]:
  def loadAll(): F[Seq[Film]]

  def save(film: Film): F[Film]

object FilmRepository:
  class FilmMockRepository extends FilmRepository[IO]:
    var films: Seq[Film] = Seq.empty

    override def loadAll(): IO[Seq[Film]] =
      IO(films)

    override def save(film: Film): IO[Film] = {
      if (film.name == "") {
        IO.raiseError(new Exception("doof"))
      }
      else {
        IO {
          films = films :+ film
          film
        }
      }
    }