case class Brewery(
  id: Int,
  name: String,
  address: String,
  city: String,
  state: String,
  code: String,
  country: String,
  phone: String,
  website: String,
  description: String
) {
  def beers(allBeers: List[Beer]): List[Beer] =
    allBeers.filter(_.brewery_id == id)
}

case class Beer(
  id: Int,
  brewery_id: Int,
  style_id: Int,
  name: String,
  abv: Double,
  description: String
) {
  def brewery(allBreweries: List[Brewery]): Brewery =
    allBreweries.find(_.id == brewery_id).get
}
