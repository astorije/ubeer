import spray.json._
import DefaultJsonProtocol._

object Repository {
  println("Data loaded:")

  val categorySource = io.Source.fromFile("src/main/resources/categories.json")("UTF-8").mkString.parseJson
  implicit val categoryFormat = jsonFormat2(Category.apply)
  val categories = categorySource.convertTo[List[Category]]

  println(s"  - ${categories.length} categories")

  val styleSource = io.Source.fromFile("src/main/resources/styles.json")("UTF-8").mkString.parseJson
  implicit val styleFormat = jsonFormat3(Style.apply)
  val styles = styleSource.convertTo[List[Style]]

  println(s"  - ${styles.length} styles")

  val brewerySource = io.Source.fromFile("src/main/resources/breweries.json")("UTF-8").mkString.parseJson
  implicit val breweryFormat = jsonFormat10(Brewery.apply)
  val breweries = brewerySource.convertTo[List[Brewery]]

  println(s"  - ${breweries.length} breweries")

  val beerSource = io.Source.fromFile("src/main/resources/beers.json")("UTF-8").mkString.parseJson
  implicit val beerFormat = jsonFormat6(Beer.apply)
  val beers = beerSource.convertTo[List[Beer]]

  println(s"  - ${beers.length} beers")
}

class Repository {
  import Repository._

  val categories = Repository.categories
  val styles = Repository.styles
  val beers = Repository.beers
  val breweries = Repository.breweries

  def style(id: Int): Option[Style] = Repository.styles.find(_.id == id)
  def category(id: Int): Option[Category] = Repository.categories.find(_.id == id)
  def beer(id: Int): Option[Beer] = Repository.beers.find(_.id == id)
  def brewery(id: Int): Option[Brewery] = Repository.breweries.find(_.id == id)
}
