package de.avalax.filmdb.domain.service

import cats.Applicative
import de.avalax.filmdb.domain.model.{Film, FilmRepository}
import de.avalax.filmdb.domain.service.FilmService.*

trait FilmService[F[_]]:
  def addFilm(film: Film): F[Film]

  def allFilms: F[Seq[Film]]

object FilmService:
  def impl[F[_] : Applicative](filmRepository: FilmRepository[F]): FilmService[F] = new FilmService[F]:
    override def allFilms: F[Seq[Film]] = filmRepository.loadAll()

    def addFilm(film: Film): F[Film] = filmRepository.save(film)
