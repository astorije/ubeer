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
  description: String,
  latitude: String,
  longitude: String
) {
  def beers(allBeers: List[Beer]) = allBeers.filter(_.brewery_id == id)
}

case class Beer(
  id: Int,
  brewery_id: Int,
  name: String,
  abv: Double,
  description: String,
  style: String,
  category: String
) {
  def brewery(allBreweries: List[Brewery]) = allBreweries.find(_.id == brewery_id).get
}
