import spray.json._
import DefaultJsonProtocol._
import sangria.execution.deferred.{DeferredResolver, Fetcher, Relation, RelationIds}

import scala.concurrent.Future.successful

class Repository extends Fetchers {
  val categories = loadFile[Category]("categories.json")
  val styles = loadFile[Style]("styles.json")
  val breweries = loadFile[Brewery]("breweries.json")
  val beers = loadFile[Beer]("beers.json")

  private def loadFile[T : JsonFormat](fileName: String): List[T] =
    io.Source.fromResource(fileName)("UTF-8").mkString.parseJson.convertTo[List[T]]
}

trait Fetchers {
  val categoryFetcher = Fetcher.caching(
    (repo: Repository, ids: Seq[Int]) =>
      successful(repo.categories.filter(c => ids contains c.id)))

  val styleByCategory = Relation("byCategory", (s: Style) => Seq(s.category_id))

  val styleFetcher = Fetcher.relCaching(
    (repo: Repository, ids: Seq[Int]) =>
      successful(repo.styles.filter(s => ids contains s.id)),
    (repo: Repository, ids: RelationIds[Style]) =>
      successful(repo.styles.filter(s => ids(styleByCategory) contains s.category_id)))

  val breweryFetcher = Fetcher.caching(
    (repo: Repository, ids: Seq[Int]) =>
      successful(repo.breweries.filter(b => ids contains b.id)))

  val beerByBrewery = Relation("byBrewery", (b: Beer) => Seq(b.brewery_id))
  val beerByStyle = Relation("byStyle", (b: Beer) => Seq(b.style_id))

  val beerFetcher = Fetcher.relCaching(
    (repo: Repository, ids: Seq[Int]) =>
      successful(repo.beers.filter(b => ids contains b.id)),
    (repo: Repository, ids: RelationIds[Beer]) => {
      val breweryIds = ids.get(beerByBrewery) getOrElse Nil
      val styleIds = ids.get(beerByStyle) getOrElse Nil

      successful(repo.beers.filter(b => breweryIds.contains(b.brewery_id) || styleIds.contains(b.style_id)))
    })

  val deferredResolver = DeferredResolver.fetchers(
    categoryFetcher, styleFetcher, breweryFetcher, beerFetcher)
}
