import spray.json._
import DefaultJsonProtocol._

object Repository {
  println("Data loaded:")

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

  val beers = Repository.beers
  val breweries = Repository.breweries

  def beer(id: Int): Option[Beer] = Repository.beers.find(_.id == id)
  def brewery(id: Int): Option[Brewery] = Repository.breweries.find(_.id == id)
}
