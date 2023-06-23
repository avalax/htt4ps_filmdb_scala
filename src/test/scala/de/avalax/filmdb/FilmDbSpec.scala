package de.avalax.filmdb

import cats.effect.IO
import de.avalax.filmdb.domain.model.FilmRepository
import de.avalax.filmdb.domain.model.FilmRepository.FilmMockRepository
import de.avalax.filmdb.domain.service.FilmService
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*

class FilmDbSpec extends CatsEffectSuite:

  import FilmDbSpec.*

  test("showAllFilms returns status code 200") {
    implicit val repository: FilmRepository[IO] = new FilmMockRepository()

    assertIO(showAllFilms.map(_.status), Status.Ok)
  }

  test("Ohne Filme zeige eine leere Liste") {
    implicit val repository: FilmRepository[IO] = new FilmMockRepository()

    assertIO(showAllFilms.flatMap(_.as[String]), "[]")
  }

  test("Ein Film anlegen") {
    implicit val repository: FilmRepository[IO] = new FilmMockRepository()

    assertIO(addFilm.flatMap(_.as[String]), """{"name":"Alice"}""")
  }

  test("Beim Speichern eines invaliden Filmes eine 400 zurueckgeben") {
    implicit val repository: FilmRepository[IO] = new FilmMockRepository()

    assertIO(addInvalidFilm.map(_.status.code), 400)
  }

  test("Einen Film anzeigen") {
    implicit val repository: FilmRepository[IO] = new FilmMockRepository()

    assertIO(addFilm >> showAllFilms.flatMap(_.as[String]), """[{"name":"Alice"}]""")
  }


object FilmDbSpec {
  private[this] def showAllFilms(implicit repository: FilmRepository[IO]): IO[Response[IO]] =
    val getFilms = Request[IO](Method.GET, uri"/")
    val filmDb = FilmService.impl[IO](repository)
    Routes.filmDbRoutes(filmDb).orNotFound(getFilms)

  private[this] def addFilm(implicit repository: FilmRepository[IO]): IO[Response[IO]] =
    val addFilm = Request[IO](Method.POST, uri"/film")
      .withEntity("""{"name": "Alice"}""")
    val filmDb = FilmService.impl[IO](repository)
    Routes.filmDbRoutes(filmDb).orNotFound(addFilm)

  private[this] def addInvalidFilm(implicit repository: FilmRepository[IO]): IO[Response[IO]] =
    val addFilm = Request[IO](Method.POST, uri"/film")
      .withEntity("""{"name": ""}""")
    val filmDb = FilmService.impl[IO](repository)
    Routes.filmDbRoutes(filmDb).orNotFound(addFilm)

}
