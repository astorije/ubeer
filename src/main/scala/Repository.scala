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

  def breweries(cityOption: Option[String]): List[Brewery] = cityOption match {
    case None => Repository.breweries
    case Some(city) => Repository.breweries.filter(_.city == city)
  }

  def style(id: Int): Option[Style] = styles.find(_.id == id)
  def category(id: Int): Option[Category] = categories.find(_.id == id)
  def beer(id: Int): Option[Beer] = beers.find(_.id == id)
  def brewery(id: Int): Option[Brewery] = breweries(None).find(_.id == id)

  def stylesByCategory(categoryId: Int): List[Style] =
    styles.filter(_.category_id == categoryId)

  def beersByStyle(styleId: Int): List[Beer] =
    beers.filter(_.style_id == styleId)

  def beersByBrewery(breweryId: Int): List[Beer] =
    beers.filter(_.brewery_id == breweryId)
}
