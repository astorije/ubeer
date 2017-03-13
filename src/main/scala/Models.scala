case class Category(
  id: Int,
  name: String
) {
  def styles(allStyles: List[Style]): List[Style] =
    allStyles.filter(_.category_id == id)
}

case class Style(
  id: Int,
  category_id: Int,
  name: String
) {
  def beers(allBeers: List[Beer]): List[Beer] =
    allBeers.filter(_.style_id == id)

  def category(allCategories: List[Category]): Category =
    allCategories.find(_.id == category_id).get
}

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

  def style(allStyles: List[Style]): Style =
    allStyles.find(_.id == style_id).get
}
