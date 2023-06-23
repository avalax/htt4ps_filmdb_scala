package de.avalax.filmdb

import cats.effect.{IO, IOApp}
import de.avalax.filmdb.domain.model.FilmRepository.FilmMockRepository

object Main extends IOApp.Simple:
  private val filmRepository = new FilmMockRepository()
  val run: IO[Nothing] = Server.run(filmRepository)
